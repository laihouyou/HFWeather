package com.xiaojianjun.wanandroid.model.api

import com.gddst.app.lib_common.utils.WeatherUtil
import com.gddst.app.lib_common.weather.util.Keys
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by xiaojianjun on 2019-09-18.
 */
interface ApiService {

    companion object {
//        const val BASE_URL = "https://www.wanandroid.com"
        const val BASE_URL = "https://search.heweather"
    }

   /* @GET("/article/listproject/{page}/json")
    suspend fun getProjectList(@Path("page") page: Int): ApiResult<Pagination<Article>>

    @GET("/article/top/json")
    suspend fun getTopArticleList(): ApiResult<List<Article>>

    @GET("/article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): ApiResult<Pagination<Article>>

    @GET("/user_article/list/{page}/json")
    suspend fun getUserArticleList(@Path("page") page: Int): ApiResult<Pagination<Article>>

    @GET("tree/json")
    suspend fun getArticleCategories(): ApiResult<MutableList<Category>>

    @GET("article/list/{page}/json")
    suspend fun getArticleListByCid(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResult<Pagination<Article>>

    @GET("project/tree/json")
    suspend fun getProjectCategories(): ApiResult<MutableList<Category>>

    @GET("project/list/{page}/json")
    suspend fun getProjectListByCid(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResult<Pagination<Article>>

    @GET("wxarticle/chapters/json")
    suspend fun getWechatCategories(): ApiResult<MutableList<Category>>

    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getWechatArticleList(
        @Path("page") page: Int,
        @Path("id") id: Int
    ): ApiResult<Pagination<Article>>

    @GET("navi/json")
    suspend fun getNavigations(): ApiResult<List<Navigation>>

    @GET("banner/json")
    suspend fun getBanners(): ApiResult<List<Banner>>

    @GET("hotkey/json")
    suspend fun getHotWords(): ApiResult<List<HotWord>>

    @GET("friend/json")
    suspend fun getFrequentlyWebsites(): ApiResult<List<Frequently>>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResult<UserInfo>

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): ApiResult<UserInfo>

    @POST("lg/collect/{id}/json")
    suspend fun collect(@Path("id") id: Int): ApiResult<Any?>

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollect(@Path("id") id: Int): ApiResult<Any?>

    @FormUrlEncoded
    @POST("article/query/{page}/json")
    suspend fun search(
        @Field("k") keywords: String,
        @Path("page") page: Int
    ): ApiResult<Pagination<Article>>

    @FormUrlEncoded
    @POST("lg/user_article/add/json")
    suspend fun shareArticle(
        @Field("title") title: String,
        @Field("link") link: String
    ): ApiResult<Any>

    @GET("lg/coin/userinfo/json")
    suspend fun getPoints(): ApiResult<PointRank>

    @GET("lg/coin/list/{page}/json")
    suspend fun getPointsRecord(@Path("page") page: Int): ApiResult<Pagination<PointRecord>>

    @GET("coin/rank/{page}/json")
    suspend fun getPointsRank(@Path("page") page: Int): ApiResult<Pagination<PointRank>>

    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectionList(@Path("page") page: Int): ApiResult<Pagination<Article>>

    @GET("user/lg/private_articles/{page}/json")
    suspend fun getSharedArticleList(@Path("page") page: Int): ApiResult<Shared>*/

    @POST("lg/user_article/delete/{id}/json")
    suspend fun deleteShare(@Path("id") id: Int): ApiResult<Any>

    /**
     * 获取热门城市列表
     * @param key
     * @param group
     *         world，返回全球热门城市
     * 特殊值：cn，返回中国热门城市
     * 特殊值：overseas，查询海外热门城市（不含中国）
     * @return
     */
    @GET("https://search.heweather.net/top")
    suspend fun getHostCity(
            @Query("number")number:Number= WeatherUtil.city_host_num,
            @Query("key")key:String=Keys.key,
            @Query("group")group:String=WeatherUtil.world,
            @Query("lang")lang:String=WeatherUtil.cn
    ):ApiResult<Response<ResponseBody>>

    /**
     * 通过城市名字等字段获取城市信息
     * @param key
     * @param location
     * @return
     */
    @GET("https://search.heweather.net/find")
    suspend fun getCityId(
            @Query("number")number:Number=WeatherUtil.city_search_num,
            @Query("key")key:String=Keys.key,
            @Query("group")group:String=WeatherUtil.world,
            @Query("location")location:String
    ):ApiResult<Response<ResponseBody>>

}