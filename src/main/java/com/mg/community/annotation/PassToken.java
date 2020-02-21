package com.mg.community.annotation;

import java.lang.annotation.*;

/**
 * @ClassName PassToken
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/19 13:22
 * @Version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PassToken {
    boolean required() default true;
}
