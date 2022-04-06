package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.render.JsonResultRender;
import org.simpleframework.mvc.render.ResourceNotFoundResultRender;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.render.ViewResultRender;
import org.simpleframework.mvc.type.ControllerMethod;
import org.simpleframework.mvc.type.RequestPathInfo;
import org.simpleframework.util.ConverterUtil;
import org.simpleframework.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Controller请求处理器
 * */
@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {
    //IOC容器
    private BeanContainer beanContainer;
    //请求和controller方法的映射集合
    private Map<RequestPathInfo, ControllerMethod> pathControllerMethodMap = new ConcurrentHashMap<>();

    /**
     * 依靠容器的能力，建立起请求路径、请求方法与Controller方法实例的映射
     */
    public ControllerRequestProcessor() {
        this.beanContainer = BeanContainer.getInstance();
        Set<Class<?>> requestMappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMethodMap(requestMappingSet);
    }

    private void initPathControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (ValidationUtil.isEmpty(requestMappingSet)) {
            return;
        }
        //1、遍历所有被@RequestMapping修饰的类
        for (Class<?> requestMappingClass : requestMappingSet) {
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            String basePath = requestMapping.value();//一级路径
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }
            //2、遍历类里所有被@RequestMapping修饰的方法
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if (ValidationUtil.isEmpty(methods)) {
                continue;
            }
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                    String methodPath = methodRequest.value();
                    if (!methodPath.startsWith("/")) {
                        methodPath = "/" + methodPath;
                    }
                    String url = basePath + methodPath;
                    //3、解析方法里被@RequestParam标记的参数
                    Parameter[] parameters = method.getParameters();
                    Map<String, Class<?>> methodParams = new HashMap<>();
                    if (!ValidationUtil.isEmpty(parameters)) {
                        for (Parameter parameter : parameters) {
                            RequestParam param = parameter.getAnnotation(RequestParam.class);
                            if (param == null) {
                                throw new RuntimeException("The param must have @RequestParam");
                            }
                            methodParams.put(param.value(), parameter.getType());
                        }
                    }
                    //4、将获取到的信息封装起来放到映射表中
                    String httpMethod = String.valueOf(methodRequest.method());
                    RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);
                    if (this.pathControllerMethodMap.containsKey(requestPathInfo)) {
                        log.warn("duplicate url:{} registration , current class {} method{} will override the former one",
                                requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
                    }
                    ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParams);
                    this.pathControllerMethodMap.put(requestPathInfo, controllerMethod);

                }

            }

        }
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1、解析HttpServletRequest的请求方法、请求路径、获取对应的ControllerMethod
        String method = requestProcessorChain.getRequestMethod();
        String path = requestProcessorChain.getRequestPath();
        ControllerMethod controllerMethod = this.pathControllerMethodMap.get(new RequestPathInfo(method, path));
        if (controllerMethod == null) {//根据请求的方法和路径在map种得不到对应的实例，说明找不到对应的资源
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(method, path));
            return false;
        }
        //2、解析请求参数，并传递给获取到的controllerMethod 实例去执行
        Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());
        //3、根据处理的结果，选择对应的render进行渲染
        setResultRender(result, controllerMethod, requestProcessorChain);

        return true;
    }

    /**
     * 根据不同的情况设置不同的渲染器
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        if (result == null) {
            return;
        }
        ResultRender resultRender;
        boolean isJson = controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            resultRender = new JsonResultRender(result);
        } else {
            resultRender = new ViewResultRender(result);
        }
        requestProcessorChain.setResultRender(resultRender);
    }

    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        //1、从请求里获取get或者post的参数名及其对应的值
        Map<String, String> requestParamMap = new HashMap<>();
        //get,post方法的请求参数获取方式
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
            if (!ValidationUtil.isEmpty(parameter.getValue())) {
                //只支持一个参数对应一个值得形式
                requestParamMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
        }
        //2、根据获取到得请求参数名及其对应得值，以及controllerMethod里面的参数和类型得映射关系，去实例化
        List<Object> methodParams = new ArrayList<>();
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();
        for (String paramName : methodParamMap.keySet()) {
            Class<?> type = methodParamMap.get(paramName);
            String requestValue = requestParamMap.get(paramName);
            Object value;
            //只支持String及其基本类型及他们得包装类型
            if (requestValue == null) {
                //将请求里得参数值转为适配与参数类型得空值
                value = ConverterUtil.primitiveNull(type);
            } else {
                value = ConverterUtil.convert(type, requestValue);
            }
            methodParams.add(value);
        }
        //3、执行controller里面对应的方法并返回结果
        Object controller = beanContainer.getBean(controllerMethod.getControllerClass());
        Method invokeMethod = controllerMethod.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }

        return result;
    }
}
