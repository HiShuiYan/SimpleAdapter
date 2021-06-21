package com.common.commonadapter.listener

import android.util.SparseArray
import com.common.commonadapter.base.ItemViewHolder

interface HolderRegister {
    fun registerHolder(
        holders: SparseArray<Class<out ItemViewHolder<*>>>,
        datas: SparseArray<Class<*>>)
}