package com.lzh.micro.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lizhuohang
 * @Date: Created in 18:15 17/12/6
 * 基于类的服务注册名注解
 * 一个类理论上可以属于多个service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpperServiceName {
    String[] value() default {""};
}
