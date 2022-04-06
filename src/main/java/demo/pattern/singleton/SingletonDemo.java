package demo.pattern.singleton;

import java.lang.reflect.Constructor;

public class SingletonDemo {
    public static void main(String[] args) throws Exception {
        System.out.println(EnumStarvingSingleton.getInstance());

        Class clazz = EnumStarvingSingleton.class;
        Constructor constructor = clazz.getDeclaredConstructor();//获得私有构造方法
        constructor.setAccessible(true);
        EnumStarvingSingleton enumStarvingSingleton = (EnumStarvingSingleton) constructor.newInstance();
        System.out.println(enumStarvingSingleton.getInstance());

    }
}
