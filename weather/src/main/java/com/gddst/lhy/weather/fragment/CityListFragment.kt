package com.gddst.lhy.weather.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.com.sky.downloader.greendao.CityVoDao
import com.gddst.app.lib_common.base.BaseApplication
import com.gddst.app.lib_common.base.fragment.BackFragment
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder
import com.gddst.app.lib_common.recyclerView.MyLinearLayoutManager
import com.gddst.app.lib_common.utils.DateUtil
import com.gddst.lhy.weather.R
import com.gddst.lhy.weather.util.WeatherUtil
import com.gddst.lhy.weather.vo.WeatherVo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_city_list_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class CityListFragment2 : BackFragment(){

    private val weatherVoList: MutableList<WeatherVo> = arrayListOf();

    private var isEdit : Boolean =false

    override fun lazyLoad() {
        intiRecycleList()
    }

//    private fun intiRecycleList() = Observable.just(1)
//            .subscribeOn(Schedulers.io())
//            .map { getWeatherVoList() }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { setListDate() }

    /**
     * 使用协程方式执行
     */
    private fun intiRecycleList(){
        GlobalScope.launch(Dispatchers.Main){
            suspendGetWeatherList()
            setListDate()
        }
    }

    private suspend fun suspendGetWeatherList()= withContext(Dispatchers.IO){
        getWeatherVoList()
    }

    private fun setListDate() {
        city_recycler_list.layoutManager=MyLinearLayoutManager(activity)
        city_recycler_list.adapter = object :CommonRecycleViewAdapter<WeatherVo>(activity, R.layout.item_city_recycle, weatherVoList){
            override fun convert(viewHolder: ViewHolder, weatherVo: WeatherVo, position: Int) {
                val basicBean=weatherVo.basic
                val nowBean=weatherVo.now
                if (isEdit){
                    if (weatherVo.cityType!=1){
                        viewHolder.setVisible(R.id.im_city_delete,View.VISIBLE)
                    }
                    viewHolder.setVisible(R.id.tv_weather_oath, View.GONE)
                    viewHolder.setVisible(R.id.tv_weather_max_min_num, View.GONE)
                    viewHolder.setVisible(R.id.im_weather_icon, View.GONE)
                    viewHolder.setVisible(R.id.tv_weather_num, View.GONE)
                }else{
                    viewHolder.setVisible(R.id.im_city_delete, View.GONE)

                    viewHolder.setVisible(R.id.tv_weather_oath, View.VISIBLE)
                    viewHolder.setVisible(R.id.tv_weather_max_min_num, View.VISIBLE)
                    viewHolder.setVisible(R.id.im_weather_icon, View.VISIBLE)
                    viewHolder.setVisible(R.id.tv_weather_num, View.VISIBLE)
                }
                viewHolder.setText(R.id.tv_city_detailed, basicBean.location)
                if (weatherVo.cityType == 1) {
                    viewHolder.setVisible(R.id.im_loaction_icon, View.VISIBLE)
                } else {
                    viewHolder.setVisible(R.id.im_loaction_icon, View.GONE)
                }
                /**
                 * 判断城市是否属于直辖市、省会城市
                 */
                //县与市名字一样
                if (basicBean.parent_city == basicBean.location) {
                    if (basicBean.location == basicBean.admin_area) {      //县与省名字一样
                        //属于直辖市  如北京市 直接显示国家信息
                        viewHolder.setText(R.id.tv_province, basicBean.cnty)
                    } else {     //县与省名字不一样
                        //属于地级城市 如赣州市 直接显示省份信息
                        viewHolder.setText(R.id.tv_province, basicBean.admin_area)
                    }
                } else {          //县与市名字一样
                    if (basicBean.parent_city == basicBean.admin_area) {      //市与省名字一样
                        //属于县级城市 如 重庆-重庆-重庆-中国
                        viewHolder.setText(R.id.tv_province, basicBean.location + "-" + basicBean.cnty)
                    } else {        //市与省名字不一样
                        //属于县级城市 如 赣州-赣州-江西-中国
                        viewHolder.setText(R.id.tv_province, basicBean.parent_city + "-" + basicBean.admin_area)
                    }
                }
                //当前温度
                viewHolder.setText(R.id.tv_weather_num, nowBean.fl + "℃")
                //实况天气图标
                viewHolder.setBackgroundRes(
                        R.id.im_weather_icon,
                        resources.getIdentifier(
                                WeatherUtil.weather + weatherVo.now.cond_code,
                                WeatherUtil.drawable,
                                context!!.packageName
                        )
                )


                //温度、方向
                viewHolder.setText(R.id.tv_weather_oath,
                        "湿度" + nowBean.hum + "%" + "|"
                                + nowBean.wind_dir + nowBean.wind_sc + "级" + "|"
                                + "能见度" + nowBean.vis + "公里"
                )

                //最低最高气温
                val dailyForecastBeanList = weatherVo.daily_forecast
                for (dailyForecastBean in dailyForecastBeanList) {
                    if (dailyForecastBean.date == DateUtil.currDay()) {
                        viewHolder.setText(R.id.tv_weather_max_min_num,
                                dailyForecastBean.tmp_max + "/"
                                        + dailyForecastBean.tmp_min + "℃"
                        )
                        break
                    }
                }

                //删除城市
                viewHolder.setOnClickListener(R.id.im_city_delete) {
                    if (weatherVo.cityType != 1) {
                        val cityVoDao = BaseApplication.getIns().daoSession.cityVoDao
                        val cityVoList = cityVoDao.queryBuilder()
                                .where(CityVoDao.Properties.Cid.eq(basicBean.cid)).list()
                        if (cityVoList.size == 1) {
                            cityVoDao.delete(cityVoList[0])
                            weatherVoList.removeAt(position)
                            notifyItemChanged(position)
                            EventBus.getDefault().post(weatherVo)
                        }
                    }
                }
            }
        }
    }

    override fun backFragment(): Boolean {
        back()
        return true
    }

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_city_list_layout,container,false)
    }

    override fun initListener() {
        tv_city_back.setOnClickListener {
            back()
        }
        tv_city_edit.setOnClickListener {
            isEdit=!isEdit
            city_recycler_list.adapter?.notifyDataSetChanged()
        }
        btn_city_add.setOnClickListener {
            val citySearchFragment=CitySearchFragment2()
            val fragmentTransaction: FragmentTransaction = activity!!.getSupportFragmentManager().beginTransaction()
            //添加加载动画
            fragmentTransaction.setCustomAnimations(R.animator.city_search_add, R.animator.city_search_detele)
            fragmentTransaction.add(R.id.cityManagementFarmeLayout, citySearchFragment)
            fragmentTransaction.commit()
        }

    }

    override fun initView(view: View?) {

    }

    private fun getWeatherVoList():MutableList<WeatherVo>{
        val sharedPreferences=PreferenceManager.getDefaultSharedPreferences(BaseApplication.getIns())
        val cityVos=BaseApplication.getIns().daoSession.cityVoDao.queryBuilder()
                .orderAsc(CityVoDao.Properties.CityType)
                .orderAsc(CityVoDao.Properties.Id)
                .list()
        for (cityvo in cityVos){
            val weatherStr=sharedPreferences.getString(cityvo.cid,"")
            if (TextUtils.isEmpty(weatherStr)) {
                weatherVoList.add(WeatherVo())
            } else {
                weatherVoList.add(BaseApplication.getGson().fromJson(weatherStr, WeatherVo::class.java))
            }
        }
        return weatherVoList
    }

    private fun back(){
        val fragmentTransaction=activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.animator.city_list_add,R.animator.city_list_detele)
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }

}