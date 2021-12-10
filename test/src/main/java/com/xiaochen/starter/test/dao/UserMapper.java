package com.xiaochen.starter.test.dao;

import com.xiaochen.starter.test.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User queryByUsername(@Param("username") String username);

    User queryUserById(@Param("Id") String Id);

    List<User> load();
}
