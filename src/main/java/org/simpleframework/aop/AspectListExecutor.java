package org.simpleframework.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Method;
import java.util.*;

//主要针对每个被代理的对象进行方法的拦截
public class AspectListExecutor implements MethodInterceptor {
    //被代理的类
    private Class<?> targetClass;
    //排序好的Aspect列表
    @Getter
    private List<AspectInfo> sortAspectInfoList;
    public AspectListExecutor(Class<?> targetClass,List<AspectInfo> aspectInfoList){
        this.targetClass=targetClass;
        //将其排序好之后再赋值给对应的成员变量
        this.sortAspectInfoList=sortAspectInfoList(aspectInfoList);
    }

    /**
     * 按照order的值进行升序排序，确保order值小的aspect先织入
     * @param aspectInfoList
     * @return
     */
    private List<AspectInfo> sortAspectInfoList(List<AspectInfo> aspectInfoList) {
        Collections.sort(aspectInfoList, new Comparator<AspectInfo>() {
            @Override
            public int compare(AspectInfo o1, AspectInfo o2) {
                return o1.getOrderIndex()-o2.getOrderIndex();
            }
        });
        return aspectInfoList;
    }

    /**
     *
     * @param proxy 动态代理对象
     * @param method 被代理的方法实例
     * @param args 被代理对象方法需要的参数数组
     * @param methodProxy 动态代理生成的method对象实例
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnValue=null;
        //传入被代理方法的实例，对sortAspectInfoList进行精筛
        collectAccurateMatchedAspectList(method);
        //aspect集合空值校验
        if(ValidationUtil.isEmpty(sortAspectInfoList)){//说明初筛列表经过精筛之后变成了空，说明不需要织入任何逻辑
            //只执行自身的方法
            returnValue=methodProxy.invokeSuper(proxy,args);

            return returnValue;}
        //1、按照order的顺序升序执行完所有Aspect的before方法
        invokeBeforeAdvices(method,args);
        try{
            //2、执行被代理类的方法
            returnValue=methodProxy.invokeSuper(proxy,args);
            //3、如果被代理方法正常返回，则按照order的顺序降序执行完所有Aspect的after方法
            returnValue=invokeAfterReturningAdvices(method,args,returnValue);
        } catch (Exception e) {
            //4、如果被代理方法抛出异常，则按照order的顺序降序执行
            invokeAfterThrowingAdvices(method,args,e);
        }
        return returnValue;
    }

    //此时sortAspectInfoList存储的是对应于某一个被代理类的Aspect初筛列表
    //使用初筛列表中的每一个aspect列表对应的pointcutLoader实例分别调用其方法传入被代理方法实例来判断被代理方法实例是否真的匹配此aspect的pointcut表达式
    //匹配就保留，不匹配就删除
    private void collectAccurateMatchedAspectList(Method method) {
        if(ValidationUtil.isEmpty(sortAspectInfoList)){return;}
        Iterator<AspectInfo> it=sortAspectInfoList.iterator();
        while (it.hasNext()){
            AspectInfo aspectInfo=it.next();
            if(!aspectInfo.getPointcutLocator().accurateMatches(method)){
                it.remove();
            }
        }

    }

    //4、如果被代理方法抛出异常，则按照order的顺序降序执行
    private void invokeAfterThrowingAdvices(Method method, Object[] args, Exception e) throws Throwable {
        for(int i=sortAspectInfoList.size()-1;i>=0;i--){
            sortAspectInfoList.get(i).getAspectObject().afterThrowing(targetClass,method,args,e);
        }
    }

    //3、如果被代理方法正常返回，则按照order的顺序降序执行完所有Aspect的after方法
    private Object invokeAfterReturningAdvices(Method method, Object[] args, Object returnValue) throws Throwable {
        Object result=null;
        for(int i=sortAspectInfoList.size()-1;i>=0;i--){
            result=sortAspectInfoList.get(i).getAspectObject().afterReturning(targetClass,method,args,returnValue);
        }
        return result;
    }

    //1、按照order的顺序升序执行完所有Aspect的before方法
    private void invokeBeforeAdvices(Method method, Object[] args) throws Throwable {
        for (AspectInfo aspectInfo : sortAspectInfoList) {
            aspectInfo.getAspectObject().before(targetClass,method,args);
        }
    }
}
