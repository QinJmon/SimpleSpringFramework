package org.simpleframework.inject;

import com.imooc.controller.frontend.MainPageController;
import com.imooc.service.combine.HeadLineShopCategoryCombineServiceImpl;
import com.imooc.service.combine.HeadLineShopCategoryServiceImpl2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;

public class DependencyInjectorTest {

    @Test
    @DisplayName("依赖注入doIoc")
    public void doIocTest(){
        //1、获取容器实例
        BeanContainer beanContainer = BeanContainer.getInstance();
        //2、指定范围将被注解标记的类交给容器管理加载
        beanContainer.loadBeans("com.imooc");
        //3、先判断容器是否被加载，加载后就可以获得bean实例
        Assertions.assertEquals(true,beanContainer.isLoaded());
        //4、判断是否是MainPageController创建的实例
        MainPageController bean = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true,(bean instanceof MainPageController));

        //6、因为没调用doIoc，所以成员变量的实例应该为null
        Assertions.assertEquals(null,bean.getHeadLineShopCategoryCombineService());
        //7、调用doIoc，进行依赖注入
        new DependencyInjector().doIoc();
        Assertions.assertNotEquals(null,bean.getHeadLineShopCategoryCombineService());
        Assertions.assertEquals(true,bean.getHeadLineShopCategoryCombineService() instanceof HeadLineShopCategoryCombineServiceImpl);
        Assertions.assertEquals(false,bean.getHeadLineShopCategoryCombineService() instanceof HeadLineShopCategoryServiceImpl2);
    }
}
