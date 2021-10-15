package com.common.base.ability;

import androidx.viewbinding.ViewBinding;

/**
 * ViewBinding集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:47
 */
public interface IBinding<B extends ViewBinding> {

    /**
     * 获取ViewBinding
     *
     * @return
     */
    B getViewBinding();
}
