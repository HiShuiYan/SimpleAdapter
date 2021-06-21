package com.common.app.holder

import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import com.common.app.R
import com.common.commonadapter.base.ItemViewHolder
import com.common.commonadapter.listener.HolderCallBack

class HolderTwo(context: Context, parent: ViewGroup?) :
    ItemViewHolder<String>(context, parent, R.layout.holder_two) {

    interface HolderTwoCallback: HolderCallBack {
        fun oneClick()
        fun twoClick()
    }

    override fun onBindViewHolder(data: String, postion: Int) {
        setText(R.id.tvOne, data)
        setOnClick(R.id.tvOne)
        setOnClick(R.id.tvTwo)
    }

    override fun onViewClick(viewId: Int) {
        super.onViewClick(viewId)
        when(viewId){
            R.id.tvOne ->{
                if (holderCallback is HolderTwoCallback) {
                    (holderCallback as HolderTwoCallback).oneClick()
                }
                Toast.makeText(context,"One Click", Toast.LENGTH_SHORT).show()
            }
            R.id.tvTwo ->{
                if (holderCallback is HolderTwoCallback) {
                    (holderCallback as HolderTwoCallback).twoClick()
                }
                Toast.makeText(context,"TWo Click",Toast.LENGTH_SHORT).show()
            }
        }
    }

}