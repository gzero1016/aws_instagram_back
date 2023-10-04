package com.toyproject.instagram.dto;

import com.toyproject.instagram.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignupReqDto {
//    @NotBlank(message = "전화번호 또는 이메일은 공백일 수 없습니다.")
    // 회원가입할때 들어올 데이터들 (클라이언트가 전달해준 변수 그대로)
    @Pattern(regexp = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$|^[0-9]{11}+$", message = "이메일 또는 전화번호를 입력하세요.")
    private String phoneOrEmail;

    @Pattern(regexp = "^[가-힣]{2,6}$", message = "이름은 한글만 입력할 수 있습니다.")
    private String name;

    @Pattern(regexp = "^(?=.*[a-z])[a-z0-9_.]*$", message = "사용할 수 없는 사용자 이름입니다. 다른 이름을 사용하세요.")
    private String username;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$", message = "비밀번호는 영문, 숫자 조합으로 8자 이상 입력하세요.")
    private String password;

    // dto를 entity로 변환하는 과정
    public User toUserEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password)) // Dto -> Entity로 변환할때 비밀번호 암호화
                .build();
    }
    // 클라이언트 -> 컨트롤러 -> Service 는 dto로 소통
    // Service -> Repository -> Database 는 Entity로 소통
    // Service 에서 dto를 entity로 변환을 해야한다.
}
