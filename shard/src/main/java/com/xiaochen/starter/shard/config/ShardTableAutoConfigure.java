package com.xiaochen.starter.shard.config;


import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.xiaochen.starter.shard.aop.ShardTableAop;
import com.xiaochen.starter.shard.common.CommonConst;
import com.xiaochen.starter.shard.common.LogConst;
import com.xiaochen.starter.shard.interceptor.ParamHandlerInterceptor;
import com.xiaochen.starter.shard.interceptor.StatementHandlerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Iterator;
import java.util.List;


@Order(Integer.MAX_VALUE)
@Slf4j
@Configuration
@AutoConfigureAfter({PageHelperAutoConfiguration.class})
@EnableConfigurationProperties(ShardTableStarterProperties.class)
@ConditionalOnProperty(value = CommonConst.SHARD_ENABLED, havingValue = "true", matchIfMissing = false)
public class ShardTableAutoConfigure implements InitializingBean {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @Bean
    @ConditionalOnMissingBean(ParamHandlerInterceptor.class)
    public ParamHandlerInterceptor paramInterceptor() {
        log.info(LogConst.ONE_TIPS, "init paramInterceptor");
        return new ParamHandlerInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(StatementHandlerInterceptor.class)
    public StatementHandlerInterceptor statementHandlerInterceptor() {
        log.info(LogConst.ONE_TIPS, "init statementHandlerInterceptor");
        return new StatementHandlerInterceptor();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        log.warn(LogConst.ONE_TIPS, "afterPropertiesSet");

        Iterator var3 = this.sqlSessionFactoryList.iterator();
        while (var3.hasNext()) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) var3.next();
            sqlSessionFactory.getConfiguration().addInterceptor(paramInterceptor());
            sqlSessionFactory.getConfiguration().addInterceptor(statementHandlerInterceptor());
            log.info(LogConst.ONE_TIPS, "add paramInterceptor");
        }
    }

    @Bean
    @ConditionalOnMissingBean(ShardTableAop.class)
    public ShardTableAop shardTableAop() {
        log.info(LogConst.ONE_TIPS, "init ShardTableAop");
        return new ShardTableAop();
    }
}
