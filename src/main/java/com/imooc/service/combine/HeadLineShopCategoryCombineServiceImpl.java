package com.imooc.service.combine;

import com.imooc.entity.bo.HeadLine;
import com.imooc.entity.bo.ShopCategory;
import com.imooc.entity.dto.MainPageInfo;
import com.imooc.entity.dto.Result;
import com.imooc.service.solo.HeadLineService;
import com.imooc.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.inject.annotation.Autowired;

import java.util.List;

@Service
public class HeadLineShopCategoryCombineServiceImpl implements HeadLineShopCategoryCombineService {

    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private ShopCategoryService shopCategoryService;

    @Override
    public Result<MainPageInfo> getMainPageInfo() {
        //1、获得头条列表
        HeadLine headLineCondition=new HeadLine();
        headLineCondition.setEnableStatus(1);
        Result<List<HeadLine>> headLineList
                = headLineService.queryHeadLine(headLineCondition, 1, 4);
        //2、获得商铺类别列表
        ShopCategory shopCategory=new ShopCategory();
        Result<List<ShopCategory>> shopCategoryList
                = shopCategoryService.queryShopCategory(shopCategory, 1, 100);//将所有的父类别为null的商铺都查询出来

        //3、合并两者信息
       Result<MainPageInfo> result=
               mergeMainPageInfoResult(headLineList,shopCategoryList);
        return result;
    }

    private Result<MainPageInfo> mergeMainPageInfoResult(Result<List<HeadLine>> headLineList, Result<List<ShopCategory>> shopCategoryList) {
        return null;
    }
}
