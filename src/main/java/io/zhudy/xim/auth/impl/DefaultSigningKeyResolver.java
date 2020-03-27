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
