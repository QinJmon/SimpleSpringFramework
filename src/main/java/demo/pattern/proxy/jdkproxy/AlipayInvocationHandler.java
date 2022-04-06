package demo.pattern.proxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AlipayInvocationHandler implements InvocationHandler {
    private Object targetObject;

    public AlipayInvocationHandler(Object targetObject){
        this.targetObject=targetObject;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        beforePay();
        Object result = method.invoke(targetObject, args);
        afterPay();
        return result;
    }

    private void beforePay() {
        System.out.println("从银行取款");
    }
    private void afterPay() {
        System.out.println("给商家付款转账");
    }
}
