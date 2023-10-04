package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.exception.SignupException;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // email , phone 중 어느걸로 로그인 했는지 정규식을 들고와 확인후 해당 데이터베이스 컬럼에 값 추가하는 로직
        Matcher emailMatcher = Pattern.compile("^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$")
                .matcher(signupReqDto.getPhoneOrEmail());
        Matcher phoneMatcher = Pattern.compile("^[0-9]{11}+$")
                .matcher(signupReqDto.getPhoneOrEmail());

        if(emailMatcher.matches()) {
            user.setEmail(signupReqDto.getPhoneOrEmail());
        }
        if(phoneMatcher.matches()) {
            user.setPhone(signupReqDto.getPhoneOrEmail());
        }

        checkDuplicated(user); // 중복확인
        userMapper.saveUser(user); // 중복확인에서 예외가 터지지 않았으면 저장하기
    }

    // 중복 확인 로직
    private void checkDuplicated(User user) {
        if(StringUtils.hasText(user.getPhone())) {  // null , 빈값여부를 같이 체크해줌
            if(userMapper.findUserByPhone(user.getPhone()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("phone", "이미 사용중인 연락처 입니다.");
                throw new SignupException(errorMap);
            }
        }
        if(StringUtils.hasText(user.getEmail())) {
            if(userMapper.findUserByEmail(user.getEmail()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("email", "이미 사용중인 이메일 입니다.");
                throw new SignupException(errorMap);
            }
        }
        if(StringUtils.hasText(user.getUsername())) {
            if(userMapper.findUserByUsername(user.getUsername()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("username", "이미 사용중인 사용자이름 입니다.");
                throw new SignupException(errorMap);
            }
        }
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








