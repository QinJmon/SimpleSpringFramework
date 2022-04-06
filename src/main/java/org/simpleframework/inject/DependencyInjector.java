package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {
    //需要先获取容器中的实例，
    private BeanContainer beanContainer;

    //为了在DependencyInjector实例被创建时就能获得beanContainer实例，
    // 可以在构造方法里面编写获得容器实例的逻辑
    public DependencyInjector(){
        beanContainer=BeanContainer.getInstance();
    }
    /*
    * 执行IoC
    * */
    public void doIoc(){
        //判空
        if(ValidationUtil.isEmpty(beanContainer.getClasses())){
            log.warn("bean is nothing class");
            return;
        }
        //1、遍历Bean容器中所有的Class对象
        for (Class<?> clazz : beanContainer.getClasses()) {
            //2、遍历Class对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            //判空
            if(ValidationUtil.isEmpty(fields)){
                continue;
            }
            for (Field field : fields) {
                //3、找出被Autowired标记的成员变量
                if(field.isAnnotationPresent(Autowired.class)){
                    //先去获得Autowired注解实例，再去获取注解里面的属性
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();

                    //4、获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();
                    //5、获取这些成员变量的类型在容器里对应的实例
                    Object fieldValue=getFileInstance(fieldClass,autowiredValue);
                    if(fieldValue==null){
                        throw new RuntimeException("unable to inject relevant type,target fieldClass is:"+fieldClass.getName());
                    }else {
                        //6、通过反射将对应的成员变量实例注入到成员变量所在类的实例里
                        Object targetBean = beanContainer.getBean(clazz);
                        ClassUtil.setField(field,targetBean,fieldValue,true);
                    }
                }
            }
        }
    }

    /*
    * 根据Class在beanContainer里获取其实例或者实现类
    * */
    private Object getFileInstance(Class<?> fieldClass, String autowiredValue) {
        //从容器中获得bean
        Object fieldValue = beanContainer.getBean(fieldClass);
        //判空
        if(fieldValue!=null){
            return fieldValue;//直接找到，说明是类
        }else {
            //在容器中没有直接找到bean，有两种情况1、传入的是接口，bean里面存的是实现类  2、就是没有
           Class<?> implementedClass= getImplmentedClass(fieldClass,autowiredValue);//根据接口找实现类
            if(implementedClass!=null){
                return beanContainer.getBean(implementedClass);
            }else {
                return null;
            }

        }

    }
    /*
    * 获取接口的实现类
    * */
    private Class<?> getImplmentedClass(Class<?> fieldClass, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(fieldClass);
        //判空
        if(!ValidationUtil.isEmpty(classSet)){
            //首先判断autowiredValue的值是否为默认值(空)
            if(ValidationUtil.isEmpty(autowiredValue)){
                //再判断实现类有几个
                if(classSet.size()==1){
                    return classSet.iterator().next();//一个实现类没有歧义直接返回
                }else {
                    //多个实现类又没有指定具体的实现类名称，报错
                    throw new RuntimeException("multiple implemented classes for" +fieldClass.getName()+ " please set @Autowired's value to pick one");

                }
            }else {//指定了实现类
                //遍历classSet，寻找并返回
                for (Class<?> clazz : classSet) {
                    //得到类的简写名称getSimpleName()只有一个类名
                    if(autowiredValue.equals(clazz.getSimpleName())){
                        return clazz;
                    }

                }

            }


        }
        return null;

    }


}
