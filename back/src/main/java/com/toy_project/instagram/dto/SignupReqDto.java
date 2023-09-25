package com.toy_project.instagram.dto;

import com.toy_project.instagram.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class SignupReqDto {
    // 회원가입할때 들어올 데이터들
    private String phoneAndEmail;
    private String name;
    private String username;
    private String password;

    public User toUserEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .email(phoneAndEmail)
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
    }

}
