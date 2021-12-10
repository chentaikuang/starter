package com.xiaochen.starter.test;

import com.xiaochen.starter.test.consts.CommonConst;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({CommonConst.SCAN_BASE, CommonConst.SCAN_CONFIG})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
