package com.common.commonadapter.simple

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.ViewGroup
import com.common.commonadapter.R
import com.qts.common.adapter.BuildConfig
import com.common.commonadapter.base.ItemViewHolder


//未知Type的Holder
//Debug 模式，会有提醒
class UnKnowViewHolder(context: Context, parent: ViewGroup?) :
        ItemViewHolder<Any>(context, parent, R.layout.holder_unknow) {

    override fun onBindViewHolder(data: Any, postion: Int) {
        if (BuildConfig.DEBUG) {
            setText(R.id.tvUnknow, "UNKNOW")
            setBackgroundColor(
                    R.id.tvUnknow,
                    ContextCompat.getColor(itemView.context, android.R.color.holo_red_light)
            )
        }
    }

}


