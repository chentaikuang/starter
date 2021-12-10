package com.xiaochen.starter.shard.util;

import com.xiaochen.starter.shard.common.LogConst;
import com.xiaochen.starter.shard.model.ShardTableValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Slf4j
public class ShardTableThreadLocalUtil {

    private static ThreadLocal<Map<Thread, Stack<ShardTableValue>>> values = new ThreadLocal<>();

    public static void main(String[] args) {
        ThreadLocal<String> testThreadlocal = new ThreadLocal<>();
        testThreadlocal.set(RandomStringUtils.randomAlphabetic(2));
        System.out.println(testThreadlocal.get());
        testThreadlocal.set(RandomStringUtils.randomAlphabetic(2));
        System.out.println(testThreadlocal.get());
        testThreadlocal.set(RandomStringUtils.randomAlphabetic(2));
        System.out.println(testThreadlocal.get());
    }

    public static void setVale(ShardTableValue shardTableValue) {
        if (values.get() == null) {
            values.set(new HashMap<>());
        }
        Thread currentThread = Thread.currentThread();
        if (!values.get().containsKey(currentThread)) {
            Stack stack = new Stack();
            values.get().put(currentThread, stack);
        }
        values.get().get(currentThread).push(shardTableValue);
    }

    public static ShardTableValue getValue() {
        Thread currentThread = Thread.currentThread();
        if (!values.get().containsKey(currentThread)) {
            log.warn("no found currentThread,{}-> {}", currentThread, values.get());
            return null;
        }
        Stack<ShardTableValue> shardTableValues = values.get().get(currentThread);
        log.info(LogConst.TWO_TIPS, shardTableValues.size(), shardTableValues);
        if (CollectionUtils.isEmpty(shardTableValues)) {
            log.warn("stack empty,{} -> {}", currentThread, shardTableValues);
            return null;
        }
        ShardTableValue pop = shardTableValues.pop();
        log.info(LogConst.TWO_TIPS, "treadlocal pop stack", pop);
        if (CollectionUtils.isEmpty(shardTableValues)) {
            log.warn("B remove currentThread key,{} -> {}", currentThread, values.get());
            values.get().remove(currentThread);
            log.warn("A remove currentThread key,{} -> {}", currentThread, values.get());
        }
        return pop;
    }
}
