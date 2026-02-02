package com.yyggee.eggs;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yyggee.eggs.constants.Endpoints;
import com.yyggee.eggs.model.ds1.User;
import com.yyggee.eggs.security.JwtTokenProvider;
import com.yyggee.eggs.security.WebSecurityConfig;
import com.yyggee.eggs.service.ds1.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

// @EnableAutoConfiguration(exclude= WebSecurityConfigIgnore.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RedisTemplateMocker.class})
@AutoConfigureMockMvc
@ActiveProfiles("gitlab")
@Import(WebSecurityConfig.class)
@Disabled
public class EggsApplicationTests {

  @Autowired MockMvc mvc;

  @MockBean UserService userService;
  @MockBean PasswordEncoder passwordEncoder;
  @MockBean JwtTokenProvider jwtTokenProvider;
  @MockBean AuthenticationManager authenticationManager;

  @BeforeAll
  static void setup() {
    System.out.println("do something");
  }

  @BeforeEach
  void injectData() {
    when(userService.search("admin"))
        .thenReturn(
            new User()
                .setUsername("admin")
                .setEmail("admin@email.com")
                .setPassword("changeMeSuperman"));
    when(userService.search("client"))
        .thenReturn(
            new User()
                .setUsername("client")
                .setEmail("client@email.com")
                .setPassword("changeMeSuperman"));
  }

  @Test
  public void Should_SuccessToGet_ForCheckingHealthStatus() throws Exception {
    mvc.perform(
            get(Endpoints.BASE_URL + Endpoints.HEALTH_CHECK)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "{\"status\":\"OK\",\"message\":\"Service is running health and strong.\"}"));
  }
}
