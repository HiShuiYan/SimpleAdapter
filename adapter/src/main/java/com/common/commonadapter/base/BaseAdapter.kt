package com.common.commonadapter.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.common.commonadapter.listener.LoadMoreListener
import com.common.commonadapter.simple.SimpleLoadMoreView
import com.common.commonadapter.simple.SimpleViewHolder
import java.lang.ref.WeakReference

/**
 * 特别注意postion和layoutPos
 * 存在加入headLayout导致 数据postion 和 layoutPos 不一致
 * 默认返回的是数据postion，已经经过计算
 * 有必要时候可使用：getItemByLayoutPos/getDataPosByLayoutPos
 * GridLayoutManager 和 瀑布流StaggeredLayoutManager支持
 */
abstract class BaseAdapter<T>(var context: Context) : RecyclerView.Adapter<ItemViewHolder<Any>>() {
    protected val HEADER_VIEW = 0x11111111
    protected val EMPTY_VIEW = 0x22222222
    protected val FOOT_VIEW = 0x33333333
    protected val LOAD_MORE_VIEW = 0x44444444
    protected val UNKNOW = 0x55555555

    private var preLoadNumber = 1

    //header
    internal var headerLayout: LinearLayout? = null

    //empty
    internal var emptyLayout: FrameLayout? = null

    //footer
    internal var footerLayout: LinearLayout? = null

    //
    internal var baseLoadMoreView: BaseLoadMoreView? = null
    private var layoutInflater: LayoutInflater? = null

    internal var mData: MutableList<T> = mutableListOf()

    //上拉加载监听
    internal var listener: LoadMoreListener? = null

    //
    private var loadMoreEnable: Boolean = false
    var weakRecyclerView: WeakReference<RecyclerView>? = null
    //不满一屏时，是否可以继续加载的标记位
    private var mNextLoadEnable = true
    internal var headHolder: SimpleViewHolder? = null
    internal var footHolder: SimpleViewHolder? = null

    override fun getItemCount(): Int {
        var count = getHeaderLayoutCount()
        if (mData.size == 0 && emptyLayout != null) {
            count += 1
        } else {
            count += mData.size
            //上拉加载item再加1
            if (loadMoreEnable) count++
        }
        count += getFooterLayoutCount()
        return count
    }

    private fun getFootAndLoadMoreCount(): Int {
        var count = getFooterLayoutCount();
        if (loadMoreEnable) count++
        return count;
    }

    //setData
    private fun compatibilityDataSizeChanged(size: Int) {
        val dataSize = mData.size
        if (dataSize == size) {
            notifyDataSetChanged()
        }
    }

