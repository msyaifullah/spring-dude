package com.yyggee.eggs;

import com.yyggee.eggs.interceptors.RequestInterceptor;
import com.yyggee.eggs.model.ds1.Role;
import com.yyggee.eggs.model.ds1.User;
import com.yyggee.eggs.repositories.ds1.AuditorJPARepository;
import com.yyggee.eggs.security.JwtTokenProvider;
import com.yyggee.eggs.service.ds1.UserService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@SpringBootApplication
public class EggsApplication implements CommandLineRunner {

  @Autowired UserService userService;

  @Autowired AuditorJPARepository auditorJPARepository;

  @Autowired JwtTokenProvider tokenProvider;

  public static void main(String[] args) {
    SpringApplication.run(EggsApplication.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Override
  public void run(String... params) throws Exception {
    log.info("!important : add initial user ");
    // Generate initial user
    if (userService.search("admin") == null) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword("changeMeSuperman");
      admin.setEmail("admin@email.com");
      admin.setRoles(
          new ArrayList<Role>() {
            {
              add(Role.ROLE_ADMIN);
            }
          });

      userService.signup(admin);
      log.info("Added user 'admin' to db");
    } else {
      log.info("user 'admin' already created");
    }

    if (userService.search("client") == null) {
      User client = new User();
      client.setUsername("client");
      client.setPassword("changeMeRobin");
      client.setEmail("client@email.com");
      client.setRoles(
          new ArrayList<Role>() {
            {
              add(Role.ROLE_CLIENT);
              add(Role.ROLE_ACCOUNT_MANAGER);
            }
          });

      userService.signup(client);
      log.info("Added user 'client' to db");
    } else {
      log.info("user 'client' already created");
    }
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {

    return new WebMvcConfigurer() {

      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor(tokenProvider, auditorJPARepository));
      }

      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowCredentials(false)
            .allowedOrigins("*")
            .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization", "Merchant")
            .exposedHeaders(
                "File-Type",
                "File-Length",
                "Content-Type",
                "Content-Length",
                "Last-Modified",
                "Last-Created");
      }
    };
  }
}
