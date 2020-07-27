package com.gddst.lhy.weather.ui.citysearch

import com.xiaojianjun.wanandroid.model.api.RetrofitClient

class CitySearchRepository {
    suspend fun getHostCity()=RetrofitClient.apiService.getHostCity().apiData()
    suspend fun getCityId(location:String)=RetrofitClient.apiService.getCityId(location = location).apiData()
}