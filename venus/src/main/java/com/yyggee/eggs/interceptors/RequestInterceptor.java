package com.yyggee.eggs.interceptors;

import com.yyggee.eggs.configs.TenantContext;
import com.yyggee.eggs.model.ds1.Auditor;
import com.yyggee.eggs.repositories.ds1.AuditorJPARepository;
import com.yyggee.eggs.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    private static final String START_TIME_HEADER_FIELD = "startTime";
    private static final String MERCHANT_HEADER_FIELD = "Merchant";

    private final AuditorJPARepository jpaRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RequestInterceptor(JwtTokenProvider jwtTokenProvider, AuditorJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        long startTime = System.currentTimeMillis();

        log.info("Handling {} request uri: {} at time {}", request.getMethod(), requestUri, startTime);

        request.setAttribute(START_TIME_HEADER_FIELD, startTime);
        String environment = request.getHeader(MERCHANT_HEADER_FIELD);

        TenantContext.setTenant(environment);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        log.info("Post Handling");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestUri = request.getRequestURI();
        long startTime = (Long) request.getAttribute(START_TIME_HEADER_FIELD);
        long diff = System.currentTimeMillis() - startTime;

        try {
            String username = jwtTokenProvider.getUsernameFromTokens(request.getHeader(HttpHeaders.AUTHORIZATION));
            username = (username == null) ? "" : username;
            jpaRepository.save(new Auditor()
                    .setUsername(username).setMethod(request.getMethod())
                    .setUrl(requestUri).setStatus(String.valueOf(response.getStatus()))
            );
        } catch (Exception e) {
            log.info("Tracking user is error {}", e.getMessage());
        }

        log.info("Completed handling {} request uri: {} returned {} in {} ms", request.getMethod(), requestUri, response.getStatus(), diff);
        TenantContext.clear();
    }
}

