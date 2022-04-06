package demo.reflect;

import java.lang.reflect.Constructor;

public class ConstructorCollector {
    public static void main(String[] args) throws Exception {
        Class clazz = Class.forName("demo.reflect.GetClassDemo");
        //1、获取所有的共有构造方法
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            System.out.println(constructor);
        }

        //2、获取所有构造方法(共有、私有。。)
        constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            System.out.println(constructor);
        }
        //3、获得单个单参数的共有方法
        Constructor constructor = clazz.getConstructor(String.class, int.class);
        System.out.println(constructor);

        //4、获得单个私有的构造方法
        constructor = clazz.getDeclaredConstructor(int.class);
        System.out.println(constructor);
        //暴力访问（忽略访问修饰符）
        constructor.setAccessible(true);
        GetClassDemo getClassDemo = (GetClassDemo) constructor.newInstance(1);
    }
}
