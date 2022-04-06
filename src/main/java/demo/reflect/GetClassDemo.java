package demo.reflect;

public class GetClassDemo {

    //---------构造函数-----------
    //(默认的带参数构造函数)
    GetClassDemo(String str) {
        System.out.println("(默认)的构造方法 s = " + str);
    }

    //无参构造函数
    public GetClassDemo() {
        System.out.println("调用了公有的无参构造方法 。。。");
    }

    //有一个参数的构造函数
    public GetClassDemo(char name) {
        System.out.println("调用了带有一个参数的构造方法，参数值为 " + name);
    }

    //有多个参数的构造函数
    public GetClassDemo(String name, int index) {
        System.out.println("调用了带有多个参数的构造方法，参数值为【目标名】： " + name + " 【序号】" + index);
    }
    //受保护的构造函数
    protected GetClassDemo(boolean n){
        System.out.println("受保护的构造方法 n :" + n);
    }
    //私有的构造函数
    private GetClassDemo(int index){
        System.out.println("私有的构造方法 序号：" + index);
    }

    //**************字段*******************//
    public String name;
    protected int index;
    char type;
    private String targetInfo;
    @Override
    public String toString(){
        return "ReflectTarget [name=" + name + ", index=" + index + ", type=" + type
                + ", targetInfo=" + targetInfo + "]";
    }
    //***************成员方法***************//
    public void show1(String s){
        System.out.println("调用了公有的，String参数的show1(): s = " + s);
    }
    protected void show2(){
        System.out.println("调用了受保护的，无参的show2()");
    }
    void show3(){
        System.out.println("调用了默认的，无参的show3()");
    }
    private String show4(int index){
        System.out.println("调用了私有的，并且有返回值的，int参数的show4(): index = " + index);
        return "show4result";
    }

    public static void main(String[] args) throws ClassNotFoundException {
        GetClassDemo getClassDemo = new GetClassDemo();
        Class class1 = getClassDemo.getClass();
        System.out.println(class1);

        Class class2 = GetClassDemo.class;
        System.out.println(class2);
        System.out.println(class1==class2);

        Class class3 = Class.forName("demo.reflect.GetClassDemo");
        System.out.println(class3);
        System.out.println(class2==class3);

    }
}
