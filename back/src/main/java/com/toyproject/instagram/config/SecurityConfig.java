package com.toyproject.instagram.config;

import com.toyproject.instagram.exception.AuthenticateExceptionEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity  // 현재 우리가 만든 Security 설정 정책을 따르겠다.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticateExceptionEntryPoint authenticateExceptionEntryPoint;

    @Bean // 외부에서 가져오는 라이브러리라 코드수정이 불가하기 때문에 직접 생성해서 IoC에 넣은것이다.
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();    // WebMvcConfig에서 설정한 cors 정책을 따르겠다.
        http.csrf().disable();  // csrf 토큰 비활성화 // 서버사이드 랜더링할때 csrf를 사용한다.

        http.authorizeRequests()
                .antMatchers("/api/v1/auth/**") // /api/v1/auth/로 시작하는 모든 요청
                .permitAll()   // 인증없이 요청을 허용하겠다.
                .anyRequest()   // 나머지 모든 요청은
                .authenticated() // 인증을 받아라
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticateExceptionEntryPoint);
    }
}
