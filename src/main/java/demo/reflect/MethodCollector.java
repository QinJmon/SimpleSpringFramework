package demo.reflect;

import java.lang.reflect.Method;

public class MethodCollector {

    public static void main(String[] args) throws Exception {
        //获得class对象
        Class clazz = Class.forName("demo.reflect.GetClassDemo");
        //2、获取所有公有方法
        Method[] methods = clazz.getMethods();
        //3、获取该类的所有方法
        Method[] declaredMethods = clazz.getDeclaredMethods();
        //调用指定的公用方法show1
        Method method = clazz.getMethod("show1", String.class);

        //调用指定的私有方法show4
        Method show4 = clazz.getDeclaredMethod("show4", int.class);
        show4.setAccessible(true);
        //使用实例化对象使用方法(属性和方法的使用都要先实例化对象)
        GetClassDemo getClassDemo = (GetClassDemo) clazz.getConstructor().newInstance();
        String res = (String) show4.invoke(getClassDemo, 20);
        System.out.println("返回值 ： " + res);
    }
}
