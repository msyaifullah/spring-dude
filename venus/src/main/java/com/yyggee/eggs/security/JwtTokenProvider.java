package com.yyggee.eggs.security;

import com.yyggee.eggs.constants.ConstantAuth;
import com.yyggee.eggs.exceptions.KitchenException;
import com.yyggee.eggs.model.ds1.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  @Value("${security.jwt.token.secret-key:secret-key}")
  private String secretKeyString;

  @Value("${security.jwt.token.expire-length:3600000}")
  private long validityInMilliseconds; // 1h

  @Value("${security.jwt.token.refresh-expire-length:15}")
  private int validityInDays; // 15D

  @Autowired private AppUserDetails appUserDetails;

  private SecretKey secretKey;

  @PostConstruct
  protected void init() {
    // Ensure key is at least 256 bits (32 bytes) for HS256
    byte[] keyBytes = secretKeyString.getBytes();
    if (keyBytes.length < 32) {
      byte[] paddedKey = new byte[32];
      System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
      keyBytes = paddedKey;
    }
    secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createToken(String username, List<Role> roles) {

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    List<SimpleGrantedAuthority> authorities =
        roles.stream()
            .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return Jwts.builder()
        .subject(username)
        .claim("auth", authorities)
        .issuedAt(now)
        .expiration(validity)
        .signWith(secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = appUserDetails.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsername(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public String resolveBearerToken(String bearerToken) {
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public String resolveBearerToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public String resolveBasicToken(String basicToken) {
    if (basicToken != null && basicToken.startsWith("Basic ")) {
      String data = basicToken.substring(6);
      return new String(Base64.getDecoder().decode(data));
    }
    return null;
  }

  public boolean validateToken(String token) {
    if ("".equals(token))
      throw new KitchenException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      throw new KitchenException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
    }
  }

  public Claims claimToken(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new KitchenException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public String createRefreshToken(String username) {

    Date now = new Date();
    Date validity = addDay(validityInDays);

    return Jwts.builder()
        .subject(username)
        .audience()
        .add("AuthDashboardSvc")
        .and()
        .issuedAt(now)
        .expiration(validity)
        .signWith(secretKey)
        .compact();
  }

  public Date addDay(int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, day);
    return calendar.getTime();
  }

  public String getUsernameFromTokens(String token) {
    String basicToken = resolveBasicToken(token);
    String bearerToken = resolveBearerToken(token);

    if (!"".equals(basicToken) && basicToken != null) {
      String[] authentication = basicToken.split(":");
      return authentication[ConstantAuth.UNAME];
    }

    if (!"".equals(bearerToken) && bearerToken != null) {
      return getUsername(bearerToken);
    }
    return "";
  }
}
