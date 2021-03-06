package com.common.commonadapter

import android.content.Context
import android.util.SparseArray
import android.view.ViewGroup
import com.common.commonadapter.base.BaseAdapter
import com.common.commonadapter.base.ItemViewHolder
import com.common.commonadapter.base.LifecycleAdapter
import com.common.commonadapter.listener.HolderCallBack
import com.common.commonadapter.listener.HolderCreater
import com.common.commonadapter.simple.SimpleViewHolder
import com.common.commonadapter.simple.TemplateData
import com.common.commonadapter.simple.UnKnowViewHolder

class CommonMuliteAdapter : LifecycleAdapter<TemplateData> {

    private var holderCreater: HolderCreater? = null
    private val callbacks = SparseArray<HolderCallBack>()
    private val holders = SparseArray<Class<out ItemViewHolder<*>>>()
    private val datas = SparseArray<Class<*>>()

    constructor(context: Context) : super(context) {}

    // holderCreater
    constructor(context: Context, holderCreater: HolderCreater) : super(context) {
        this.holderCreater = holderCreater
    }

    fun registerItemHolder(
        key: Int,
        holderClass: Class<out ItemViewHolder<*>>,
        dataClass: Class<*>
    ) {
        holders.put(key, holderClass)
        datas.put(key, dataClass)
    }

    fun registerHolderCallBack(viewType: Int, callBack: HolderCallBack) {
        callbacks.put(viewType, callBack)
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
        return getItemViewTypeByData(getItem(position - getHeaderLayoutCount()))
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
                createViewByLayoutId(
                    baseLoadMoreView!!.getLayoutId(), parent
                )
            )
            UNKNOW -> return UnKnowViewHolder(context, parent)
            else -> {
                val holder: ItemViewHolder<Any>
                if (holderCreater != null) {
                    holder = holderCreater!!.getHolder(context, parent, viewType) as ItemViewHolder<Any>
                    return holder
                } else {
                    try {
                        val vhClass = getItemViewHolderByType(viewType)
                        holder = vhClass.getConstructor(Context::class.java, ViewGroup::class.java)
                            .newInstance(context, parent) as ItemViewHolder<Any>
                        if (callbacks.get(viewType) != null) {
                            holder.setCallBack(callbacks.get(viewType))
                        }
                        return holder
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw RuntimeException("Check ViewType:" + viewType + "  Holder Constructor!!!\n" + e.toString() + "\n" + e.message)
                        }
                    }
                }
            }
        }
        return UnKnowViewHolder(context, parent)
    }

    private fun getItemViewHolderByType(viewType: Int): Class<out ItemViewHolder<*>> {
        var holder = holders.get(viewType)
        if (holder == null) {
            holder = UnKnowViewHolder::class.java
        }
        return holder
    }

    private fun getItemViewTypeByData(templateData: TemplateData?): Int {
        if (templateData == null) return UNKNOW
        val template = templateData.template
        if(holderCreater==null) {
            val data = templateData.data
            //data ?????????template ???????????????template????????????holder,??????template???????????????
            //?????????????????????T?????????????????????T??????????????????T????????????UNKNOW holder
            //??????bind??????holder??????????????????A
            if (data == null || template == -1 || holders.get(template) == null
                || !data.javaClass.equals(datas.get(template))
            ) {
                return UNKNOW
            }
        }
        return template
    }

    override fun onBindViewHolder(holder: ItemViewHolder<Any>, position: Int) {
        autoLoadMore(position)
        val viewType = holder.itemViewType
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
            UNKNOW -> {
                val dataPos = getDataPosByLayoutPos(position)
                holder.onBindViewHolder("UNKNOW", dataPos)
            }
            else -> {
                val templateData = getItemByLayoutPos(position)
                val dataPos = getDataPosByLayoutPos(position)
                if (templateData != null && templateData.data != null && dataPos >= 0) {
                    //??????????????????????????????check????????????????????????holder??????
                    // Log.e("AAAAA", templateData.data!!.javaClass.equals(holder.getRealType()).toString())
                    //????????????????????????????????????
                    //onBindViewHolder????????????data??????????????????????????????????????????
                    holder.onBindViewHolder(templateData.data!!, dataPos)
                }
            }
        }
    }

}
