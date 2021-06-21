package com.common.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.commonadapter.base.ItemViewHolder
import com.common.app.holder.HolderOne
import com.common.commonadapter.listener.HolderCreater
import com.common.commonadapter.listener.LoadMoreListener
import com.qts.common.commonadapter.CommonSimpleAdapter

class MainActivity : AppCompatActivity() {
    var adapter:CommonSimpleAdapter<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = CommonSimpleAdapter(this,object:HolderCreater{
            override fun getHolder(
                context: Context,
                parent: ViewGroup?,
                viewType: Int
            ): ItemViewHolder<*> {
                return HolderOne(context, parent)
            }
        })

//        adapter = CommonSimpleAdapter(this,HolderOne::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        adapter?.registerHolderCallBack(object: HolderOne.HolderOneCallback{
            override fun oneClick() {
                Log.e("AAAA","one callback")
            }
        })

        adapter?.setLoadMoreListener(object:LoadMoreListener{
            override fun loadMore() {
                recyclerView.postDelayed(Runnable { addData() },2000)
            }
        })
        addData()
    }

    private fun addData(){
        val list = mutableListOf<String>()
        list.add("Hahah"+(adapter!!.getDataCount()+1))
        list.add("Hahah"+(adapter!!.getDataCount()+2))
        list.add("Hahah"+(adapter!!.getDataCount()+3))
        list.add("Hahah"+(adapter!!.getDataCount()+4))
        list.add("Hahah"+(adapter!!.getDataCount()+5))
        list.add("Hahah"+(adapter!!.getDataCount()+6))
        list.add("Hahah"+(adapter!!.getDataCount()+7))
        list.add("Hahah"+(adapter!!.getDataCount()+8))
        list.add("Hahah"+(adapter!!.getDataCount()+9))
        list.add("Hahah"+(adapter!!.getDataCount()+10))
        list.add("Hahah"+(adapter!!.getDataCount()+11))
        list.add("Hahah"+(adapter!!.getDataCount()+12))
        list.add("Hahah"+(adapter!!.getDataCount()+13))
        list.add("Hahah"+(adapter!!.getDataCount()+14))
        list.add("Hahah"+(adapter!!.getDataCount()+15))
        adapter?.addDatas(list)
        adapter?.loadMoreComplete()
    }

}