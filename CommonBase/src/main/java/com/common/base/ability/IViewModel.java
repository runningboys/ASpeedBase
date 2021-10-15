package com.common.base.ability;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * ViewModel集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:47
 */
public interface IViewModel<V extends ViewModel> extends ViewModelStoreOwner {

    /**
     * 创建ViewModel
     *
     * @return
     */
    V createViewModel();

    /**
     * 创建ViewModel
     *
     * @param clazz
     * @param <T>
     * @return
     */
    default V createViewModel(Class<V> clazz) {
        return new ViewModelProvider(this).get(clazz);
    }
}
