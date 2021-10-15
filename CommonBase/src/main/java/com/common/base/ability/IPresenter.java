package com.common.base.ability;

import com.common.base.BasePresenter;

/**
 * Presenter集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 15:49
 */
public interface IPresenter<P extends BasePresenter> {

    /**
     * 创建Presenter
     *
     * @return
     */
    P createPresenter();
}
