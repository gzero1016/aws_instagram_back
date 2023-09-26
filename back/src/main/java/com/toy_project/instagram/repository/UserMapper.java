package com.toy_project.instagram.repository;

import com.toy_project.instagram.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public Integer saveUser(User user);
    public User findUserByPhone(String phoneOrEmailOrUsername);
    public User findUserByEmail(String phoneOrEmailOrUsername);
    public User findUserByUsername(String phoneOrEmailOrUsername);
}
