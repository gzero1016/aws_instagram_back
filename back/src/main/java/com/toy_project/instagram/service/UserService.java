package com.toy_project.instagram.service;

import com.toy_project.instagram.dto.SignupReqDto;
import com.toy_project.instagram.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service    // IoC
@RequiredArgsConstructor
public class UserService {

    // private final(상수)로 선언된 것들은 무조건 한번 초기화를 해야하며 사용해야한다. (아니면 오류남)
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;  // passwordEncoder 라고 IoC에 넣었기때문에 호출도 passwordEncoder 라고 해야함

    public void signupUser(SignupReqDto signupReqDto) {
        Integer executeCount = userMapper.saveUser(signupReqDto.toUserEntity(passwordEncoder));
        System.out.println(executeCount);
    }
}
