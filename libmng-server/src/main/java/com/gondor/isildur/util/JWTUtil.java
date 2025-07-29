package com.gondor.isildur.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;

public class JWTUtil {
  private static final long TOKEN_EXPIRED_TIME = 30 * 24 * 60 * 60 * 1000L; // 修复时间单位
  private static final String JWT_SECRET = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=";

  // 直接使用Base64解码后的密钥
  private static final Key signingKey = new SecretKeySpec(
          JWT_SECRET.getBytes(StandardCharsets.UTF_8),
          SignatureAlgorithm.HS256.getJcaName()
  );

  public static String createToken(Map<String, String> claims) {
    long nowMillis = System.currentTimeMillis();
    Date exp = new Date(nowMillis + TOKEN_EXPIRED_TIME);

    return Jwts.builder()
            .setClaims(claims)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .setExpiration(exp)
            .compact();
  }

  public static Claims verifyToken(String jws)
          throws ExpiredJwtException, SignatureException, MalformedJwtException {
    return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(jws)
            .getBody();
  }
}