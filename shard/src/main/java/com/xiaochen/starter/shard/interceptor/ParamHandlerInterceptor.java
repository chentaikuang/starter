package com.xiaochen.starter.shard.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.xiaochen.starter.shard.common.LogConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Intercepts({
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
@Slf4j
public class ParamHandlerInterceptor implements Interceptor {

    public static final String BOUND_SQL = "boundSql";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        log.info(LogConst.ONE_TIPS, this.getClass().getSimpleName());
//        Object[] args = invocation.getArgs();
//        log.info(LogConst.ONE_TIPS, args);
//        Object target = invocation.getTarget();
//        log.info(LogConst.ONE_TIPS, target);
//        Method method = invocation.getMethod();
//        log.info(LogConst.ONE_TIPS, method);
//        log.info(LogConst.ONE_TIPS, method.getAnnotations());
//        log.info(LogConst.ONE_TIPS, method.getAnnotatedParameterTypes());

//        parameterHandler(invocation);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler) {
            log.warn(LogConst.TWO_TIPS, "plugin", target.getClass().getSimpleName());
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private void parameterHandler(Invocation invocation) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, SQLException {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        PreparedStatement preparedStatement = (PreparedStatement) invocation.getArgs()[0];//类头部@Signature结构体指定有一个参数
        Field boundSqlField = parameterHandler.getClass().getDeclaredField(BOUND_SQL);
        boundSqlField.setAccessible(true);
        BoundSql boundSql = (BoundSql) boundSqlField.get(parameterHandler);

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        List<String> paramList = parameterMappings.stream().map(ParameterMapping::getProperty).collect(Collectors.toList());
        log.info("sql params -> {}", paramList);

        Field parameterObjectField = parameterHandler.getClass().getDeclaredField("parameterObject");
        parameterObjectField.setAccessible(true);
        Object paramObject = parameterObjectField.get(parameterHandler);//获取到参数

        //改写参数
        Map<String, Object> paramMap = new HashMap<>();

        if (paramObject == null) {
            paramObject = paramMap;
        } else {
            Class<?> paramObjectClass = paramObject.getClass();
            if (ClassUtils.isPrimitiveOrWrapper(paramObjectClass)
                    || String.class.isAssignableFrom(paramObjectClass)
                    || Number.class.isAssignableFrom(paramObjectClass)) {
                if (paramList.size() == 1) {
                    paramMap.put(paramList.iterator().next(), paramObject);
                    paramObject = paramMap;
                }
            } else if (paramObject instanceof Map) {
                paramMap.putAll((Map<? extends String, ?>) paramObject);
                paramObject = paramMap;
            } else {
                //具体的参数类
                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(paramObjectClass, BOUND_SQL);
                //可读写字段，才能动态操作字段
                if (propertyDescriptor != null && propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Object paramVal = propertyDescriptor.getReadMethod().invoke(paramObject);
                    if (paramVal == null) {
                        propertyDescriptor.getWriteMethod().invoke(paramObject, BOUND_SQL);
                    }
                }
            }
        }

        if (paramObject instanceof Map) {
            log.info("参数转换类型为Map -> {}", JSONObject.toJSONString(paramObject));
        }

        parameterObjectField.set(parameterHandler, paramObject);
        parameterHandler.setParameters(preparedStatement);
    }
}
