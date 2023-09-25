package com.toy_project.instagram.service;

import com.toy_project.instagram.dto.SignupReqDto;
import com.toy_project.instagram.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signupUser(SignupReqDto signupReqDto) {
        Integer executeCount = userMapper.saveUser(signupReqDto.toUserEntity(passwordEncoder));
    }
}
