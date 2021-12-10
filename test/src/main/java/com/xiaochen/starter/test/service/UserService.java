package com.xiaochen.starter.test.service;

import com.xiaochen.starter.shard.aop.ShardTableAnnotation;
import com.xiaochen.starter.test.dao.UserMapper;
import com.xiaochen.starter.test.entity.User;
import com.xiaochen.starter.test.util.AppContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @ShardTableAnnotation(tableName = "user", shardKey = "#user.getUsername()")
    public User queryByUsername(User user) {
        return userMapper.queryByUsername(user.getUsername());
    }

    @ShardTableAnnotation(tableName = "user", shardKey = "#userId")
    public User findUserById(String userId) {
        return userMapper.queryUserById(userId);
    }

    public List<User> findAll() {
        return userMapper.load();
    }

    @ShardTableAnnotation(tableName = "user", shardKey = "#user.getUsername()")
    public User mergeInfo(User user) {
        //Aop内部方法调用无法被拦截处理
        User user1 = AppContextUtil.getClzBean(UserService.class).findUserById(user.getId());
        log.error(user1.toString());
        return userMapper.queryByUsername(user.getUsername());
    }
}
