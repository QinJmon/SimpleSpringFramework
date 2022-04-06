package org.simpleframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Set;

public class ClassUtilTest {

    @Test
    public void extractPackageClassTest(){
        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.imooc.entity");
        System.out.println(classSet);
        Assertions.assertEquals(4,classSet.size());
    }
}
