package com.yyggee.eggs.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.yyggee.eggs.event.GenericSpringEventPublisher;
import com.yyggee.eggs.model.ds1.Auditor;
import com.yyggee.eggs.model.ds1.User;
import com.yyggee.eggs.repositories.ds1.AuditorJPARepository;
import com.yyggee.eggs.repositories.ds1.UserJPARepository;
import com.yyggee.eggs.security.JwtTokenProvider;
import com.yyggee.eggs.service.ds1.AuditorService;
import com.yyggee.eggs.service.ds1.UserService;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles("gitlab")
@Disabled
class AuditorServiceTest {

  private static Auditor p1;
  private static Auditor p2;

  @Mock private AuditorJPARepository auditorJPARepository;

  @MockBean UserService userService;
  @InjectMocks AuditorService auditorService;

  @MockBean UserJPARepository userJPARepository;
  @MockBean PasswordEncoder passwordEncoder;
  @MockBean JwtTokenProvider jwtTokenProvider;
  @MockBean AuthenticationManager authenticationManager;

  @Autowired GenericSpringEventPublisher publisher;

  @BeforeAll
  public static void init() {
    p1 = new Auditor("joko", "GET", "OK", "coba.com/testing/page");
    p2 = new Auditor("joki", "POST", "OK", "coba.com/testing/page?create=data");
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
  public void findAllTest_WhenNoRecord() {

    Mockito.when(auditorJPARepository.findAll()).thenReturn(Arrays.asList());
    assertThat(auditorService.findAll().size(), is(0));
    Mockito.verify(auditorJPARepository, Mockito.times(1)).findAll();
  }

  @Test
  public void findAllTest_WhenRecord() {

    Mockito.when(auditorJPARepository.findAll()).thenReturn(Arrays.asList(p1, p2));
    assertThat(auditorService.findAll().size(), is(2));
    assertThat(auditorService.findAll().get(0), is(p1));
    assertThat(auditorService.findAll().get(1), is(p2));
    Mockito.verify(auditorJPARepository, Mockito.times(3)).findAll();
  }

  @Test
  public void findById() {

    Mockito.when(auditorJPARepository.findById(1L)).thenReturn(Optional.of(p1));
    assertThat(auditorService.findById(1L), is(Optional.of(p1)));
    Mockito.verify(auditorJPARepository, Mockito.times(1)).findById(1L);
  }

  @Test
  void createOrUpdate() {

    Mockito.when(auditorJPARepository.save(p1)).thenReturn(p1);
    assertThat(auditorService.createOrUpdate(p1), is(p1));
    Mockito.verify(auditorJPARepository, Mockito.times(1)).save(p1);

    Mockito.when(auditorJPARepository.save(p2)).thenReturn(p2);
    assertThat(auditorService.createOrUpdate(p2).getUsername(), is("joki"));
    Mockito.verify(auditorJPARepository, Mockito.times(1)).save(p2);
  }

  @Test
  void deleteById() {
    auditorService.deleteById(1L);
    Mockito.verify(auditorJPARepository, Mockito.times(1)).deleteById(1L);
  }

  @Test
  void test_event() {
    log.info("coba");
    publisher.publishCustomEvent("data1");
  }
}
