package com.gddst.lhy.weather.ui.citysearch

import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.com.sky.downloader.greendao.CityVoDao
import com.gddst.app.lib_common.base.BaseApplication
import com.gddst.app.lib_common.weather.db.CityVo
import com.gddst.lhy.weather.util.JsonUtils
import com.gddst.lhy.weather.util.WeatherUtil
import com.google.gson.reflect.TypeToken
import com.lhy.wanandroid.base.BaseViewModel
import com.xiaojianjun.wanandroid.model.api.RetrofitClient

class CitySearchViewModel : BaseViewModel() {
    val cityHostList= MutableLiveData<MutableList<CityVo>>()
    val search_citys= MutableLiveData<MutableList<CityVo>>()

    val citySearchRepository by lazy { CitySearchRepository() }

    fun getCityHostList(){
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(BaseApplication.getIns())
        val hostCityListStr = sharedPreferences.getString(WeatherUtil.hostCityListStr, "")
        if (TextUtils.isEmpty(hostCityListStr)) {
            requestHostCityList()
        } else {
            val cityVoList = BaseApplication.getGson().fromJson<MutableList<CityVo>?>(
                    hostCityListStr,
                    object : TypeToken<MutableList<CityVo>?>() {}.type)
            setHostCityData(cityVoList)
        }
    }

    private fun requestHostCityList() {
        //这里进行网络请求获取数据
        launch(
                block = {
                    val result=citySearchRepository.getHostCity()
                    val cityVoList=JsonUtils.getCityHostList(result)
                    val sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(BaseApplication.getIns()).edit()
                    sharedPreferences.putString(WeatherUtil.hostCityListStr, BaseApplication.getGson().toJson(cityVoList))
                    sharedPreferences.apply()

                    setHostCityData(cityVoList)
                }
        )
    }

    private fun setHostCityData(cityVoList: MutableList<CityVo>?) {
        //添加定位城市数据
        val cityVoLocationList = BaseApplication.getIns().daoSession.cityVoDao.queryBuilder()
                .where(CityVoDao.Properties.CityType.eq(WeatherUtil.city_location)).list()
        if (cityVoLocationList.size == 1) {
            cityVoLocationList[0].isSelected = true
            cityVoList?.add(cityVoLocationList[0])
        }
        //对比数据库，数据库已经存在的热门城市改为已选中
        for (j in cityVoList!!.indices) {
            val cityVoHost = cityVoList.get(j)
            val cityDbVos = BaseApplication.getIns().daoSession
                    .cityVoDao.queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVoHost.getCid())).list()
            if (cityDbVos.size == 1) {
                val cityVodb = cityDbVos[0]
                cityVoHost.setIsSelected(true)
                cityVoHost.setAddCityTime(cityVodb.addCityTime)
                cityVoHost.setCityType(cityVodb.cityType)
            }
        }
        cityHostList.value=cityVoList
    }

    fun getSearch_citys(location:String){
        launch(
                block = {
                    val rest=citySearchRepository.getCityId(location)
                    val searchCitys=JsonUtils.getCityHostList(rest)
                    search_citys.value=searchCitys
                }
        )
    }
}