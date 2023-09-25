package com.toy_project.instagram.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class SignupException extends RuntimeException{  // 예외를 생성하는 클래스

    private Map<String, String> errorMap;

    // 생성자
    public SignupException(Map<String, String> errorMap) {
        super("회원가입 유효성 검사 오류");
        this.errorMap = errorMap;
        errorMap.forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });
    }
}
