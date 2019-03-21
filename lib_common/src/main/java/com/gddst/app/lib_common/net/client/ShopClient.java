package com.gddst.app.lib_common.net.client;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by chenzj on 2017/4/11.
 */

public interface ShopClient {
//    @POST("sq580-store-api/goods/api/gethospitallist")
//    Observable<BaseResponse<List<Good>>> getSocialShop(@Body GetServiceBody otherServiceBody);
//
//    //https://api.douban.com/v2/movie/
//    @GET("top250")
//

    @GET()
    Observable<Response<ResponseBody>> getProvinceList(@Url String url);

    @GET("https://free-api.heweather.net/s6/weather")
    Observable<Response<ResponseBody>> getWeatherNow(@Query("key") String key, @Query("location") String location);

    @GET("https://free-api.heweather.net/s6/air/now")
    Observable<Response<ResponseBody>> getAirNow(@Query("key") String key, @Query("location") String location);

//    @GET("https://free-api.heweather.net/s6/weather/forecast")
//    Observable<Response<ResponseBody>> getWeatherForecast(@Query("key") String key, @Query("location") String location);
//
//    @GET("https://free-api.heweather.net/s6/weather/lifestyle")
//    Observable<Response<ResponseBody>> getWeatherLifeStyle(@Query("key") String key, @Query("location") String location);

    @GET("https://search.heweather.net/find")
    Observable<Response<ResponseBody>> getCityId(@Query("key") String key, @Query("location") String location);

    @GET("http://guolin.tech/api/bing_pic")
    Observable<Response<ResponseBody>> getPicUrl();


}
