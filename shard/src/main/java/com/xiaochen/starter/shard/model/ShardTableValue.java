package com.xiaochen.starter.shard.model;

import lombok.Data;

@Data
public class ShardTableValue {
    private String table;
    private long targetValue;
}
