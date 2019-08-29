package com.derek.android.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target(ElementType.METHOD)：表示该注解只能注解在方法上。如果想类和方法都可以用，那可以这么写：@Target({ElementType.METHOD,ElementType.TYPE})，依此类推。
 * @Retention(RetentionPolicy.RUNTIME)：表示该注解在程序运行时是可见的（还有SOURCE、CLASS分别指定注解对于那个级别是可见的，一般都是用RUNTIME）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BehaviorTrace {
    String value();
    int type();
}
