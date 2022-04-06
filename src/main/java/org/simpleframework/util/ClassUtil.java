package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     *获取包下类集合
     * @param packageName
     * @return 该包下的所有类的集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName){
        //1、获取类加载器
        ClassLoader classLoader = getClassLoader();
        //2、通过类加载器获取需要加载的资源
        //获取资源的分隔符是以 / 为标识分割的，但是包名是以 . 分割的，所以要先处理packageName
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if(url==null){
            log.warn("unable to retrieve anything from package:" +packageName);
            return null;
        }
        //3、依据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet=null;
        //过滤出文件类型的资源 getProtocol()获得协议名
        if(url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL )){
            //创建集合实例
            classSet=new HashSet<Class<?>>();
            //获得资源的实际路径
            File packageDirectory = new File(url.getPath());
            //遍历目录以及子目录获得class文件
            extractClassFile(classSet,packageDirectory,packageName);
        }
        //TODO 此处可以加入针对其他类型资源的处理，例如打成jar包后，protocol就是jar
        return classSet;
    }

    /**
     *
     * @param emptyClassSet 装载目标类的集合
     * @param fileSource  文件或者目录
     * @param packageName  包名
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if(!fileSource.isDirectory()){//当找到文件时递归停止，及判断不是目录就终止。
            return;
        }
        //如果是文件夹，就罗列当前文件夹下的所有文件和文件夹（不包含子文件夹）
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isDirectory()){//是文件夹就通过
                    return true;
                }else {//文件需要判断是否是class文件
                    //获取文件的绝对值路径
                    String absolutePath = file.getAbsolutePath();
                    if(absolutePath.endsWith(".class")){
                        //若是class文件，则直接加载
                        addToClassSet(absolutePath);
                    }
                    return false;
                }

            }
            //根据class文件的绝对值路径，获取并生成class对象，并放入classSet中
            private void addToClassSet(String absolutePath) {
                //1、从class文件的绝对值路径里提取出包含了package的类名
                //将绝对路径中的分隔符转为.
                absolutePath =absolutePath.replace(File.separator,".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                //将className包含的.class后缀去掉
                className=className.substring(0,className.lastIndexOf("."));
                //2、通过反射机制获取对应的Class对象并加入到classSet里
                Class targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
            }
        });

        if(files!=null){//先判断得到的文件们是否为空，在遍历进行递归调用
            for (File f : files) {
                //递归调用
                extractClassFile(emptyClassSet, f, packageName);
            }
        }

    }

    /**
     * 获取Class对象
     * @param className=package+类名
     * @return Class
     */
    public static Class<?> loadClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error:",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取类加载器
     * @return 当前ClassLoader
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取类的实例化对象
     * @param clazz Class
     * @param accessible  是否支持创建出私有class对象的实例
     * @param <T>  class的类型
     * @return 类的实例化
     */
    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T) constructor.newInstance();

        } catch (Exception e) {
            log.error("newInstance error ",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置类的属性值
     * @param field 成员变量
     * @param targetBean  类实例
     * @param value  成员变量的值
     * @param accessible 是否允许设置私有属性
     */
    public static void setField(Field field, Object targetBean, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(targetBean,value);
        } catch (IllegalAccessException e) {
            log.error("setField error",e);
            throw new RuntimeException(e);
        }

    }
}
