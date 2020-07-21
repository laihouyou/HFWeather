package com.gddst.webapp.wanandroid.ui

import android.content.Context
import android.os.Bundle
import android.view.View.OVER_SCROLL_NEVER
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter
import com.gddst.webapp.wanandroid.R
import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.ProgressStyle
import kotlinx.android.synthetic.main.activity_wan_android_main.*

class WanAndroidMainActivity : AppCompatActivity() {

    var adapter: CommonRecycleViewAdapter<String>?=null
    var mAdapter: CommonRecycleViewAdapter<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wan_android_main)
        initRecyclerView()
    }

    private fun initRecyclerView() {

//        adapter=CommonRecycleViewAdapter<String>(this,)

        recyclerView.adapter=mAdapter

        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.overScrollMode=OVER_SCROLL_NEVER

        recyclerView.addItemDecoration(getItemDecoration())

        recyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader)
        recyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader)

        recyclerView.setLoadMoreEnabled(true)

        recyclerView.setOnRefreshListener {

        }
        recyclerView.setOnLoadMoreListener {

        }

        //设置头部加载颜色
        recyclerView.setHeaderViewColor(R.color.colorAccent, R.color.dark, android.R.color.white)
        //设置底部加载颜色
        recyclerView.setFooterViewColor(R.color.colorAccent, R.color.dark, android.R.color.white)
        //设置底部加载文字提示
        recyclerView.setFooterViewHint("拼命加载中", "我是有底线的", "网络不给力啊，点击再试一次吧")

        recyclerView.refresh()
    }

    private fun getItemDecoration(): RecyclerView.ItemDecoration {
        return DividerDecoration.Builder(this)
                .setHeight(R.dimen.dp_4)
                .setPadding(R.dimen.dp_4)
                .setColorResource(R.color.split)
                .build()
    }

}
