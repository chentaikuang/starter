package com.xiaochen.starter.dingtalk.config;

import com.xiaochen.starter.dingtalk.common.CommonConst;
import com.xiaochen.starter.dingtalk.model.DingtalkApp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
@ConfigurationProperties(prefix = CommonConst.DING_TALK_CONFIG)
public class DingtalkAppProperties {

//    @Value("${spring.application.name}")
//    private String curAppName;

    private List<DingtalkApp> apps;

    public static Map<String, DingtalkApp> dingtalkAppMap;

    @PostConstruct
    public void init() throws Exception {

        log.error(CommonConst.ONE_TIPS, "PostConstruct");
//        log.error(CommonConst.TWO_TIPS, "current app name", curAppName);

        if (CollectionUtils.isEmpty(apps)) {
            log.error("dingtalkApp config isEmpty");
            throw new Exception("未找到钉钉APP配置信息");
        }

        checkAppsParam();

        dingtalkAppMap = apps.stream().collect(Collectors.toMap(DingtalkApp::getName, DingtalkApp -> DingtalkApp));
        log.warn(CommonConst.ONE_TIPS, dingtalkAppMap);
    }

    private void checkAppsParam() {
        apps.forEach(app -> {
            Assert.isTrue(StringUtils.isNotBlank(app.getName()), "钉钉配置Name不可为空");
            Assert.isTrue(StringUtils.isNotBlank(app.getToken()), "钉钉配置Token不可为空");
            Assert.isTrue(StringUtils.isNotBlank(app.getSecret()), "钉钉配置Secret不可为空");
        });
    }
}
