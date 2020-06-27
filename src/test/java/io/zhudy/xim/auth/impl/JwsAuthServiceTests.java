/**
 * Copyright 2019-2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zhudy.xim.auth.impl;

import static io.zhudy.xim.BizCodes.C3300;
import static io.zhudy.xim.BizCodes.C3301;
import static io.zhudy.xim.BizCodes.C3302;
import static io.zhudy.xim.BizCodes.C3305;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.Keys;
import io.zhudy.xim.BizCodeException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/** @author Kevin Zou (kevinz@weghst.com) */
class JwsAuthServiceTests {

  String kid = "test";
  Key testSecret =
      Keys.hmacShaKeyFor(
          "29c5fab077c009b9e6676b2f082a7ab3b0462b41acf75f075b5a7bac5619ec81c9d8bb2e25b6d33800fba279ee492ac7d05220e829464df3ca8e00298c517764"
              .getBytes(StandardCharsets.UTF_8));
  Key illegalSecret =
      Keys.hmacShaKeyFor(
          "29c5fab077c009b9e6676b2f082a7ab3b0462b41acf75f075b5a7bac5619ec81c9d8bb2e25b6d33800fba279ee492ac7d05220e829464df3ca8e00298c517764-illegal-secret"
              .getBytes(StandardCharsets.UTF_8));
  SigningKeyResolver signingKeyResolver = new DefaultSigningKeyResolver(Map.of(kid, testSecret));

  @Test
  void authorize() {
    var token =
        Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, kid)
            .signWith(testSecret, SignatureAlgorithm.HS512)
            .setId("test")
            .compact();

    JwsAuthService authService = new JwsAuthService(signingKeyResolver);
    var p = authService.authorize(token);
    StepVerifier.create(p)
        .expectNextMatches(ac -> "test".equals(ac.uid()) && token.equals(ac.token()))
        .verifyComplete();
  }

  @Test
  void illegalToken() {
    JwsAuthService authService = new JwsAuthService(signingKeyResolver);
    var p = authService.authorize("illegal token");
    StepVerifier.create(p)
        .expectErrorMatches(e -> C3300 == ((BizCodeException) e).getBizCode())
        .verify(Duration.ofSeconds(1));
  }

  @Test
  void expiredToken() {
    var exp = Instant.now(Clock.systemDefaultZone()).minus(1, ChronoUnit.DAYS);
    var token =
        Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, kid)
            .signWith(testSecret, SignatureAlgorithm.HS512)
            .setId("test")
            .setExpiration(Date.from(exp))
            .compact();

    JwsAuthService authService = new JwsAuthService(signingKeyResolver);
    var p = authService.authorize(token);
    StepVerifier.create(p)
        .expectErrorMatches(e -> C3301 == ((BizCodeException) e).getBizCode())
        .verify(Duration.ofSeconds(1));
  }

  @Test
  void tokenNbf() {
    var nbf = Instant.now(Clock.systemDefaultZone()).plus(1, ChronoUnit.DAYS);
    var token =
        Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, kid)
            .signWith(testSecret, SignatureAlgorithm.HS512)
            .setId("test")
            .setNotBefore(Date.from(nbf))
            .compact();

    JwsAuthService authService = new JwsAuthService(signingKeyResolver);
    var p = authService.authorize(token);
    StepVerifier.create(p)
        .expectErrorMatches(e -> C3302 == ((BizCodeException) e).getBizCode())
        .verify(Duration.ofSeconds(1));
  }

  @Test
  void notFoundKid() {
    var token =
        Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, "not-found-kid")
            .signWith(testSecret, SignatureAlgorithm.HS512)
            .setId("test")
            .compact();

    JwsAuthService authService = new JwsAuthService(signingKeyResolver);
    var p = authService.authorize(token);
    StepVerifier.create(p)
        .expectErrorMatches(e -> C3305 == ((BizCodeException) e).getBizCode())
        .verify(Duration.ofSeconds(1));
  }

  @Test
  void wrongSign() {
    var token =
        Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, "not-found-kid")
            .signWith(illegalSecret, SignatureAlgorithm.HS512)
            .setId("test")
            .compact();

    JwsAuthService authService = new JwsAuthService(signingKeyResolver);
    var p = authService.authorize(token);
    StepVerifier.create(p)
        .expectErrorMatches(e -> C3305 == ((BizCodeException) e).getBizCode())
        .verify(Duration.ofSeconds(1));
  }
}
