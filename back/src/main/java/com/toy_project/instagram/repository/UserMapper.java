package com.toy_project.instagram.repository;

import com.toy_project.instagram.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public Integer saveUser(User user);
}
