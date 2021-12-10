package com.xiaochen.starter.shard.config;

import com.xiaochen.starter.shard.common.CommonConst;
import com.xiaochen.starter.shard.common.LogConst;
import com.xiaochen.starter.shard.model.ShardTableRule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Slf4j
@ConfigurationProperties(prefix = CommonConst.SHARD_CONFIG)
public class ShardTableStarterProperties {

    private List<ShardTableRule> rules;
    public static Map<String, ShardTableRule> shardTableRuleMap;

    @PostConstruct
    public void init() throws Exception {

        log.error(LogConst.ONE_TIPS, "PostConstruct");

        if (CollectionUtils.isEmpty(rules)) {
            log.warn("shardTable rules isEmpty");
            shardTableRuleMap = new HashMap<>();
            return;
        }
        shardTableRuleMap = rules.stream().collect(Collectors.toMap(ShardTableRule::getTableName, ShardTableRule -> ShardTableRule));
        log.warn(LogConst.ONE_TIPS, shardTableRuleMap);
    }
}
