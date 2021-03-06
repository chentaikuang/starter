package com.xiaochen.starter.test.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaochen.starter.dingtalk.config.DingtalkAppProperties;
import com.xiaochen.starter.dingtalk.service.DingtalkService;
import com.xiaochen.starter.test.entity.User;
import com.xiaochen.starter.test.service.UserService;
import com.xiaochen.starter.test.util.RedisUtil;
import com.xiaochen.starter.test.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;
import java.util.Set;

@Slf4j
@RestController("TestApi")
@RequestMapping("/test/api")
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired(required = false)
    private DingtalkAppProperties dingtalkAppProperties;

    @Autowired(required = false)
    private DingtalkService dingtalkService;

    @Autowired
    private UserService userService;

    //http://localhost:8899/test/api/findUser?id=1
    @RequestMapping("/findUser")
    public String findUser(String id, String userName) {
        Assert.isTrue(StringUtils.isNotBlank(id) || StringUtils.isNotBlank(userName), "参数不可皆为空");
        log.info("param id -> {},userName -> {}", id, userName);
        User user = null;
        if (StringUtils.isNotBlank(id)) {
            user = userService.findUserById(id);
        } else {
            user = new User();
            user.setUsername(userName);
            user = userService.queryByUsername(user);
        }
        if (user == null) {
            return "no found user by " + (StringUtils.isNotBlank(id) ? "id[" + id + "]" : "userName[" + userName + "]");
        }
        return JSONObject.toJSONString(user);

    }

    //http://localhost:8899/test/api/cache?clr=1
    @RequestMapping("/cache")
    public String cache(int clr) {
        String zsetKey = "zset_test";
        if (clr == 1) {
            log.warn("clear size -> {}", redisUtil.zsetSize(zsetKey));
            redisUtil.clear(zsetKey);
        }
        int num = 10;
        while (num-- >= 1) {
            redisUtil.zsetAdd(zsetKey, RandomStringUtils.randomNumeric(2), new Random().nextInt(1000));
        }
        log.info("zset size -> {}", redisUtil.zsetSize(zsetKey));
        Set zset = redisUtil.range(zsetKey, 0, -1);
        log.info("zset -> {}", zset);
        return RandomStringUtils.randomAlphanumeric(6);
    }

    //http://localhost:8899/test/api/loadProperties
    @RequestMapping("/loadProperties")
    public String loadProperties() {
        log.warn("dingtalkService -> {}", dingtalkService);
        log.warn("dingtalkAppProperties -> {}", dingtalkAppProperties);
        return JSONObject.toJSONString(dingtalkAppProperties);
    }

    //http://localhost:8899/test/api/send?t=0
    @RequestMapping("/send")
    public String send(int t) {
        if (t == 0) {
            dingtalkService.send("简单发送消息测试", "我是一条内容消息");
        } else if (t == 1) {

            dingtalkService.send("会员注册失败告警", "[no_key]手机号保存失败,手机号保存失败,手机号保存失败,手机号保存失败,手机号保存失败,手机号保存失败", new RuntimeException());
        } else if (t == 2) {

            dingtalkService.send("系统运行错误告警", "[has_key]CUP资源告警CUP资源告警CUP资源告警数据库资源不够.数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够",
                    new IllegalArgumentException(), "test");
        } else if (t == 3) {
            dingtalkService.send("BCP发送消息测试", "我是一条内容消息", "bcp");
        } else {
            dingtalkService.send("系统运行错误告警", "[has_key]CUP资源告警CUP资源告警CUP资源告警数据库资源不够.数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够",
                    new RuntimeException(RandomStringUtils.randomAlphanumeric(8)), "XXX");
        }
        return RandomStringUtils.randomNumeric(2);
    }

    //http://localhost:8899/test/api/thread?id=1
    @RequestMapping("/thread")
    public void thread(String id) {
//        synchronized () sleep join yield
        log.warn("ST [{}] -> {}", id, DateUtils.formatDate(new Date()));
        if ("s".equals(id)) {
            ThreadUtil.sleep(10_000);
        } else if ("j".equalsIgnoreCase(id)) {
            ThreadUtil.joinThread(10_000);
        }
        log.warn("ED [{}] -> {}", id, DateUtils.formatDate(new Date()));
    }
}
