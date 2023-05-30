package com.util.base.skin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.common.skin.base.SkinDialog

/**
 * 自定义弹窗
 *
 * @author LiuFeng
 * @data 2021/9/9 17:22
 */
class CustomDialog(context: Context) : SkinDialog(context) {

    class CustomBuilder(private val mContext: Context) {
        private var mLayoutId = 0
        private var view: View? = null

        fun setContentView(@LayoutRes layoutId: Int): CustomBuilder {
            mLayoutId = layoutId
            return this
        }

        fun setContentView(view: View?): CustomBuilder {
            this.view = view
            return this
        }

        fun create(): SkinDialog {
            val dialog = CustomDialog(mContext)
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(mLayoutId, null)
            }
            dialog.setContentView(view!!)
            return dialog
        }
    }
}