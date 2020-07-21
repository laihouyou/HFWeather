package com.gddst.lhy.weather.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.com.sky.downloader.greendao.CityVoDao
import com.gddst.app.lib_common.base.BaseApplication
import com.gddst.app.lib_common.base.fragment.BackFragment
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder
import com.gddst.app.lib_common.net.DlObserve
import com.gddst.app.lib_common.net.NetManager
import com.gddst.app.lib_common.recyclerView.MyLinearLayoutManager
import com.gddst.app.lib_common.recyclerView.TextHeader
import com.gddst.app.lib_common.weather.db.CityVo
import com.gddst.app.lib_common.weather.util.Keys
import com.gddst.app.lib_common.widgets.SignKeyWordTextView
import com.gddst.lhy.weather.R
import com.gddst.lhy.weather.util.JsonUtils
import com.gddst.lhy.weather.util.WeatherUtil
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration
import com.github.jdsjlzx.interfaces.OnRefreshListener
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_city_search_layout.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.io.IOException
import java.util.*

class CitySearchFragment2 : BackFragment() {
//    private var searchView: SearchView? = null
    //搜索的关键字
    private var searchText: String=""
//    private var city_recycler_recyclerView: LRecyclerView? = null
    private var lRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var cityHostAdapter: CommonRecycleViewAdapter<CityVo?>? = null
    private val cityHostList: MutableList<CityVo?>? = ArrayList()
//    private val listItemDecoration: ItemDecoration = null
    private var layoutManager: RecyclerView.LayoutManager? = null
//    private var city_search_recycler: LRecyclerView? = null
    private var search_lRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var search_cityHostAdapter: CommonRecycleViewAdapter<CityVo?>? = null
    private val search_cityList: MutableList<CityVo?>? = ArrayList()
    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_city_search_layout, container, false)
    }

    override fun initView(view: View) {
//        searchView = view.findViewById(R.id.searchView)
        //        //设置搜索框直接展开显示。左侧有无放大镜(在搜索框中) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
//        searchView.onActionViewExpanded();
//设置输入框提示语
        searchView.setQueryHint("请输入关键字")
        city_recycler_recyclerView.addItemDecoration(GridItemDecoration.Builder(activity)
                .setHorizontal(R.dimen.dp_15)
                .setVertical(R.dimen.dp_20)
                .setColorResource(R.color.beige)
                .build())
        city_search_recycler.setPullRefreshEnabled(false)
        city_search_recycler.addItemDecoration(DividerItemDecoration(activity, 1))
        setListRecyclerView()
    }

    private fun setGridRecyclerView() {
        layoutManager = GridLayoutManager(activity, 3)
        city_recycler_recyclerView.setLayoutManager(layoutManager)
    }

    private fun setListRecyclerView() {
        layoutManager = MyLinearLayoutManager(activity)
        city_search_recycler.setLayoutManager(layoutManager)
    }

    override fun initListener() {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!TextUtils.isEmpty(newText)) {
                    searchText = newText
                    city_recycler_recyclerView.setVisibility(View.GONE)
                    city_search_recycler.setVisibility(View.VISIBLE)
                    //搜索城市，显示列表
                    getCitySearchList(newText)
                } else {
                    city_recycler_recyclerView.setVisibility(View.VISIBLE)
                    city_search_recycler.setVisibility(View.GONE)
                    //显示热门城市，显示网格
                    getCityHostList()
                }
                return false
            }
        })
        city_recycler_recyclerView.setOnRefreshListener(OnRefreshListener { requestHostCityList() })
    }

    private fun getCityHostList() {
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(BaseApplication.getIns())
        val hostCityListStr = sharedPreferences.getString(WeatherUtil.hostCityListStr, "")
        if (TextUtils.isEmpty(hostCityListStr)) {
            requestHostCityList()
        } else {
            val cityVoList = BaseApplication.getGson().fromJson<MutableList<CityVo?>?>(
                    hostCityListStr,
                    object : TypeToken<MutableList<CityVo?>?>() {}.type)
            setHostCityData(cityVoList)
        }
    }

    private fun getCitySearchList(cityName: String?) {
        NetManager.INSTANCE.shopClient
                .getCityId(Keys.key, WeatherUtil.world_scenic, WeatherUtil.city_search_num, cityName)
                .subscribeOn(Schedulers.io())
                .map(object : Function<Response<ResponseBody?>?,MutableList<CityVo?>?> {
                    override fun apply(response: Response<ResponseBody?>): MutableList<CityVo?>? {
                        return JsonUtils.getCityHostList(response)
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DlObserve<MutableList<CityVo?>?>() {
                    @Throws(IOException::class)
                    override fun onResponse(cityVos: MutableList<CityVo?>?) {
                        setSearchCityData(cityVos)
                    }

                    override fun onError(errorCode: Int, errorMsg: String?) {}
                })
    }

    override fun lazyLoad() {
        cityHostAdapter = object : CommonRecycleViewAdapter<CityVo?>(
                activity,
                R.layout.item_text,
                cityHostList
        ) {
            override fun convert(viewHolder: ViewHolder, cityVo: CityVo?, position: Int) {
                val item_text = viewHolder.getView<TextView?>(R.id.item_text)
                item_text?.setText(cityVo?.getLocation())
                //如果是定位城市，动态设置定位图标
                if (cityVo?.getCityType() == WeatherUtil.city_location) {
                    val drawableLeft = resources.getDrawable(R.drawable.city_loaction)
                    drawableLeft.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
                    item_text?.setCompoundDrawables(drawableLeft, null, null, null)
                } else {
                    item_text?.setCompoundDrawables(null, null, null, null)
                }
                if (cityVo!!.getIsSelected()) {
                    item_text?.setTextColor(resources.getColor(R.color.cornflowerblue12))
                    item_text?.setBackgroundResource(R.drawable.city_search_host_list_selected)
                } else {
                    item_text?.setTextColor(resources.getColor(R.color.point_facility11))
                    item_text?.setBackgroundResource(R.drawable.city_search_host_list_no_selected)
                }
                viewHolder.setOnClickListener(R.id.item_text, View.OnClickListener { evenBusPostCityVo(cityVo) })
            }
        }
        lRecyclerViewAdapter = LRecyclerViewAdapter(cityHostAdapter)
        city_recycler_recyclerView.setAdapter(lRecyclerViewAdapter)
        //禁止自动加载更多功能
        city_recycler_recyclerView.setLoadMoreEnabled(false)
        city_recycler_recyclerView.refresh()
        search_cityHostAdapter = object : CommonRecycleViewAdapter<CityVo?>(
                activity,
                R.layout.item_search_text,
                search_cityList
        ) {
            override fun convert(viewHolder: ViewHolder, cityVo: CityVo?, position: Int) {
                val item_text = viewHolder.getView<SignKeyWordTextView?>(R.id.item_search_text)
                item_text?.setTextColor(resources.getColor(R.color.point_facility11))
                item_text?.setBackgroundColor(resources.getColor(R.color.white))
                //设置高亮关键字
                item_text?.setSignText(searchText)
                //设置高亮颜色
                item_text?.setSignTextColor(resources.getColor(R.color.salmon))
                item_text?.setText(
                        cityVo?.getLocation() + "," + cityVo?.getParent_city() + "," + cityVo?.getAdmin_area(),
                        TextView.BufferType.NORMAL)
                viewHolder.setOnClickListener(R.id.item_search_text, View.OnClickListener { evenBusPostCityVo(cityVo) })
                //判断本地数据库中是否有该城市
                val cityVoDbList = BaseApplication.getIns().daoSession.cityVoDao
                        .queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVo?.getCid())).list()
                if (cityVoDbList.size > 0) {
                    viewHolder.setText(R.id.item_search_hint, getString(R.string.added))
                    viewHolder.setTextColor(R.id.item_search_hint, resources.getColor(R.color.lightseagreen))
                    viewHolder.setVisible(R.id.item_search_hint, View.VISIBLE)
                } else {
                    viewHolder.setText(R.id.item_search_hint, "")
                    viewHolder.setTextColor(R.id.item_search_hint, resources.getColor(R.color.lightseagreen))
                    viewHolder.setVisible(R.id.item_search_hint, View.GONE)
                }
            }
        }
        search_lRecyclerViewAdapter = LRecyclerViewAdapter(search_cityHostAdapter)
        city_search_recycler.setAdapter(search_lRecyclerViewAdapter)
        //禁止自动加载更多功能
        city_search_recycler.setLoadMoreEnabled(false)
    }

    private fun evenBusPostCityVo(cityVo: CityVo?) {
        EventBus.getDefault().post(cityVo)
    }

    private fun addHeaderView() {
        val textHeader = TextHeader(activity)
        textHeader.setHeaderText("热门城市")
        lRecyclerViewAdapter?.addHeaderView(textHeader)
    }

    private fun requestHostCityList() {
        NetManager.INSTANCE.shopClient
                .getHostCity(Keys.key, WeatherUtil.world, WeatherUtil.city_host_num, WeatherUtil.cn)
                .subscribeOn(Schedulers.io())
                .map(object : Function<Response<ResponseBody?>?, MutableList<CityVo?>?> {
                    override fun apply(responseBodyResponse: Response<ResponseBody?>): MutableList<CityVo?>? {
                        return JsonUtils.getCityHostList(responseBodyResponse)
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DlObserve<MutableList<CityVo?>?>() {
                    @Throws(IOException::class)
                    override fun onResponse(cityVos: MutableList<CityVo?>?) { //将热门城市数据保存
                        val sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(BaseApplication.getIns()).edit()
                        sharedPreferences.putString(WeatherUtil.hostCityListStr, BaseApplication.getGson().toJson(cityVos))
                        sharedPreferences.apply()
                        setHostCityData(cityVos)
                    }

                    override fun onError(errorCode: Int, errorMsg: String?) {
                        Log.i("errorMsg",errorMsg)
                    }
                })
    }

    private fun setHostCityData(cityVos: MutableList<CityVo?>?) {
        Observable.just(cityVos)
                .subscribeOn(Schedulers.io())
                .doOnNext { cityVos ->
                    cityHostList?.clear()
                    //添加定位城市数据
                    val cityVoLocationList = BaseApplication.getIns().daoSession.cityVoDao.queryBuilder()
                            .where(CityVoDao.Properties.CityType.eq(WeatherUtil.city_location)).list()
                    if (cityVoLocationList.size == 1) {
                        cityVoLocationList[0].isSelected = true
                        cityHostList?.add(cityVoLocationList[0])
                    }
                    //对比数据库，数据库已经存在的热门城市改为已选中
                    for (j in cityVos!!.indices) {
                        val cityVoHost = cityVos.get(j)
                        val cityDbVos = BaseApplication.getIns().daoSession
                                .cityVoDao.queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVoHost?.getCid())).list()
                        if (cityDbVos.size == 1) {
                            val cityVodb = cityDbVos[0]
                            cityVoHost?.setIsSelected(true)
                            cityVoHost?.setAddCityTime(cityVodb.addCityTime)
                            cityVoHost?.setCityType(cityVodb.cityType)
                            continue
                        }
                    }
                    //添加热门城市数据
                    cityHostList?.addAll(cityVos)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DlObserve<MutableList<CityVo?>?>() {
                    @Throws(IOException::class)
                    override fun onResponse(cityVos: MutableList<CityVo?>?) {
                        setGridRecyclerView()
                        city_recycler_recyclerView.setAdapter(lRecyclerViewAdapter) //必须重新setAdapter
                        if (lRecyclerViewAdapter?.getHeaderViewsCount() == 0) {
                            addHeaderView()
                        }
                        notifyChanged()
                    }

                    override fun onError(errorCode: Int, errorMsg: String?) {}
                })
    }

    private fun setSearchCityData(cityVos: MutableList<CityVo?>?) {
        search_cityList?.clear()
        search_cityList!!.addAll(cityVos!!)
        notifyChanged_search()
    }

    private fun notifyChanged_search() {
        if (search_lRecyclerViewAdapter != null) {
            search_lRecyclerViewAdapter?.notifyDataSetChanged()
        }
    }

    private fun notifyChanged() {
        if (cityHostAdapter != null) {
            city_recycler_recyclerView.refreshComplete(50)
            cityHostAdapter?.notifyDataSetChanged()
        }
    }

    override fun backFragment(): Boolean {
        back()
        return true
    }

    private fun back() {
        val fragmentTransaction = activity?.getSupportFragmentManager()?.beginTransaction()
        //添加加载动画
        fragmentTransaction?.setCustomAnimations(R.animator.city_search_add, R.animator.city_search_detele)
        fragmentTransaction?.remove(this)
        fragmentTransaction?.commit()
    }
}