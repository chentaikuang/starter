package com.xiaochen.starter.test.config;

import com.xiaochen.starter.test.consts.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis配置类
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {CommonConst.MAPPER_SCAN, CommonConst.MAPPER_SCAN_DAO}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class MyBatisConfig {
    private final String LOCATION_PATTERN = CommonConst.MYBATIS_MAPPER_XML;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(LOCATION_PATTERN));
        log.info("init mybatis-sqlSessionFactory");
        return bean.getObject();
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        log.info("init mybatis-transactionManager");
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        log.info("init mybatis-sqlSessionTemplate");
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
