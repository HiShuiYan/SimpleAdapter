package com.common.commonadapter.base

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class LifecycleAdapter<T>(context: Context) : BaseAdapter<T>(context) {

    private var layoutManager: RecyclerView.LayoutManager? = null
    var layoutPos = intArrayOf(-1, -1)

    private fun getStartAndEnd(): IntArray {
        if (layoutManager is LinearLayoutManager) {
            layoutPos[0] = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            layoutPos[1] = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        } else if (layoutManager is GridLayoutManager) {
            layoutPos[0] = (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            layoutPos[1] = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val starts = IntArray((layoutManager as StaggeredGridLayoutManager).spanCount)
            val ends = IntArray((layoutManager as StaggeredGridLayoutManager).spanCount)
            (layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(starts)
            (layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(ends)
            findRange(starts, ends)
        }
        return layoutPos
    }

    private fun findRange(startPos: IntArray, endPos: IntArray) {
        var start = -1
        var end = -1
        if (startPos.size > 0 && endPos.size > 0) {
            start = startPos[0]
            end = endPos[0]
            for (i in 1 until startPos.size) {
                if (start > startPos[i]) {
                    start = startPos[i]
                }
            }
            for (i in 1 until endPos.size) {
                if (end < endPos[i]) {
                    end = endPos[i]
                }
            }
        }
        layoutPos[0] = start
        layoutPos[1] = end
    }

    fun onPageResume() {
        try {
            if (layoutManager == null || weakRecyclerView == null || weakRecyclerView?.get() == null) return

            getStartAndEnd()
            val start = layoutPos[0]
            val end = layoutPos[1]

            if (start < 0) return
            for (index in start..end) {
                val view = layoutManager!!.findViewByPosition(index) ?: continue
                val holder = weakRecyclerView?.get()?.getChildViewHolder(view)
                if (holder is ItemViewHolder<*>) {
                    holder.onPageResume()
                }
            }
        } catch (e: Exception) { }
    }

    fun onPagePause() {
        try {
            if (weakRecyclerView == null || weakRecyclerView?.get() == null) return

            for (index in 0..weakRecyclerView?.get()!!.childCount) {
                val view = weakRecyclerView?.get()!!.getChildAt(index) ?: continue
                val holder = weakRecyclerView?.get()?.getChildViewHolder(view)
                if (holder is ItemViewHolder<*>) {
                    holder.onPagePause()
                }
            }
        } catch (e: Exception) { }
    }

}