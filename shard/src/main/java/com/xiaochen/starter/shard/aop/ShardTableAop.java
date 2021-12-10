package com.xiaochen.starter.shard.aop;

import com.xiaochen.starter.shard.common.CommonConst;
import com.xiaochen.starter.shard.common.LogConst;
import com.xiaochen.starter.shard.model.ShardTableValue;
import com.xiaochen.starter.shard.util.ShardTableThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
//@Component
public class ShardTableAop {

    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private static final String POINTCUT_ANNOTATION = "@annotation("+ CommonConst.ANNOTATION_PATH+")";
    private static final String POINTCUT_METHOD_NAME = "pointcut()";

    @Pointcut(POINTCUT_ANNOTATION)
    public void pointcut() {
    }

    @Before(POINTCUT_METHOD_NAME)
    public void before(JoinPoint joinPoint) {
        log.info(LogConst.TWO_TIPS, "before method", joinPoint.getSignature().getName());
    }

    @Around(value = POINTCUT_METHOD_NAME)
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();
        log.warn("target method -> {}:{}", method.hashCode(), method.getName());

        ShardTableAnnotation shardTableAnnotation = method.getAnnotation(ShardTableAnnotation.class);
        String tableName = shardTableAnnotation.tableName();
        String target = shardTableAnnotation.shardKey();

        List<String> paramNames = Arrays.asList(methodSignature.getParameterNames());
        List<Object> paramVals = Arrays.asList(joinPoint.getArgs());
        //将方法的参数名和参数值一一对应的放入上下文中
        EvaluationContext ctx = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.size(); i++) {
            ctx.setVariable(paramNames.get(i), paramVals.get(i));
        }

        // 解析SpEL表达式获取结果
        String value = spelExpressionParser.parseExpression(shardTableAnnotation.shardKey()).getValue(ctx).toString();
        log.warn("{},{},解析SpEL表达式获取结果:{} -> {}", Thread.currentThread().hashCode(), shardTableAnnotation.hashCode(), target, value);

        setShardValue(tableName, value);

        return joinPoint.proceed();
    }

    private void setShardValue(String tableName, String value) {
        ShardTableValue shardTableValue = new ShardTableValue();
        shardTableValue.setTable(tableName);
        shardTableValue.setTargetValue(StringUtils.isNumeric(value) ? Long.parseLong(value) : Math.abs(value.hashCode()));
        ShardTableThreadLocalUtil.setVale(shardTableValue);
    }

    @After(value = POINTCUT_METHOD_NAME)
    public void after(JoinPoint joinPoint) {
        log.debug(LogConst.TWO_TIPS, "after method", joinPoint.getSignature().getName());
    }

}

