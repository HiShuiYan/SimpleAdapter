package com.common.commonadapter.base

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

//加载更多View，默认不实现error状态
//有需要自行添加
abstract class BaseLoadMoreView {

    private var loadMoreStatus =
            STATUS_DEFAULT

    fun setLoadMoreStatus(loadMoreStatus: Int) {
        this.loadMoreStatus = loadMoreStatus
    }

    fun getLoadMoreStatus(): Int {
        return loadMoreStatus
    }

    internal fun convert(holder: ItemViewHolder<Any>) {
        when (loadMoreStatus) {
            STATUS_LOADING -> {
                visibleLoading(holder, true)
                visibleLoadEnd(holder, false)
            }
            STATUS_END -> {
                visibleLoading(holder, false)
                visibleLoadEnd(holder, true)
            }
            STATUS_DEFAULT -> {
                visibleLoading(holder, false)
                visibleLoadEnd(holder, false)
            }
        }
    }

    private fun visibleLoading(holder: ItemViewHolder<Any>, visible: Boolean) {
        holder.setVisible(getLoadingViewId(), visible)
    }

    private fun visibleLoadEnd(holder: ItemViewHolder<Any>, visible: Boolean) {
        val loadEndViewId = getLoadEndViewId()
        if (loadEndViewId != 0) {
            holder.setVisible(loadEndViewId, visible)
        }
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    @IdRes
    protected abstract fun getLoadingViewId(): Int

    @IdRes
    protected abstract fun getLoadEndViewId(): Int

    companion object {
        //正常
        val STATUS_DEFAULT = 0
        //加载中
        val STATUS_LOADING = 1
        //所有数据加载完成
        val STATUS_END = 2
    }
}

