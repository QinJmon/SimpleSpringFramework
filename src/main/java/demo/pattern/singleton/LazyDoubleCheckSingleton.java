package demo.pattern.singleton;

public class LazyDoubleCheckSingleton {

    //加volatile防止重排序，以免出现还没初始化好，已经给对象分配好内存空间的情况
    private volatile static LazyDoubleCheckSingleton instance;

    private LazyDoubleCheckSingleton(){}

    public static LazyDoubleCheckSingleton getInstance(){
        //第一次检测
        if(instance==null){
            //加锁
            synchronized (LazyDoubleCheckSingleton.class){
                //第二次判断
                if(instance==null){
                    //这条指令分为三步，加上volatile关键字使其严格按照顺序执行，不会出现132情况
                    /*
                    * 1、分配内存空间
                    * 2、初始化对象
                    * 3、设置instance指向刚分配的内存地址，此时instance对象！null*/
                    instance=new LazyDoubleCheckSingleton();
                }
            }
        }
        return instance;
    }
}
