package com.xiaochen.starter.dingtalk.config;

import com.xiaochen.starter.dingtalk.common.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(DingtalkAppProperties.class)
@ConditionalOnProperty(value = CommonConst.DING_TALK_CONFIG, havingValue = "true", matchIfMissing = true)
public class DingtalkAutoConfigure implements InitializingBean {

    @Autowired
    private DingtalkAppProperties dingtalkAppProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.error("afterPropertiesSet -> {}", dingtalkAppProperties);
    }
}
