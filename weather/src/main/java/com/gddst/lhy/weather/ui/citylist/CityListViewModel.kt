package com.gddst.lhy.weather.ui.citylist

import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.com.sky.downloader.greendao.CityVoDao
import com.gddst.app.lib_common.base.BaseApplication
import com.gddst.app.lib_common.base.fragment.BaseFragment
import com.gddst.lhy.weather.R
import com.gddst.lhy.weather.vo.WeatherVo
import com.lhy.wanandroid.base.BaseViewModel

class CityListViewModel :BaseViewModel() {

    val weathervoList=MutableLiveData<MutableList<WeatherVo>>()

     fun getWeatherVoList(){
         val weatherVoList= mutableListOf<WeatherVo>()
         val sharedPreferences= PreferenceManager.getDefaultSharedPreferences(BaseApplication.getIns())
         val cityVos= BaseApplication.getIns().daoSession.cityVoDao.queryBuilder()
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
         weathervoList.value=weatherVoList
     }

    fun  backFragment(fragment: BaseFragment){
        val fragmentTransaction=fragment.activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.animator.city_list_add, R.animator.city_list_detele)
        fragmentTransaction.remove(fragment)
        fragmentTransaction.commit()
    }
}