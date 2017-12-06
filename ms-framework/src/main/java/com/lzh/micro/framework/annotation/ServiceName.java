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
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceName {

    String value() default "";
}
