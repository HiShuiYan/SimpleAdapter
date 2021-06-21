package com.common.commonadapter.holder

import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import com.common.commonadapter.R
import com.common.commonadapter.base.ItemViewHolder
import com.common.commonadapter.listener.HolderCallBack

class HolderOne(context: Context, parent: ViewGroup?) :
    ItemViewHolder<String>(context, parent, R.layout.holder_one) {

    interface HolderOneCallback : HolderCallBack {
        fun oneClick()
    }

    override fun onBindViewHolder(data: String, postion: Int) {
        setText(R.id.tvOne, data)
        setOnClick(R.id.tvOne)
    }

    override fun onViewClick(viewId: Int) {
        super.onViewClick(viewId)
        when (viewId) {
            R.id.tvOne -> {
                if (holderCallback is HolderOneCallback) {
                    (holderCallback as HolderOneCallback).oneClick()
                }
                Toast.makeText(context,"One Click",Toast.LENGTH_SHORT).show()
            }
        }
    }

}