package com.gddst.app.lib_common.net;

import android.webkit.URLUtil;

import com.gddst.app.lib_common.net.base.DlException;
import com.gddst.app.lib_common.net.client.ShopClient;
import com.gddst.app.lib_common.utils.AppUtil;
import com.gddst.app.lib_common.utils.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gddst.app.lib_common.net.HttpUrl.SHOP_URL;
import static com.gddst.app.lib_common.net.base.ErrorCode.NET_DISABLE;


/**
 * @author chenzj
 * @Title: NetManager
 * @Description: 类的描述 -
 * @date 2017/3/1 16:21
 * @email admin@chenzhongjin.cn
 */
public enum NetManager {

    INSTANCE;

    private ShopClient mShopClient;

    public void initShopClient() {
        OkHttpClient.Builder mOkHttpClient = new OkHttpClient.Builder();
        Retrofit.Builder mRetrofit = new Retrofit.Builder();
        setCommonSetting(mOkHttpClient, mRetrofit, SHOP_URL);
        mShopClient = mRetrofit.client(mOkHttpClient.build()).build().create(ShopClient.class);
    }

    private void setCommonSetting(OkHttpClient.Builder okhttpBuilder, Retrofit.Builder retrofitBuilder, String hostUrl) {
        setCommonSetting(okhttpBuilder, retrofitBuilder, hostUrl, 10, 10, 10);
    }

    private void setCommonSetting(OkHttpClient.Builder okhttpBuilder, Retrofit.Builder retrofitBuilder, String hostUrl,
                                  int conTimeout, int writeTimeout, int readTimeout) {
        if (hostUrl!=null){
            if (!URLUtil.isValidUrl(hostUrl)) {
                throw new IllegalArgumentException("please setup the validUrl");
            } else {
                retrofitBuilder.baseUrl(hostUrl);
            }
        }else {
            retrofitBuilder.baseUrl("");
        }
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        okhttpBuilder.connectTimeout(conTimeout, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);

        //常用参数拦截器
        ParamsInterceptor paramsInterceptor = new ParamsInterceptor();
        okhttpBuilder.addInterceptor(paramsInterceptor);

        //日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpBuilder.addInterceptor(loggingInterceptor);

        okhttpBuilder.addInterceptor(chain -> {
            if (AppUtil.isNetworkAvailable(Utils.getContext())) {
                return chain.proceed(chain.request());
            } else {
                throw new DlException(NET_DISABLE, "网络连接失败，请开启您的网络连接，并重试！");
            }
        });
    }

    public ShopClient getShopClient() {
        return mShopClient;
    }
}
