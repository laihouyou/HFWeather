package com.gddst.lhy.weather.ui.citysearch

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.gddst.app.lib_common.base.BaseActivity
import com.gddst.app.lib_common.base.fragment.BaseVmFragment
import com.gddst.app.lib_common.recyclerView.MyLinearLayoutManager
import com.gddst.app.lib_common.recyclerView.TextHeader
import com.gddst.app.lib_common.weather.db.CityVo
import com.gddst.lhy.weather.R
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_city_search_layout.*

class CitySearchFragment2 : BaseVmFragment<CitySearchViewModel>() {
    private val cityHosts = mutableListOf<CityVo>()
    private val search_cityList = mutableListOf<CityVo>()

    private lateinit var  cityHostRecycleAdapter: LRecyclerViewAdapter

    override fun viewModelClass(): Class<CitySearchViewModel> {
        return CitySearchViewModel::class.java
    }

    override fun initView(view: View?) {
        searchView.setQueryHint("请输入关键字")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!TextUtils.isEmpty(newText)) {
                    city_recycler_recyclerView.setVisibility(View.GONE)
                    city_search_recycler.setVisibility(View.VISIBLE)
                    //搜索城市，显示列表
                    mViewModel.getSearch_citys(newText)
                } else {
                    city_recycler_recyclerView.setVisibility(View.VISIBLE)
                    city_search_recycler.setVisibility(View.GONE)
                    //显示热门城市，显示网格
                    mViewModel.getCityHostList()
                }
                return false
            }
        })
        city_recycler_recyclerView.addItemDecoration(GridItemDecoration.Builder(activity)
                .setHorizontal(R.dimen.dp_15)
                .setVertical(R.dimen.dp_20)
                .setColorResource(R.color.beige)
                .build())
        city_search_recycler.setPullRefreshEnabled(false)
        city_search_recycler.addItemDecoration(DividerItemDecoration(activity, 1))
        setListRecyclerView()

        cityHostRecycleAdapter= LRecyclerViewAdapter(CityHostAdapter(activity as BaseActivity,cityhostList = cityHosts))

        city_recycler_recyclerView.adapter=cityHostRecycleAdapter

        city_search_recycler.adapter=LRecyclerViewAdapter(
                CitySearchAdapter(activity as BaseActivity,search_cityList = search_cityList))
        city_search_recycler.setLoadMoreEnabled(false)
    }

    override fun lazyLoad() {

    }

    override fun initData() {
        super.initData()
        mViewModel.getCityHostList()
    }

    override fun observe() {
        super.observe()
        mViewModel.run {
            cityHostList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                cityHosts.clear()
                cityHosts.addAll(it)
                setGridRecyclerView()
                city_recycler_recyclerView.adapter=cityHostRecycleAdapter
                if (cityHostRecycleAdapter.headerViewsCount==0){
                    addHeaderView()
                }
                cityHostRecycleAdapter.notifyDataSetChanged()
            })

            search_citys.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                search_cityList.clear()
                search_cityList.addAll(it)
                city_search_recycler.adapter?.notifyDataSetChanged()
            })
        }
    }

    private fun addHeaderView() {
        val textHeader = TextHeader(activity)
        textHeader.setHeaderText("热门城市")
        cityHostRecycleAdapter.addHeaderView(textHeader)
    }

    override fun backFragment(): Boolean {
        TODO("Not yet implemented")
    }

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_city_search_layout,container,false)
    }

    override fun initListener() {

    }

    private fun setGridRecyclerView() {
        city_recycler_recyclerView.setLayoutManager(GridLayoutManager(activity, 3))
    }

    private fun setListRecyclerView() {
        city_search_recycler.setLayoutManager(MyLinearLayoutManager(activity))
    }

}