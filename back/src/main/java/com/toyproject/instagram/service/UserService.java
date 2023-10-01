package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service // IoC
@RequiredArgsConstructor
public class UserService {

    // private final(상수)로 선언된 것들은 무조건 한번 초기화를 해야하며 사용해야한다. (아니면 오류남)
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder; // passwordEncoder 라고 IoC에 넣었기때문에 호출도 passwordEncoder 라고 해야함
    private final AuthenticationManagerBuilder authenticationManagerBuilder; // Security 라이브러리를 다운받음과 동시에 IoC에 등록되어있음
    private final JwtTokenProvider jwtTokenProvider;

    public void signupUser(SignupReqDto signupReqDto) {
        User user = signupReqDto.toUserEntity(passwordEncoder);
        Integer executeCount = userMapper.saveUser(user);
        System.out.println(executeCount);
    }

    public String signinUser(SigninReqDto signinReqDto) {
        // PrincipalDetailsService 에서 phone, email, username중 어느것으로 로그인 시도를 했는지 확인후 로그인 시도를 한 값을
        // 아이디와 패스워드를 넣은것을 받아와 토큰을 만든다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signinReqDto.getPhoneOrEmailOrUsername(), signinReqDto.getLoginPassword());

        // 만일 값이 없으면 담기전에 authenticate <- 얘가 예외터트림
        // 값이 있으면 authentaication에 값을 담음
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // jwtTokenProvider 클래스에서 만든 JWT토큰생성 메소드를 사용하여 로그인 인증을 한 객체로 JWT 토큰을 생성한다.
        // 로그인 인증을 한 객체 안에는 로그인 유저의 정보가 들어있음
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        return accessToken;
    }

    public Boolean authenticate(String token) {
        String accessToken = jwtTokenProvider.convertToken(token); // 토큰에 Bearer 를 제거하고 토큰만 추출해서 accessToken 에 넣음
        if(!jwtTokenProvider.validateToken(accessToken)) { // accessToken 정보 변형, 기간 만료, JWT 토큰을 사용할 수 있는지 없는지 판별
            throw new JwtException("사용자 정보가 만료되었습니다. 다시 로그인하세요.");
        }
        return true;
    }
}








