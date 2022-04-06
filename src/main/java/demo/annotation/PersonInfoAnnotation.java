package demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)//只能作用在变量上
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonInfoAnnotation {
    //名字
    public String name();
    //年龄
    public int age() default 18;
    //性别
    public String gender() default "女";
    //开发语言
    public String[] language();
}
