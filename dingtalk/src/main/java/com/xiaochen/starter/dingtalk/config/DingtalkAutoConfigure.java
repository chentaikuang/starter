package com.xiaochen.starter.dingtalk.config;

import com.xiaochen.starter.dingtalk.common.CommonConst;
import com.xiaochen.starter.dingtalk.service.DingtalkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(DingtalkAppProperties.class)
@ConditionalOnProperty(value = CommonConst.DING_TALK_ENABLED, havingValue = "true", matchIfMissing = false)
public class DingtalkAutoConfigure implements InitializingBean {

    @Autowired
    private DingtalkAppProperties dingtalkAppProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.warn("afterPropertiesSet -> {}", dingtalkAppProperties);
    }

    @Bean
    @ConditionalOnMissingBean(DingtalkService.class)
    public DingtalkService dingtalkService() {
        log.info(CommonConst.ONE_TIPS, "init dingtalkService");
        return new DingtalkService();
    }
}
