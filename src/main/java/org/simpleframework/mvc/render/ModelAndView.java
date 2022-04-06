package org.simpleframework.mvc.render;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储处理完后的结果数据以及显示该数据的视图
 */
public class ModelAndView {
    @Getter
    private String view;//页面所在的路径
    @Getter
    private Map<String,Object> model=new HashMap<>();//页面的data数据

    public ModelAndView setView(String view){
        this.view=view;
        return this;
    }

    public ModelAndView addViewData(String attributeName,Object attributeValue){
        model.put(attributeName,attributeValue);
        return this;
    }

}
