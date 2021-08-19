package com.yyggee.eggs.security;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.yyggee.eggs.constants.ConstantAuth;
import com.yyggee.eggs.exceptions.KitchenException;
import com.yyggee.eggs.model.ds1.Role;
import io.jsonwebtoken.*;
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
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds; // 1h

    @Value("${security.jwt.token.refresh-expire-length:15}")
    private int validityInDays; // 15D

    @Autowired
    private AppUserDetails appUserDetails;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).filter(Objects::nonNull).collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = appUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
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
        if ("".equals(token)) throw new KitchenException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new KitchenException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }

    public Claims claimToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return jws.getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new KitchenException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String createRefreshToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.setAudience("AuthDashboardSvc");

        Date now = new Date();
        Date validity = addDay(validityInDays);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
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

