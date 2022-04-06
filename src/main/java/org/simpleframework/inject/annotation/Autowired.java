package org.simpleframework.inject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) //仅支持在成员变量上使用
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value() default "";
}
