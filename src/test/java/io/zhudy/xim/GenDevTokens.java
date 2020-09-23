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
package io.zhudy.xim;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import org.junit.jupiter.api.Test;

/** @author Kevin Zou (kevinz@weghst.com) */
class GenDevTokens {

  String kid = "dev";
  Key testSecret =
      Keys.hmacShaKeyFor(
          "48991ed9387952211883afc2392fc5332239993e8184a42888bf4123e78cd572cc18292d81d9578af50eb6ccd66c78bb71f7a6bd28a7b8b14ce7801ab75b1278"
              .getBytes(StandardCharsets.UTF_8));
  //  SigningKeyResolver signingKeyResolver = new DefaultSigningKeyResolver(Map.of(kid,
  // testSecret));

  @Test
  void genTokens() {
    List<String> uids = List.of("u1", "u2", "u3");
    System.out.println("gen-tokens =====================================================");
    uids.forEach(
        uid -> {
          var token = genToken(uid);
          System.out.println("uid: " + uid);
          System.out.println(token);
        });
    System.out.println("gen-tokens =====================================================");
  }

  private String genToken(String uid) {
    return Jwts.builder()
        .setHeaderParam(JwsHeader.KEY_ID, kid)
        .signWith(testSecret, SignatureAlgorithm.HS512)
        .setId(uid)
        .compact();
  }
}
