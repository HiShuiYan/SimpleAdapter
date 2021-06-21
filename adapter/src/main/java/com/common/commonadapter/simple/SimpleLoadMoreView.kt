package com.common.commonadapter.simple

import com.common.commonadapter.R
import com.common.commonadapter.base.BaseLoadMoreView

//默认loadMore view，有需要自行定制
class SimpleLoadMoreView : BaseLoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.load_more_view
    }

    override fun getLoadingViewId(): Int {
        return R.id.load_more_loading_view
    }

    override fun getLoadEndViewId(): Int {
        return R.id.load_more_load_end_view
    }

}