package com.xiaochen.starter.shard.interceptor;

import com.xiaochen.starter.shard.aop.ShardTableAnnotation;
import com.xiaochen.starter.shard.common.LogConst;
import com.xiaochen.starter.shard.config.ShardTableStarterProperties;
import com.xiaochen.starter.shard.model.ShardTableRule;
import com.xiaochen.starter.shard.model.ShardTableValue;
import com.xiaochen.starter.shard.util.ParseSqlTableUtil;
import com.xiaochen.starter.shard.util.ShardTableThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @author ligit
 * 执行顺序：Executor -> StatementHandler -> ParameterHandler -> ResultSetHandler
 * <p>
 * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
 * ParameterHandler (getParameterObject, setParameters)
 * ResultSetHandler (handleResultSets, handleOutputParameters)
 * StatementHandler (prepare, parameterize, batch, update, query)
 * 对应功能为：
 * 拦截执行器的方法
 * 拦截参数的处理
 * 拦截结果集的处理
 * 拦截Sql语法构建的处理
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@Slf4j
public class StatementHandlerInterceptor implements Interceptor {

    @Autowired
    private ShardTableStarterProperties shardTableStarterProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();//被代理对象
        Method method = invocation.getMethod();//代理方法
        Object[] args = invocation.getArgs(); //方法参数
        log.warn("target SimpleName -> {},TypeName -> {}", target.getClass().getSimpleName(), target.getClass().getTypeName());

        statementHandler(invocation);
        return invocation.proceed();
    }

    private void statementHandler(Invocation invocation) throws Exception {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        String methodId = mappedStatement.getId();//mapper方法全路径
        String methodType = mappedStatement.getSqlCommandType().toString();
        log.warn("methodId -> {},type -> {}", methodId, methodType);

//        mapperAnnotation(methodId, invocation.getMethod());

//        Object parameterHandlerObject = statementHandler.getParameterHandler().getParameterObject();
//        printParameterHandlerObj(parameterHandlerObject);

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
//        BoundSql boundSql = statementHandler.getBoundSql();

//        printBoundSql(boundSql);

        String newSql = convertSql(boundSql);
        metaObject.setValue("delegate.boundSql.sql", newSql);
    }

    private void printParameterHandlerObj(Object parameterHandlerObject) {
        log.warn(LogConst.TWO_TIPS, "parameterHandlerObject", parameterHandlerObject);
        if (parameterHandlerObject != null) {
            Class<?> objectClass = parameterHandlerObject.getClass();
            log.warn(LogConst.TWO_TIPS, "parameterHandlerObject", objectClass);
            log.warn(LogConst.TWO_TIPS, "parameterHandlerObject", objectClass.getTypeName());
            log.warn(LogConst.TWO_TIPS, "parameterHandlerObject", objectClass.getTypeParameters());
            Field[] fields = objectClass.getFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    log.warn(LogConst.TWO_TIPS, "获取参数", field.getName());
                }
            }
            if (parameterHandlerObject instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameterHandlerObject;
                paramMap.keySet().forEach(key -> {
                    log.warn(LogConst.TWO_TIPS, key, paramMap.get(key));
                });
            }
        }
    }

    private void mapperAnnotation(String methodId, Method prepareMethod) throws ClassNotFoundException, NoSuchMethodException {
        int clzNameIndex = methodId.lastIndexOf(".");
        String className = methodId.substring(0, clzNameIndex);
        String methodName = methodId.substring(clzNameIndex + 1, methodId.length());
        log.warn(LogConst.TWO_TIPS, className, methodName);

        Class<?> classObj = Class.forName(className);
        ShardTableAnnotation shardTableAnnotation = classObj.getAnnotation(ShardTableAnnotation.class);
        log.warn(LogConst.TWO_TIPS, "Class", shardTableAnnotation);

        shardTableAnnotation = prepareMethod.getAnnotation(ShardTableAnnotation.class);
        log.warn(LogConst.TWO_TIPS, "prepareMethod", shardTableAnnotation);

        List<Method> methodList = Arrays.stream(classObj.getMethods()).filter(m -> {
            return m.getName().equals(methodName);
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(methodList)) {
            log.warn(LogConst.ONE_TIPS, "无匹配ShardTableTarget注解方法");
            return;
        }
        Method annotationMethod = methodList.get(0);
        shardTableAnnotation = annotationMethod.getAnnotation(ShardTableAnnotation.class);
        log.warn(LogConst.TWO_TIPS, "Method", shardTableAnnotation);

        for (Method method : classObj.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ShardTableAnnotation.class) && methodName.equals(method.getName())) {
                shardTableAnnotation = method.getAnnotation(ShardTableAnnotation.class);
                log.warn(LogConst.TWO_TIPS, "getDeclaredMethods", shardTableAnnotation);
                break;
            }
        }
    }

    private void printBoundSql(BoundSql boundSql) {
        List<ParameterMapping> paramMappings = boundSql.getParameterMappings();
        paramMappings.forEach(parameterMapping -> {
            log.warn(LogConst.TWO_TIPS, "boundSql.parameterMapping.getProperty", parameterMapping.getProperty());
        });

        Object paramObj = boundSql.getParameterObject();
        log.warn(LogConst.TWO_TIPS, "boundSql.getParameterObject", paramObj);
    }

    private String convertSql(BoundSql boundSql) throws Exception {
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        List<String> tabs = ParseSqlTableUtil.getTableNames(sql);
        for (String oldTab : tabs) {
            String newTab = getShardTableRule(oldTab);
            if (!oldTab.equals(newTab)) {
                sql = sql.replaceAll(appendRepaceTab(oldTab), appendRepaceTab(newTab));
            }
        }
        return sql;
    }

    private String getShardTableRule(String tab) {
        log.warn("oldTab -> {}", tab);
        if (ShardTableStarterProperties.shardTableRuleMap.containsKey(tab)) {
            ShardTableRule shardTableRule = ShardTableStarterProperties.shardTableRuleMap.get(tab);

            ShardTableValue shardTableValue = ShardTableThreadLocalUtil.getValue();
            if (shardTableValue != null) {
                long targetVal = shardTableValue.getTargetValue();
                if (targetVal >= 0) {
                    long shardTableIndex = targetVal % shardTableRule.getTableNumber();
                    log.info("shard table index, {} % {} = {}", targetVal, shardTableRule.getTableNumber(), shardTableIndex);
                    tab += shardTableIndex;
                }
            }

//            ShardTableAnnotation shardTableAnnotation = ShardTableThreadLocalUtil.get();
//            if (shardTableAnnotation != null) {
//                long targetVal = shardTableAnnotation.shardValue();
//                if (targetVal >= 0) {
//                    long shardTableIndex = targetVal % shardTableRule.getTableNumber();
//                    log.info("shard table index, {} % {} = {}", targetVal, shardTableRule.getTableNumber(), shardTableIndex);
//                    tab += shardTableIndex;
//                }
//            }

        }
        log.warn("newTab -> {}", tab);
        return tab;
    }

    private String appendRepaceTab(String tab) {
        StringBuffer sbf = new StringBuffer(" ").append(tab).append(" ");
        return sbf.toString();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            log.warn(LogConst.TWO_TIPS, "plugin", target.getClass().getSimpleName());
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        log.warn(LogConst.ONE_TIPS, ShardTableStarterProperties.shardTableRuleMap.toString());
    }
}
