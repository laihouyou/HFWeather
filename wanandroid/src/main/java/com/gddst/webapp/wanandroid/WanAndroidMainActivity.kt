package com.gddst.webapp.wanandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerView
import kotlinx.android.synthetic.main.activity_wan_android_main.*

class WanAndroidMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wan_android_main)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.adapter=getadapter()
    }

    private fun getadapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {

    }
}
