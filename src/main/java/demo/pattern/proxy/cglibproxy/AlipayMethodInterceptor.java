package demo.pattern.proxy.cglibproxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class AlipayMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
       beforePay();
        Object result = methodProxy.invokeSuper(o, objects);
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
