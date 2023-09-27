package com.toy_project.instagram.controller;

import com.toy_project.instagram.dto.SigninReqDto;
import com.toy_project.instagram.dto.SignupReqDto;
import com.toy_project.instagram.exception.SignupException;
import com.toy_project.instagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    // IoC : spring framework가 제어하는것 (어노테이션 포함)
    // private final(상수)로 부속을 넣어주면 알아서 생성해주는것이 제어의 역전(IoC) 이다.
    private final UserService userService;

    @PostMapping("/user")   // 위에서 /api/v1/auth 로 한번 감싸줬기때문에 /api/v1/auth/user가 주소가된다.
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) {
    // Valid : dto에 NotBlank 어노테이션이 달려있는 변수들의 빈값이 있는지 확인을 해서 bindingResult에 넣어라
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {   // getFieldErrors : 필드의 에러를 하나씩 꺼내서 오류메세지를 띄워라
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            // SignupException 예외 객체를 생성 후 예외를 던져버림
            throw new SignupException(errorMap);
        }
        userService.signupUser(signupReqDto);

        return ResponseEntity.ok(null);
    }

    // 로그인을 Get이 아니라 Post 요청으로 하는이유는 정보를 숨기기 위해서임
    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {

        // 정상 로그인이된다면 토큰이 반환됨
        String accessToken = userService.signinUser(signinReqDto);

        // 토큰이 200번 Ok로 응답을 보냄
        return ResponseEntity.ok().body(accessToken);
    }

}