package demo.reflect;

import java.lang.reflect.Field;

public class FieldController {
    public static void main(String[] args) throws Exception {
        //获得class对象
        Class clazz = Class.forName("demo.reflect.GetClassDemo");

        //1.获取所有公有的字段
        Field[] fields = clazz.getFields();
        //2.获取所有的字段
        Field[] declaredField = clazz.getDeclaredFields();
        //3.获取单个特定公有的field
        Field field = clazz.getField("name");
        //获得实例对象
        GetClassDemo getClassDemo = (GetClassDemo) clazz.getConstructor().newInstance();
        //给实例对象设置name属性值
        field.set(getClassDemo,"haohoa");
        System.out.println(getClassDemo.name);
        //6.获取单个私有的Field
        Field targetInfo = clazz.getDeclaredField("targetInfo");
        targetInfo.setAccessible(true);
        //给实例对象设置targetInfo属性值
        targetInfo.set(getClassDemo,"111111");
        System.out.println(getClassDemo);
    }
}
