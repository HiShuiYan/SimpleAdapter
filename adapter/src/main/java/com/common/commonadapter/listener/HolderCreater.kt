package com.common.commonadapter.listener

import android.content.Context
import android.view.ViewGroup
import com.common.commonadapter.base.ItemViewHolder

interface HolderCreater {
    fun getHolder(context: Context, parent: ViewGroup?,viewType:Int):ItemViewHolder<*>
}