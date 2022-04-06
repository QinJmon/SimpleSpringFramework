package demo.pattern.proxy;

import demo.pattern.proxy.cglibproxy.AlipayMethodInterceptor;
import demo.pattern.proxy.cglibproxy.CglibUtil;
import demo.pattern.proxy.impl.*;
import demo.pattern.proxy.jdkproxy.AlipayInvocationHandler;
import demo.pattern.proxy.jdkproxy.JdkDynamicProxyUtil;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxyDemo {
    public static void main(String[] args) {
      /*  AlipayToC toCProxy = new AlipayToC(new ToCPaymentImpl());
        toCProxy.pay();
        AliToB toBProxy = new AliToB(new ToBPaymentImpl());
        toBProxy.pay();*/
        /*ToBPaymentImpl toBPayment = new ToBPaymentImpl();
        InvocationHandler handler = new AlipayInvocationHandler(toBPayment);
        ToBPayment proxyInstance = JdkDynamicProxyUtil.newProxyInstance(toBPayment, handler);
        proxyInstance.pay();

        ToCPaymentImpl toCPayment = new ToCPaymentImpl();
        InvocationHandler toCHandler = new AlipayInvocationHandler(toCPayment);
        ToCPayment toCProxy = JdkDynamicProxyUtil.newProxyInstance(toCPayment, toCHandler);
        toCProxy.pay();*/

        CommonPayment commonPayment = new CommonPayment();
        MethodInterceptor methodInterceptor = new AlipayMethodInterceptor();
        CommonPayment proxy = CglibUtil.createProxy(commonPayment, methodInterceptor);
        proxy.pay();
    }
}

