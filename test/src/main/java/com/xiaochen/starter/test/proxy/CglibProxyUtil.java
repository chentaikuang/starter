package com.xiaochen.starter.test.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

//todo cglib未完
public class CglibProxyUtil<T> implements MethodInterceptor {
    public static <T> T invoke(Class<T> clz) {
        CglibProxyUtil cglibProxyUtil = new CglibProxyUtil();
        return (T) cglibProxyUtil.getProxy(clz);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return methodProxy.invokeSuper(o, args);
    }

    public Object getProxy(Class clz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(this);
        Object subObj = enhancer.create();

        Field[] fields = subObj.getClass().getSuperclass().getDeclaredFields();
        for (Field field : fields) {

            //获取属性 是否有注解
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                field.setAccessible(true);
                //获取当前属性的类型，有了类型后可以创建具体对象
                Class<?> type = field.getType();

//                try {
//                    //创建具体对象
//                    Object o = type.newInstance();
//                    field.set(subObj, o);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return subObj;
    }

}
