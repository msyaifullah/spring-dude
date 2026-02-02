package com.yyggee.eggs.controller.ds1;

import com.yyggee.eggs.constants.Endpoints;
import com.yyggee.eggs.dto.SigninResponse;
import com.yyggee.eggs.dto.UserRequest;
import com.yyggee.eggs.dto.UserResponse;
import com.yyggee.eggs.dto.common.BaseApiResponse;
import com.yyggee.eggs.exceptions.KitchenException;
import com.yyggee.eggs.model.ds1.User;
import com.yyggee.eggs.service.ds1.UserService;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(Endpoints.BASE_URL + "auth")
public class UserController {

  @Autowired private UserService userService;

  @Autowired private ModelMapper modelMapper;

  @PostMapping(Endpoints.USER_SIGN_IN)
  public ResponseEntity<BaseApiResponse> login(
      @RequestHeader(value = "Authorization", required = false) String basicToken,
      @RequestParam(value = "token", required = false) String bearerToken) {
    if (null == basicToken && null == bearerToken)
      throw new KitchenException("Required authorizations params", HttpStatus.UNPROCESSABLE_ENTITY);
    log.info("Hot reload test at {}", new Date());
    SigninResponse token =
        (basicToken != null)
            ? userService.signin(basicToken)
            : userService.signinToken(bearerToken);

    if (null == token) {
      throw new KitchenException("Invalid missing authorizations", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    return new ResponseEntity<>(new BaseApiResponse("OK", "Access Token", token), HttpStatus.OK);
  }

  @PostMapping(Endpoints.USER_SIGN_UP)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<BaseApiResponse> signup(@RequestBody UserRequest user) {
    UserResponse userResponse =
        modelMapper.map(userService.signup(modelMapper.map(user, User.class)), UserResponse.class);
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "User registered", userResponse), HttpStatus.OK);
  }

  @DeleteMapping(Endpoints.USER_SIGN_OUT)
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  public ResponseEntity<BaseApiResponse> signout(
      @RequestHeader("Authorization") String bearerToken) {
    Boolean isLogin = userService.signout(bearerToken);
    if (!isLogin) throw new KitchenException("Signout failed", HttpStatus.UNPROCESSABLE_ENTITY);

    return new ResponseEntity<>(new BaseApiResponse("OK", "See you later"), HttpStatus.OK);
  }

  @DeleteMapping(Endpoints.USER_SESSION_USERNAME)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<BaseApiResponse> delete(@PathVariable String username) {
    userService.delete(username);
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "User name deleted", username), HttpStatus.OK);
  }

  @GetMapping(Endpoints.USER_SESSION_USERNAME)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<BaseApiResponse> search(@PathVariable String username) {
    UserResponse userResponse = modelMapper.map(userService.search(username), UserResponse.class);
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "User name search", userResponse), HttpStatus.OK);
  }

  @GetMapping(Endpoints.USER_SESSION)
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  public ResponseEntity<BaseApiResponse> whoami(
      @RequestHeader("Authorization") String bearerToken) {
    UserResponse userResponse =
        modelMapper.map(userService.whoami(bearerToken), UserResponse.class);
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "User Session", userResponse), HttpStatus.OK);
  }

  @GetMapping(Endpoints.USER_REFRESH)
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  public ResponseEntity<BaseApiResponse> refresh(
      @RequestHeader("Authorization") String bearerToken) {
    SigninResponse token = userService.refresh(bearerToken);
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "Extend Access Token", token), HttpStatus.OK);
  }
}
