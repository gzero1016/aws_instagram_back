package com.toyproject.instagram.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request; //다운 캐스팅
        String token = httpServletRequest.getHeader("Authorization");
        String jwtToken = jwtTokenProvider.convertToken(token); // convertToken: Bearer 때는거
        String uri = httpServletRequest.getRequestURI(); // uri 주소 가져오기

        System.out.println(uri);

        // /api/v1/auth/**로 시작하지않는 나머지 주소들의 인증절차 로직
        if(!uri.startsWith("/api/v1/auth") && jwtTokenProvider.validateToken(jwtToken)) {  // validateToken: 토큰이 유효하면 True , 유효하지 않으면 False
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 시큐리티 인증 상태에 Authentication 객체가 존재하면 인증된 상태임을 의미함
        }

        chain.doFilter(request, response);  // doFilter는 마지막에 chain이 있어야 다음단계로 넘어갈 수 있음
    }
}
