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
package io.zhudy.xim.auth.impl;

import static io.zhudy.xim.ConfigKeys.IM_SERVER_AUTH_JWT_SECRETS;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import java.security.Key;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * JWT Key 查找实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class DefaultSigningKeyResolver implements SigningKeyResolver {

  private final Map<String, Key> jwtSecrets;

  @Inject
  public DefaultSigningKeyResolver(@Named(IM_SERVER_AUTH_JWT_SECRETS) Map<String, Key> jwtSecrets) {
    this.jwtSecrets = jwtSecrets;
  }

  @Override
  public Key resolveSigningKey(JwsHeader header, Claims claims) {
    SignatureAlgorithm alg = SignatureAlgorithm.forName(header.getAlgorithm());
    if (!alg.isHmac()) {
      throw new IllegalArgumentException(
          "The default resolveSigningKey(JwsHeader, Claims) implementation cannot be "
              + "used for asymmetric key algorithms (RSA, Elliptic Curve).  "
              + "Override the resolveSigningKey(JwsHeader, Claims) method instead and return a "
              + "Key instance appropriate for the "
              + alg.name()
              + " algorithm.");
    }
    return lookupVerificationKey(header.getKeyId());
  }

  @Override
  public Key resolveSigningKey(JwsHeader header, String plaintext) {
    SignatureAlgorithm alg = SignatureAlgorithm.forName(header.getAlgorithm());
    if (!alg.isHmac()) {
      throw new IllegalArgumentException(
          "The default resolveSigningKey(JwsHeader, String) implementation cannot be "
              + "used for asymmetric key algorithms (RSA, Elliptic Curve).  "
              + "Override the resolveSigningKey(JwsHeader, String) method instead and return a "
              + "Key.isHmac() appropriate for the "
              + alg.name()
              + " algorithm.");
    }
    return lookupVerificationKey(header.getKeyId());
  }

  private Key lookupVerificationKey(String kid) {
    return jwtSecrets.get(kid);
  }
}
