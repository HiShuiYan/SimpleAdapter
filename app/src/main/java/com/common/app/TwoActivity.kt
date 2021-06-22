package com.common.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.app.holder.HolderOne
import com.common.app.holder.HolderThree
import com.common.app.holder.HolderTwo
import com.common.commonadapter.CommonMuliteAdapter
import com.common.commonadapter.base.ItemViewHolder
import com.common.commonadapter.listener.HolderCreater
import com.common.commonadapter.listener.LoadMoreListener
import com.common.commonadapter.simple.TemplateData
import com.common.commonadapter.simple.UnKnowViewHolder


class TwoActivity : AppCompatActivity() {
    var adapter: CommonMuliteAdapter?=null
    val TYPE_ONE = 1
    val TYPE_TWO = 2
    val TYPE_THREE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

        adapter = CommonMuliteAdapter(this,object:HolderCreater{
            override fun getHolder(context: Context, parent: ViewGroup?, viewType: Int): ItemViewHolder<*> {
                when(viewType){
                    TYPE_ONE ->  return HolderOne(context,parent)
                    TYPE_TWO ->  return HolderTwo(context,parent)
                    TYPE_THREE ->  return HolderThree(context,parent)
                    else -> return UnKnowViewHolder(context,parent)
                }
            }
        })

//        adapter = CommonMuliteAdapter(this)
//        adapter?.registerItemHolder(TYPE_ONE,HolderOne::class.java,String::class.java)
//        adapter?.registerItemHolder(TYPE_TWO,HolderTwo::class.java,String::class.java)
//        adapter?.registerItemHolder(TYPE_THREE,HolderThree::class.java,String::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        adapter?.registerHolderCallBack(TYPE_ONE,object:HolderOne.HolderOneCallback{
            override fun oneClick() {
                Log.e("AAAA","one callback")
            }
        })
        adapter?.registerHolderCallBack(TYPE_TWO,object:HolderTwo.HolderTwoCallback{
            override fun oneClick() {
            }

            override fun twoClick() {
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
        val list = mutableListOf<TemplateData>()
        list.add(TemplateData(TYPE_ONE,"Hahah"+(adapter!!.getDataCount()+1)))
        list.add(TemplateData(TYPE_TWO,"Hahah"+(adapter!!.getDataCount()+2)))
        list.add(TemplateData(TYPE_ONE,"Hahah"+(adapter!!.getDataCount()+3)))
        list.add(TemplateData(TYPE_ONE,"Hahah"+(adapter!!.getDataCount()+4)))
        list.add(TemplateData(TYPE_THREE,"Hahah"+(adapter!!.getDataCount()+5)))
        list.add(TemplateData(TYPE_THREE,"Hahah"+(adapter!!.getDataCount()+6)))
        list.add(TemplateData(TYPE_THREE,"Hahah"+(adapter!!.getDataCount()+7)))
        list.add(TemplateData(TYPE_TWO,"Hahah"+(adapter!!.getDataCount()+8)))
        list.add(TemplateData(TYPE_ONE,"Hahah"+(adapter!!.getDataCount()+9)))
        list.add(TemplateData(TYPE_TWO,"Hahah"+(adapter!!.getDataCount()+10)))
        list.add(TemplateData(TYPE_ONE,"Hahah"+(adapter!!.getDataCount()+11)))
        list.add(TemplateData(TYPE_THREE,"Hahah"+(adapter!!.getDataCount()+12)))
        list.add(TemplateData(TYPE_TWO,"Hahah"+(adapter!!.getDataCount()+13)))
        list.add(TemplateData(TYPE_THREE,"Hahah"+(adapter!!.getDataCount()+14)))
        list.add(TemplateData(TYPE_THREE,"Hahah"+(adapter!!.getDataCount()+15)))
        adapter?.addDatas(list)
        adapter?.loadMoreComplete()
    }

}
