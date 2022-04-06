package demo.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationParser {
    //解析类的注解
    public static void parseTypeAnnotation() throws ClassNotFoundException {
        Class clazz = Class.forName("demo.annotation.ImoocCourse");
        //获得class对象得注解，而不是其里面的方法和成员变量得注解
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            CourseInfoAnnotation courseInfoAnnotation=(CourseInfoAnnotation)annotation;
            System.out.println(courseInfoAnnotation.courseName()+courseInfoAnnotation.courseTag()
            +courseInfoAnnotation.courseProfile()+courseInfoAnnotation.courseIndex());
        }

    }

    //解析成员变量得注解标签
    public static void parseFieldAnnotation() throws ClassNotFoundException {
        Class clazz = Class.forName("demo.annotation.ImoocCourse");
        //反射获得对象得属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //判定成员变量上有没有指定得注解
            boolean b = field.isAnnotationPresent(PersonInfoAnnotation.class);
            if(b){
                PersonInfoAnnotation personInfoAnnotation= field.getAnnotation(PersonInfoAnnotation.class);
                System.out.println(personInfoAnnotation.name()+personInfoAnnotation.age()
                +personInfoAnnotation.gender());

                for (String language : personInfoAnnotation.language()) {//打印数组
                    System.out.println(language);
                }
            }
        }
    }

    //解析方法上得注解
    public static void parseMethod() throws ClassNotFoundException {
        Class clazz = Class.forName("demo.annotation.ImoocCourse");
        //通过反射获得对象得方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            //判断方法上得注解是否我们想要的
            boolean b = method.isAnnotationPresent(CourseInfoAnnotation.class);
            if(b){
                CourseInfoAnnotation annotation = method.getAnnotation(CourseInfoAnnotation.class);
                System.out.println(annotation.courseName()+annotation.courseIndex()+
                        annotation.courseProfile()+annotation.courseIndex());
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        parseTypeAnnotation();
        parseFieldAnnotation();
        parseMethod();
    }
}
