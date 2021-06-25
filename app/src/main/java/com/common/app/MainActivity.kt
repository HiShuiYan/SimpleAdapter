package com.common.app

import android.content.Context
import android.content.Intent
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
    var adapter: CommonSimpleAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //两种列表构建模式
        //1
//        adapter = CommonSimpleAdapter(this, object : HolderCreater {
//            override fun getHolder(context: Context, parent: ViewGroup?, viewType: Int): ItemViewHolder<*> {
//                return HolderOne(context, parent)
//            }
//        })
        //2
        adapter = CommonSimpleAdapter(this,HolderOne::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter

        //HolderCallBack，用于Holder和外部交互
        adapter?.registerHolderCallBack(object : HolderOne.HolderOneCallback {
            override fun oneClick() {
                Log.e("AAAA", "one callback")
                val intent = Intent(this@MainActivity, TwoActivity::class.java)
                startActivity(intent)
            }
        })

        //设置listener后支持上拉加载更多
        adapter?.setLoadMoreListener(object : LoadMoreListener {
            override fun loadMore() {
                recyclerView.postDelayed(Runnable { addData() }, 2000)
            }
        })

        addData()
    }

    private fun addData() {
        val list = mutableListOf<String>()
        list.add("Hahah" + (adapter!!.getDataCount() + 1))
        list.add("Hahah" + (adapter!!.getDataCount() + 2))
        list.add("Hahah" + (adapter!!.getDataCount() + 3))
        list.add("Hahah" + (adapter!!.getDataCount() + 4))
        list.add("Hahah" + (adapter!!.getDataCount() + 5))
        list.add("Hahah" + (adapter!!.getDataCount() + 6))
        list.add("Hahah" + (adapter!!.getDataCount() + 7))
        list.add("Hahah" + (adapter!!.getDataCount() + 8))
        list.add("Hahah" + (adapter!!.getDataCount() + 9))
        list.add("Hahah" + (adapter!!.getDataCount() + 10))
        list.add("Hahah" + (adapter!!.getDataCount() + 11))
        list.add("Hahah" + (adapter!!.getDataCount() + 12))
        list.add("Hahah" + (adapter!!.getDataCount() + 13))
        list.add("Hahah" + (adapter!!.getDataCount() + 14))
        list.add("Hahah" + (adapter!!.getDataCount() + 15))
        adapter?.addDatas(list)
//        adapter?.loadMoreEnd()
        adapter?.loadMoreComplete()
    }

}