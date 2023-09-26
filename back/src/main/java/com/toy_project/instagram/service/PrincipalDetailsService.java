package com.toy_project.instagram.service;

import com.toy_project.instagram.entity.User;
import com.toy_project.instagram.repository.UserMapper;
import com.toy_project.instagram.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneOrEmailOrUsername) throws UsernameNotFoundException {

// 아이디가 phone인지 email인지 username인지 걸러서 해당값을 아이디에 넣어 password랑 리턴해준다.
        User user = userMapper.findUserByPhone(phoneOrEmailOrUsername);
        if(user != null) {
            return new PrincipalUser(user.getPhone(), user.getPassword());
        }

        user = userMapper.findUserByEmail(phoneOrEmailOrUsername);
        if(user != null) {
            return new PrincipalUser(user.getEmail(), user.getPassword());
        }

        user = userMapper.findUserByUsername(phoneOrEmailOrUsername);
        if(user != null) {
            return new PrincipalUser(user.getUsername(), user.getPassword());
        }

        throw  new UsernameNotFoundException("잘못된 사용자 정보입니다. 다시 확인하세요.");

    }
}
