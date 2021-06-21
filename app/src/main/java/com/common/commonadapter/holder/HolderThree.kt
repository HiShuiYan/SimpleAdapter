package com.common.commonadapter.holder

import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import com.common.commonadapter.R
import com.common.commonadapter.base.ItemViewHolder
import com.common.commonadapter.listener.HolderCallBack

class HolderThree(context: Context, parent: ViewGroup?) :
    ItemViewHolder<String>(context, parent, R.layout.holder_three) {

    interface HolderThreeCallback: HolderCallBack {
        fun oneClick()
        fun twoClick()
        fun threeClick()
    }

    override fun onBindViewHolder(data: String, postion: Int) {
        setText(R.id.tvOne, data)
        setOnClick(R.id.tvOne)
        setOnClick(R.id.tvTwo)
        setOnClick(R.id.tvThree)
    }

    override fun onViewClick(viewId: Int) {
        super.onViewClick(viewId)
        when(viewId){
            R.id.tvOne ->{
                if (holderCallback is HolderThreeCallback) {
                    (holderCallback as HolderThreeCallback).oneClick()
                }
                Toast.makeText(context,"One Click", Toast.LENGTH_SHORT).show()
            }
            R.id.tvTwo ->{
                if (holderCallback is HolderThreeCallback) {
                    (holderCallback as HolderThreeCallback).twoClick()
                }
                Toast.makeText(context,"TWo Click", Toast.LENGTH_SHORT).show()
            }
            R.id.tvThree ->{
                if (holderCallback is HolderThreeCallback) {
                    (holderCallback as HolderThreeCallback).threeClick()
                }
                Toast.makeText(context,"Three Click", Toast.LENGTH_SHORT).show()
            }
        }
    }
}