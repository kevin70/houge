/*
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
package top.yein.tethys.core.auth;

import static top.yein.tethys.core.BizCodes.C3300;
import static top.yein.tethys.core.BizCodes.C3301;
import static top.yein.tethys.core.BizCodes.C3302;
import static top.yein.tethys.core.BizCodes.C3305;
import static top.yein.tethys.core.BizCodes.C401;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.chaos.biz.BizCodeException;

/**
 * {@link JwsAuthService} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class JwsAuthServiceTest {

  String kid = "test";
  SecretKey testSecret =
      Keys.hmacShaKeyFor(
          "29c5fab077c009b9e6676b2f082a7ab3b0462b41acf75f075b5a7bac5619ec81c9d8bb2e25b6d33800fba279ee492ac7d05220e829464df3ca8e00298c517764"
              .getBytes(StandardCharsets.UTF_8));
  SecretKey illegalSecret =
      Keys.hmacShaKeyFor(
          "29c5fab077c009b9e6676b2f082a7ab3b0462b41acf75f075b5a7bac5619ec81c9d8bb2e25b6d33800fba279ee492ac7d05220e829464df3ca8e00298c517764-illegal-secret"
              .getBytes(StandardCharsets.UTF_8));

  private JwsAuthService newJwsAuthService() {
    return newJwsAuthService(false);
  }

  private JwsAuthService newJwsAuthService(boolean anonymousEnabled) {
    return new JwsAuthService(Map.of(kid, testSecret));
  }

  @Test
  void authorize() {
    var token =
        Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, kid)
            .signWith(testSecret, SignatureAlgorithm.HS512)
            .setId("test")
            .compact();

    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate(token);
    StepVerifier.create(p)
        .expectNextMatches(ac -> "test".equals(ac.uid()) && token.equals(ac.token()))
        .verifyComplete();
  }

  @Test
  void nullToken() {
    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate(null);
    StepVerifier.create(p)
        .expectErrorMatches(e -> C401 == ((BizCodeException) e).getBizCode())
        .verify();
  }

  @Test
  void illegalToken() {
    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate("illegal token");
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

    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate(token);
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

    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate(token);
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

    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate(token);
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

    JwsAuthService authService = newJwsAuthService();
    var p = authService.authenticate(token);
    StepVerifier.create(p)
        .expectErrorMatches(e -> C3305 == ((BizCodeException) e).getBizCode())
        .verify(Duration.ofSeconds(1));
  }
}