    //设置一份新的数据
    fun setDatas(@NonNull mData: MutableList<T>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    //数据替换，替换index上的数据
    fun setData(index: Int, @NonNull data: T) {
        if (index < 0) return
        mData[index] = data
        notifyItemChanged(index + getHeaderLayoutCount())
    }

    //数据插入在某个index后面插入一个数据
    fun addData(position: Int, @NonNull data: T) {
        if (position < 0 || position > mData.size) return
        mData.add(position, data)
        notifyItemInserted(position + getHeaderLayoutCount())
        compatibilityDataSizeChanged(1)
    }

    //添加单条数据
    fun addData(@NonNull data: T) {
        this.mData.add(data)
        notifyItemInserted(mData.size + getHeaderLayoutCount())
        compatibilityDataSizeChanged(1)
    }

    fun remove(position: Int) {
        if (position < 0 || position > mData.size - 1) return
        mData.removeAt(position)
        notifyItemRemoved(position + getHeaderLayoutCount())
        compatibilityDataSizeChanged(0)
    }

    fun removeAll() {
        mData.clear()
        notifyDataSetChanged()
    }

    //添加更多数据
    fun addDatas(@NonNull newData: Collection<T>) {
        mData.addAll(newData)
        //上拉加载添加数据时候停止滚动动画
        if (weakRecyclerView != null && weakRecyclerView?.get() != null) {
            weakRecyclerView?.get()?.stopScroll()
        }
        notifyItemRangeInserted(mData.size - newData.size + getHeaderLayoutCount(), newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    fun getItem(position: Int): T? {
        return if (position >= 0 && position < mData.size)
            mData[position]
        else
            null
    }

    fun getDatas(): MutableList<T> {
        return mData
    }

    //有header和footer时候，layoutPos和dataPos不一致
    fun getItemByLayoutPos(layoutPos: Int): T? {
        val pos = layoutPos - getHeaderLayoutCount();
        if (pos >= 0 && layoutPos < (itemCount - getFootAndLoadMoreCount())) {
            return getItem(pos);
        }
        return null
    }

    //-1  表示 foot，head
    fun getDataPosByLayoutPos(layoutPos: Int): Int {
        val pos = layoutPos - getHeaderLayoutCount();
        if (pos >= 0 && layoutPos < (itemCount - getFootAndLoadMoreCount())) {
            return pos;
        }
        return -1
    }

    //数据个数
    fun getDataCount(): Int {
        return mData.size
    }

    //布局创建View
    protected fun createViewByLayoutId(@LayoutRes layoutResId: Int): View {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        if (weakRecyclerView != null && weakRecyclerView?.get() != null) {
            return layoutInflater!!.inflate(layoutResId, weakRecyclerView?.get(), false)
        }
        return layoutInflater!!.inflate(layoutResId, null)
    }

    protected fun createViewByLayoutId(@LayoutRes layoutResId: Int, parent: ViewGroup): View {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        return layoutInflater!!.inflate(layoutResId, parent, false)
    }

    //setEmptyView
    fun setEmptyView(@LayoutRes layoutResId: Int) {
        setEmptyView(createViewByLayoutId(layoutResId))
    }

    //emptyView大小由外部决定
    fun setEmptyView(emptyView: View) {
        var insert = false
        if (emptyLayout == null) {
            emptyLayout = FrameLayout(context)
            val lp = emptyView.layoutParams
            if (lp != null) {
                val layoutParams = ViewGroup.LayoutParams(lp.width, lp.height)
                emptyLayout!!.setLayoutParams(layoutParams)
            }
            insert = true
        }
        emptyLayout!!.removeAllViews()
        emptyLayout!!.addView(emptyView)
        if (insert) {
            notifyDataSetChanged()
        }
    }

    //Add/Remove Head
    internal fun getHeaderLayoutCount(): Int {
        return if (headerLayout == null || headerLayout!!.getChildCount() == 0) {
            0
        } else 1
    }

    fun removeAllHeader() {
        if (headerLayout != null) {
            headerLayout!!.removeAllViews()
        }
    }

    fun removeHeader(index: Int) {
        if (index >= 0 && headerLayout != null && index < headerLayout!!.childCount) {
            headerLayout!!.removeViewAt(index)
        }
    }

    fun addHeaderView(@LayoutRes headerRes: Int, orientation: Int): Int {
        return addHeaderView(createViewByLayoutId(headerRes), orientation)
    }

    fun addHeaderView(header: View, orientation: Int): Int {
        return setHeaderView(header, orientation, 0)
    }

    fun addHeaderView(@LayoutRes headerRes: Int, orientation: Int, index: Int): Int {
        return addHeaderView(createViewByLayoutId(headerRes), orientation, index)
    }

    fun addHeaderView(header: View, orientation: Int, index: Int): Int {
        return setHeaderView(header, orientation, index)
    }

    private fun setHeaderView(header: View, orientation: Int, index: Int): Int {
        var insertIndex = index
        var isAlreadyInitLayout = true
        if (headerLayout == null) {
            isAlreadyInitLayout = false
            headerLayout = LinearLayout(context)
            if (orientation == LinearLayout.VERTICAL) {
                headerLayout?.setOrientation(LinearLayout.VERTICAL)
                headerLayout?.setLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            } else {
                headerLayout?.setOrientation(LinearLayout.HORIZONTAL)
                headerLayout?.setLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
            }

        }
        val childCount = headerLayout!!.getChildCount()
        if (insertIndex < 0 || insertIndex > childCount) {
            insertIndex = childCount
        }
//        else if (insertIndex >= 0 && insertIndex < childCount) {
//            headerLayout!!.removeViewAt(insertIndex)
//        }
        headerLayout!!.addView(header, insertIndex)

        if (!isAlreadyInitLayout) {
            notifyItemChanged(0)
        }
        return insertIndex
    }

    //Add/Remove Footer
    internal fun getFooterLayoutCount(): Int {
        return if (footerLayout == null || footerLayout!!.getChildCount() == 0) {
            0
        } else 1
    }

    fun removeAllFooter() {
        if (footerLayout != null) {
            footerLayout!!.removeAllViews()
        }
    }

    fun removeFooter(index: Int) {
        if (index >= 0 && footerLayout != null && index < footerLayout!!.childCount) {
            footerLayout!!.removeViewAt(index)
        }
    }

    fun addFooterView(@LayoutRes footerRes: Int, orientation: Int): Int {
        return addFooterView(createViewByLayoutId(footerRes), orientation)
    }

    fun addFooterView(footer: View, orientation: Int): Int {
        return setFooterView(footer, orientation, 0)
    }

    fun addFooterView(@LayoutRes footerRes: Int, orientation: Int, index: Int): Int {
        return addFooterView(createViewByLayoutId(footerRes), orientation, index)
    }

    fun addFooterView(footer: View, orientation: Int, index: Int): Int {
        return setFooterView(footer, orientation, index)
    }

    private fun setFooterView(footer: View, orientation: Int, index: Int): Int {
        var insertIndex = index
        var isAlreadyInitLayout = true
        if (footerLayout == null) {
            isAlreadyInitLayout = false
            footerLayout = LinearLayout(context)
            if (orientation == LinearLayout.VERTICAL) {
                footerLayout?.setOrientation(LinearLayout.VERTICAL)
                footerLayout?.setLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            } else {
                footerLayout?.setOrientation(LinearLayout.HORIZONTAL)
                footerLayout?.setLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
            }

        }
        val childCount = footerLayout!!.getChildCount()
        if (insertIndex < 0 || insertIndex > childCount) {
            insertIndex = childCount
        }
//        else if (insertIndex >= 0 && insertIndex < childCount) {
//            footerLayout!!.removeViewAt(insertIndex)
//        }

        footerLayout!!.addView(footer, insertIndex)

        if (!isAlreadyInitLayout) {
            notifyItemChanged(getItemCount() - 1)
        }
        return insertIndex
    }

    //Add LoadView
    fun setLoadMoreListener(listener: LoadMoreListener) {
        this.listener = listener
        setLoadMoreEnable(true);
    }

    fun setLoadMoreEnable(enable: Boolean) {
        val position = itemCount
        if (enable) {
            if (baseLoadMoreView == null) {
                baseLoadMoreView = SimpleLoadMoreView()
            }
            notifyItemInserted(position)
        } else {
            if (baseLoadMoreView != null && loadMoreEnable) {
                notifyItemRemoved(position)
            }
        }
        this.loadMoreEnable = enable
    }

    fun isLoadMoreEnable(): Boolean {
        return loadMoreEnable
    }

    fun setLoadMoreView(baseLoadMoreView: BaseLoadMoreView) {
        this.baseLoadMoreView = baseLoadMoreView
    }

    private fun getLoadMoreViewCount(): Int {
        if (!loadMoreEnable || baseLoadMoreView == null || listener == null) {
            return 0
        }
        return if (mData.size == 0) {
            0
        } else 1
    }

    //主动触发loadmore
    internal fun autoLoadMore(position: Int) {
        if (getLoadMoreViewCount() == 0) return
        if (position < itemCount - preLoadNumber) return
        if (baseLoadMoreView!!.getLoadMoreStatus() != BaseLoadMoreView.STATUS_DEFAULT) return
        if (!mNextLoadEnable) return

        baseLoadMoreView!!.setLoadMoreStatus(BaseLoadMoreView.STATUS_LOADING)
        listener!!.loadMore()
    }

    //所有数据加载完毕
    fun loadMoreEnd() {
        baseLoadMoreView!!.setLoadMoreStatus(BaseLoadMoreView.STATUS_END)
        if (getLoadMoreViewCount() == 0) {
            return
        }
        notifyItemChanged(itemCount - 1)
    }

    //单次执行完毕
    fun loadMoreComplete() {
        baseLoadMoreView!!.setLoadMoreStatus(BaseLoadMoreView.STATUS_DEFAULT)
        if (getLoadMoreViewCount() == 0) {
            return
        }
        notifyItemChanged(itemCount - 1)
        checkDisableLoadMoreIfNotFullPage()
    }

    //加载完毕检查是否满一屏，是否需要开启上拉加载
    fun checkDisableLoadMoreIfNotFullPage() {
        if (weakRecyclerView == null) return
        // 先把标记位设置为false
        mNextLoadEnable = false
        val recyclerView = weakRecyclerView?.get() ?: return
        val manager = recyclerView.layoutManager ?: return
        if (manager is LinearLayoutManager) {
            recyclerView.postDelayed({
                if (isFullScreen(manager)) {
                    mNextLoadEnable = true
                }
            }, 50)
        } else if (manager is StaggeredGridLayoutManager) {
            recyclerView.postDelayed({
                val positions = IntArray(manager.spanCount)
                manager.findLastCompletelyVisibleItemPositions(positions)
                val pos = getTheBiggestNumber(positions) + 1
                if (pos != itemCount) {
                    mNextLoadEnable = true
                }
            }, 50)
        }
    }

    private fun isFullScreen(llm: LinearLayoutManager): Boolean {
        return (llm.findLastCompletelyVisibleItemPosition() + 1) != itemCount ||
                llm.findFirstCompletelyVisibleItemPosition() != 0
    }

    private fun getTheBiggestNumber(numbers: IntArray?): Int {
        var tmp = -1
        if (numbers == null || numbers.isEmpty()) {
            return tmp
        }
        for (num in numbers) {
            if (num > tmp) {
                tmp = num
            }
        }
        return tmp
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        weakRecyclerView = WeakReference(recyclerView)
        resetHolderSpanCountForGrid(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        weakRecyclerView = null
    }

    //window事件传递到holder
    override fun onViewAttachedToWindow(holder: ItemViewHolder<Any>) {
        super.onViewAttachedToWindow(holder)
        holder.attachWindow()
        resetHolderSpanCountForStaggered(holder)
    }

    override fun onViewDetachedFromWindow(holder: ItemViewHolder<Any>) {
        super.onViewDetachedFromWindow(holder)
        holder.detachWindow()
    }

    //GridLayoutManager
    private fun resetHolderSpanCountForGrid(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val defSpanSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    //保持特定类型Holder 全行
                    val viewType = getItemViewType(position)
                    if (viewType == HEADER_VIEW || viewType == FOOT_VIEW || viewType == EMPTY_VIEW || viewType == LOAD_MORE_VIEW) {
                        return layoutManager.spanCount
                    }
                    return defSpanSizeLookup.getSpanSize(position)
                }
            }
        }
    }

    //瀑布流 StaggeredGridLayoutManager
    private fun resetHolderSpanCountForStaggered(holder: ItemViewHolder<Any>) {
        val lp = holder.itemView.getLayoutParams()
        if (lp is StaggeredGridLayoutManager.LayoutParams) {
            //充满一行应该
            val recyclerView = weakRecyclerView?.get() ?: return
            val layoutManager = recyclerView.layoutManager ?: return
            if (layoutManager is StaggeredGridLayoutManager && holder.getSpanCount() == layoutManager.getSpanCount()) {
                lp.setFullSpan(true)
            }
        }
    }

    fun getLoadMoreStatus(): Int {
        return baseLoadMoreView!!.getLoadMoreStatus()
    }
}

