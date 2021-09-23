package com.util.base.skin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.common.skin.api.SkinAutoChange
import com.common.skin.api.SkinManager
import com.common.skin.attr.SkinAttrType
import com.common.skin.base.SkinActivity
import com.util.base.R
import com.util.base.databinding.ActivityTestSkinBinding

class TestSkinActivity : SkinActivity() {
    lateinit var binding: ActivityTestSkinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestSkinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        initData()
    }

    private fun initData() {

    }

    private fun initListener() {
        binding.skinChangeBtn.setOnClickListener {
            SkinAutoChange.changeSkin()
        }

        binding.dialogBtn.setOnClickListener { showDialog() }

        binding.addViewBtn.setOnClickListener {
            val textView: TextView = TextView(this)
            textView.text = "hello world"
            textView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.skin_common_background
                )
            )
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.skin_common_title_text_color
                )
            )
            SkinManager.getInstance().loadSkinAttr(textView) {
                it.saveAttr(SkinAttrType.BACKGROUND, R.color.skin_common_background)
                    .saveAttr(SkinAttrType.TEXT_COLOR, R.color.skin_common_title_text_color)
            }
            binding.root.addView(textView)
        }
    }

    private fun showDialog() {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_ios_confirm, null)
        val title = layout.findViewById<View>(R.id.dialog_title) as TextView
        val message = layout.findViewById<View>(R.id.dialog_message) as TextView
        val positive = layout.findViewById<View>(R.id.button_pos) as Button
        val negative = layout.findViewById<View>(R.id.button_neg) as Button

        title.text = "Hello World"
        message.text = "你好啊！"


        val dialog = CustomDialog.CustomBuilder(this).setContentView(layout).create()

        negative.setOnClickListener {
            dialog.dismiss()
        }

        positive.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}