package com.common.skin.callback;


import com.common.skin.attr.SkinAttrBuilder;

/**
 * 皮肤属性捕获者
 *
 * @author LiuFeng
 * @data 2021/8/25 10:20
 */
public interface SkinAttrAcquirer {

    void capture(SkinAttrBuilder attrBuilder);
}
