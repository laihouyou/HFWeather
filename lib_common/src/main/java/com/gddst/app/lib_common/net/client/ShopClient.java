package com.gddst.app.lib_common.net.client;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.Map;

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

    //修改密码
    @POST()
    Observable<String> restPassword(@Url String url, @Body String par);

    /**
     * 下载所有工单内容
     * @param completeUrl   已拼接ServerUrl
     * @return
     */
    @POST("")
    Observable<Response<ResponseBody>> postData(@Url String completeUrl);

    /**
     * 下载所有工单内容
     * @param completeUrl   已拼接ServerUrl
     * @return
     */
    @POST("")
    Observable<JSONArray> postJsonArrayData(@Url String completeUrl);

    /**
     * 通用请求
     * @param completeUrl
     * @return
     */
    @POST()
    Observable<String> generalRequest(@Url String completeUrl);

    /**
     * 通用请求
     * @param noServerUrl 未拼接ServerUrl
     * @return
     */
    @POST()
    Observable<Response<ResponseBody>> generalRequestBody(@Url String noServerUrl);

    /**
     * 通用请求
     * @param noServerUrl 未拼接ServerUrl
     * @param par
     * @return
     */
    @POST()
    Observable<Response<ResponseBody>> generalRequest(@Url String noServerUrl, @Body RequestBody par);

    /**
     * 爆管分析
     * @param noServerUrl 未拼接ServerUrl
     * @return
     */
    @GET()
    Observable<Response<ResponseBody>> generalRequest(@Url String noServerUrl, @QueryMap Map<String, String> param);

//    /**
//     * 登入
//     * @param noServerUrl 未拼接ServerUrl
//     * @return
//     */
//    @POST()
//    Observable<UserBeanVO> login(@Url String noServerUrl);
//
//    /**
//     * 位置上传
//     * @param noServerUrl 未拼接ServerUrl
//     * @param par
//     * @return
//     */
//    @POST()
//    Observable<ReturnMessage> traceUpload(@Url String noServerUrl, @Body RequestBody par);
//
//    /**
//     * upDateApk
//     * @param url 未拼接ServerUrl
//     * @return
//     */
//    @POST()
//    Observable<UpdateApkInfo[]> upDateApk(@Url String url);

    /**
     * 地址搜索
     * @param url 未拼接ServerUrl
     * @return
     */
    @GET()
    Observable<String> addressSearch(@Url String url);


    /**
     * 附件及附属信息上传
     * @param noServerUrl 未拼接ServerUrl
     * @return
     */
    @POST()
    Observable<Response<ResponseBody>> uploadFile(@Url String noServerUrl, @Body RequestBody requestBody);


}
