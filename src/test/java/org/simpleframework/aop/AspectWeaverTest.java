package org.simpleframework.aop;


import com.imooc.controller.superadmin.HeadLineOprationController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;

public class AspectWeaverTest {
    @DisplayName("织入通用逻辑测试：doAop")
    @Test
    public void doAopTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.imooc");
        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();
        HeadLineOprationController headLineOperation = (HeadLineOprationController) beanContainer.getBean(HeadLineOprationController.class);
        headLineOperation.addHeadLine(null,null,null,null);
    }
}
