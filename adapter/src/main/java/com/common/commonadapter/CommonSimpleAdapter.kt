package com.qts.common.commonadapter

import android.content.Context
import android.view.ViewGroup
import com.common.commonadapter.base.BaseAdapter
import com.common.commonadapter.base.ItemViewHolder
import com.common.commonadapter.base.LifecycleAdapter
import com.common.commonadapter.listener.HolderCallBack
import com.common.commonadapter.listener.HolderCreater
import com.common.commonadapter.simple.SimpleViewHolder

class CommonSimpleAdapter<T> : LifecycleAdapter<T> {

    // 通过 Holder反射创建
    constructor(context: Context,holderClass: Class<out ItemViewHolder<*>>) : super(context) {
        this.holderClass = holderClass
    }

    // holderCreater
    constructor(context: Context,holderCreater: HolderCreater) : super(context) {
        this.holderCreater = holderCreater
    }

    private var holderClass: Class<out ItemViewHolder<*>>? = null
    private var holderCreater: HolderCreater? = null
    private var itemClickListener: OnItemClickListener? = null
    private var callback: HolderCallBack? = null

    fun registerHolderCallBack(callBack: HolderCallBack) {
        this.callback = callBack
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            if (getHeaderLayoutCount() > 0) {
                return HEADER_VIEW
            } else if (getDataCount() == 0 && emptyLayout != null) {
                return EMPTY_VIEW
            }
        } else if (position == 1) {
            if (getHeaderLayoutCount() > 0 && getDataCount() == 0 && emptyLayout != null) {
                return EMPTY_VIEW
            }
        }

        if (position == itemCount - 1) {
            if (isLoadMoreEnable()) {
                return LOAD_MORE_VIEW
            } else if (getFooterLayoutCount() > 0) {
                return FOOT_VIEW
            }
        } else if (position == itemCount - 2) {
            if (isLoadMoreEnable() && getFooterLayoutCount() > 0) {
                return FOOT_VIEW
            }
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<Any> {
        when (viewType) {
            HEADER_VIEW -> {
                if (headHolder == null) {
                    headHolder = SimpleViewHolder(headerLayout!!);
                }
                return headHolder!!
            }
            FOOT_VIEW -> {
                if (footHolder == null) {
                    footHolder = SimpleViewHolder(footerLayout!!);
                }
                return footHolder!!
            }
            EMPTY_VIEW -> return SimpleViewHolder(emptyLayout!!)
            LOAD_MORE_VIEW -> return SimpleViewHolder(
                createViewByLayoutId(baseLoadMoreView!!.getLayoutId(), parent)
            )
            else -> {
                val holder: ItemViewHolder<Any>
                if (holderCreater!=null) {
                    holder = holderCreater!!.getHolder(context,parent,viewType) as ItemViewHolder<Any>
                } else {
                    try {
                        holder = holderClass!!.getConstructor(Context::class.java, ViewGroup::class.java).newInstance(context, parent) as ItemViewHolder<Any>
                    } catch (e: Exception) {
                        throw RuntimeException("Check Holder Constructor!!!")
                    }
                }

                //设置itemClick
                holder.itemView.setOnClickListener {
                    if (itemClickListener != null) {
                        holder.onViewClick(holder.itemView.id)
                        val position = holder.layoutPosition - getHeaderLayoutCount();
                        itemClickListener!!.onItemClick(position, holder.layoutPosition)
                    }
                }
                //设置callback
                if (callback != null) {
                    holder.setCallBack(callback!!)
                }
                return holder
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<Any>, position: Int) {
        autoLoadMore(position)
        val viewType = holder.getItemViewType()
        when (viewType) {
            HEADER_VIEW -> {
            }
            EMPTY_VIEW -> {
            }
            FOOT_VIEW -> {
            }
            LOAD_MORE_VIEW -> {
                baseLoadMoreView!!.convert(holder)
            }
            else -> {
                val data = getItemByLayoutPos(position)
                val dataPos = getDataPosByLayoutPos(position)
                if (data != null && dataPos >= 0) {
                    holder.onBindViewHolder(data, dataPos)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(dataPos: Int, holderPos: Int)
    }

}







