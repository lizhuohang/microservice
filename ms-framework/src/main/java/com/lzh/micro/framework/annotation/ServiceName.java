package com.lzh.micro.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lizhuohang
 *
 * @Date: Created in 17:41 17/12/5
 *
 * 服务注册注解，基于方法级别的
 * 一个方法理论上只属于一个service
 * 方便定位到方法上，但是如果有的方法没有添加此注解，只要有一个方法有此注解，还是能提供服务
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ServiceName {

    String value() default "";
}
