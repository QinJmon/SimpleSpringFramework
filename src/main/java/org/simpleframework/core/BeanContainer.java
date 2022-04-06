package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE) //无参构造函数，可以包含私有的
public class BeanContainer {

    //存放所有被配置标记的目标对象的Map,类对象以及对应的实例
    private final Map<Class<?>,Object> beanMap=new ConcurrentHashMap<>();

    /*
    * 加载bean的注解列表
    * */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Service.class,
            Repository.class, Aspect.class);

    /*
    * 容器是否已经加载过bean
    * */
    private boolean loaded=false;

    public boolean isLoaded(){
        return loaded;
    }

    /**
     * 获取Bean容器实例
     * @return
     */
    public static BeanContainer getInstance(){
        return ContainerHolder.HOLDER.instance;
    }
    /*
    * 获得Bean实例数量
    * */
    public int size(){
        return beanMap.size();
    }

    /**
     * 添加一个Class对象和其对应的Bean实例到容器
     * @param clazz
     * @param Bean
     * @return
     */
    public Object addBean(Class<?> clazz,Object Bean){
        return beanMap.put(clazz, Bean);
    }

    /**
     * 删除容器中管理的对象
     * @param clazz
     * @return
     */
    public Object removeBean(Class<?> clazz){
        return beanMap.remove(clazz);
    }

    /**
     * 根据Class对象获得Bean实例
     * @param clazz
     * @return
     */
    public Object getBean(Class<?> clazz){
        return beanMap.get(clazz);
    }

    /**
     * 获得容器管理的所有Class对象集合
     * @return Class集合
     */
    public Set<Class<?>> getClasses(){
        return beanMap.keySet();
    }

    /**
     * 获得所有Bean集合
     * @return
     */
    public Set<Object> getBeans(){
        return (Set<Object>) beanMap.values();
    }

    /**
     * 根据注解获得容器里所有Class对象,即所有的key
     * @param annotation
     * @return
     */
    public Set<Class<?>> getClassesByAnnotation(Class< ? extends Annotation> annotation){
        //1、获取beanMap的所有class对象
        Set<Class<?>> keySet = getClasses();
        if(ValidationUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        //2、通过注解筛选被注解标记的class对象，并添加到classSet里
        Set<Class<?>> classSet=new HashSet<>();
        for (Class<?> clazz : keySet) {
            //类是否有相关的注解标记
            if(clazz.isAnnotationPresent(annotation)){
                classSet.add(clazz);
            }
        }
        return classSet.size()>0 ? classSet : null ;
    }

    /**
     * 通过接口或者父类获取实现类或者子类的Class集合，不包括本身
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass){
        //1、获取beanMap的所有class对象
        Set<Class<?>> keySet = getClasses();
        if(ValidationUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        //2、判断集合里的元素是否是传入的接口或者类的子类，并添加到classSet里
        Set<Class<?>> classSet=new HashSet<>();
        for (Class<?> clazz : keySet) {
            //判断类是否传入参数的子类，并去除本身
            if(interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)){
                classSet.add(clazz);
            }
        }
        return classSet.size()>0 ? classSet : null ;
    }



    /**
     * 扫描加载所有Bean
     * @param packageName 包名
     */
    public synchronized void loadBeans(String packageName){
        //判断bean容器是否已经加载过
        if(isLoaded()){
            log.warn("BeanContainer has been loaded");
            return;
        }
        //1、获取package所有的class
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        //2、判断集合空值，日志记录，并返回。得到了就循环遍历classset根据定义好的注解得到对应的class对象
        if(ValidationUtil.isEmpty(classSet)){
            log.warn("extract nothing from packageName "+packageName);
            return;
        }

        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                //该类上面标记了我们定义的注解
                if(clazz.isAnnotationPresent(annotation)){
                    //将目标类本身作为key，目标类实例作为v，放到beanMap中
                    beanMap.put(clazz,ClassUtil.newInstance(clazz,true));
                }
            }
        }
        loaded=true;
    }

    private enum ContainerHolder{//设置私有的枚举的成员变量
        HOLDER;//用来盛放BeanContainer实例
        private BeanContainer instance;//定义私有的成员变量
        //枚举的私有构造函数，在里面new实例
        ContainerHolder(){
            instance=new BeanContainer();
        }
    }
}
