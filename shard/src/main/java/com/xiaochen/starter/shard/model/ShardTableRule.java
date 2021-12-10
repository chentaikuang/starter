package com.xiaochen.starter.shard.model;

import lombok.Data;

@Data
public class ShardTableRule {
    private String tableName;
    private int tableNumber;
}
