package org.simpleframework.core;

import org.simpleframework.mvc.DispatcherServlet;
import com.imooc.controller.frontend.MainPageController;
import com.imooc.service.solo.HeadLineService;
import com.imooc.service.solo.impl.HeadLineServiceImpl;
import org.junit.jupiter.api.*;
import org.simpleframework.core.annotation.Controller;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanContainerTest {
    private static BeanContainer beanContainer;

    @BeforeAll  //@BeforeAll：只进行一次初始化
    static void init(){
        beanContainer=BeanContainer.getInstance();
    }

    @DisplayName("加载目标类及其实例到容器里")
    @Order(1)
    @Test
    public void loadBeansTest(){
        //先判断是否初始化
        Assertions.assertEquals(false,beanContainer.isLoaded());
        beanContainer.loadBeans("com.imooc");
        Assertions.assertEquals(6,beanContainer.size());
        Assertions.assertEquals(true,beanContainer.isLoaded());

    }

    @DisplayName("根据类获取其实例")
    @Order(2)
    @Test
    public void getBeanTest(){
        MainPageController bean = (MainPageController) beanContainer.getBean(MainPageController.class);
        //用instanceof来判断controller实例是否是MainPageController创建出来的
        Assertions.assertEquals(true,(bean instanceof MainPageController));
        //dispatcherServlet没有加注解没有被bean容器管理，所以使用bean容器获得的实例对象应该为null
        DispatcherServlet bean1 = (DispatcherServlet) beanContainer.getBean(DispatcherServlet.class);
        Assertions.assertEquals(null,bean1);
    }

    @DisplayName("根据注解获取对应的实例")
    @Order(3)
    @Test
    public void getClassesByAnnotationTest(){
        Assertions.assertEquals(true,beanContainer.isLoaded());
        Assertions.assertEquals(3,beanContainer.getClassesByAnnotation(Controller.class).size());
    }

    @DisplayName("根据接口获取实现类")
    @Order(4)
    @Test
    public void getClassesBySuperTest(){
        Assertions.assertEquals(true,beanContainer.isLoaded());
        Assertions.assertEquals(true,beanContainer.getClassesBySuper(HeadLineService.class).contains(HeadLineServiceImpl.class));
    }



}
