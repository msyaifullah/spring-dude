package com.yyggee.eggs.service.ds1;

import com.yyggee.eggs.constants.ConstantAuth;
import com.yyggee.eggs.dto.SigninResponse;
import com.yyggee.eggs.exceptions.KitchenException;
import com.yyggee.eggs.model.ds1.User;
import com.yyggee.eggs.repositories.ds1.UserJPARepository;
import com.yyggee.eggs.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

  @Autowired private UserJPARepository userJPARepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Autowired private AuthenticationManager authenticationManager;

  public SigninResponse signin(String basicToken) {
    try {
      String token = jwtTokenProvider.resolveBasicToken(basicToken);
      String[] authentication = token.split(":");

      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authentication[ConstantAuth.UNAME], authentication[ConstantAuth.PWORD]));

      User user = userJPARepository.findByUsername(authentication[ConstantAuth.UNAME]);

      String accessToken =
          jwtTokenProvider.createToken(authentication[ConstantAuth.UNAME], user.getRoles());
      Claims accessClaim = jwtTokenProvider.claimToken(accessToken);

      String refreshToken = jwtTokenProvider.createRefreshToken(authentication[ConstantAuth.UNAME]);

      user.setSession(refreshToken);

      userJPARepository.save(user);

      return SigninResponse.builder()
          .access_token(accessToken)
          .expired_in((accessClaim.getExpiration().getTime() - (new Date().getTime())) / 1000)
          .expired_at(accessClaim.getExpiration().getTime() / 1000)
          .type("Bearer")
          .refresh_token(refreshToken)
          .build();

    } catch (AuthenticationException e) {
      throw new KitchenException(
          "Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public User signup(User user) {
    if (!userJPARepository.existsByUsername(user.getUsername())) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userJPARepository.save(user);
    } else {
      throw new KitchenException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public Boolean signout(String bearerToken) {
    User user = whoami(bearerToken);
    user.setSession("");
    user = userJPARepository.save(user);
    return "".equals(user.getSession());
  }

  public void delete(String username) {
    userJPARepository.deleteByUsername(username);
  }

  /**
   * Searches for a user by username. Throws exception if user doesn't exist. Use this method for
   * API endpoints where you want to return 404.
   */
  public User search(String username) {
    User user = userJPARepository.findByUsername(username);
    if (user == null) {
      log.error("The user doesn't exist: {}", username);
      return null;
    }
    return user;
  }

  public User whoami(String bearerToken) {
    bearerToken = jwtTokenProvider.resolveBearerToken(bearerToken);
    Claims accessClaim = jwtTokenProvider.claimToken(bearerToken);

    User user = userJPARepository.findByUsername(accessClaim.getSubject());
    if (user == null || "".equals(user.getSession())) {
      throw new KitchenException("User session expired", HttpStatus.UNAUTHORIZED);
    }

    return user;
  }

  public SigninResponse refresh(String bearerToken) {
    bearerToken = jwtTokenProvider.resolveBearerToken(bearerToken);

    return getSigninResponse(bearerToken);
  }

  public SigninResponse signinToken(String refreshToken) {
    return getSigninResponse(refreshToken);
  }

  private SigninResponse getSigninResponse(String refreshToken) {
    Claims refreshClaim = jwtTokenProvider.claimToken(refreshToken);

    User user = userJPARepository.findByUsername(refreshClaim.getSubject());
    if (user == null || "".equals(user.getSession())) {
      throw new KitchenException("User session expired", HttpStatus.UNAUTHORIZED);
    }

    if (!refreshToken.equals(user.getSession())) {
      throw new KitchenException("User session invalid", HttpStatus.UNAUTHORIZED);
    }

    String accessToken =
        jwtTokenProvider.createToken(
            refreshClaim.getSubject(),
            userJPARepository.findByUsername(refreshClaim.getSubject()).getRoles());
    Claims accessClaim = jwtTokenProvider.claimToken(accessToken);

    return SigninResponse.builder()
        .access_token(accessToken)
        .expired_in((accessClaim.getExpiration().getTime() - (new Date().getTime())) / 1000)
        .expired_at(accessClaim.getExpiration().getTime() / 1000)
        .type("Bearer")
        .build();
  }
}
