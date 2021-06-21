package com.common.commonadapter.simple

import android.view.View
import com.common.commonadapter.base.ItemViewHolder

//封装head ，foot，empty ，loadMore的Holder
//内部使用
internal class SimpleViewHolder(itemView: View) : ItemViewHolder<Any>(itemView) {
    //没有业务逻辑
    override fun onBindViewHolder(data: Any, postion: Int) {}
}
