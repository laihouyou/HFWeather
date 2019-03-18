package com.gddst.lhy.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.gddst.lhy.weather.vo.*;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends BaseActivity {
    //空气质量aqi
    private TextView aqi_text;
    private TextView pm25_text;
    //标题
    private TextView tv_title;
    private TextView tv_time;
    //当日天气
    private TextView tv_Celsius;
    private TextView tv_situation;

    private ImageView im_pic;
    private ScrollView scrollView;

    private LinearLayout day_linelayout;
    private LinearLayout suggestion_linearlayout;
    private SwipeRefreshLayout swipeRefres;

    private String weatherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int BindLayout() {
        return R.layout.activity_weather;
    }

    @Override
    protected void initView() {
//        ProvinceCityFragment fragment=new ProvinceCityFragment();
//        FragmentManager fragmentManager= getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment,fragment);
//        fragmentTransaction.commit();

        aqi_text=findViewById(R.id.aqi_text);
        pm25_text=findViewById(R.id.pm25_text);

        tv_title=findViewById(R.id.tv_title);
        tv_time=findViewById(R.id.tv_time);

        tv_Celsius=findViewById(R.id.tv_Celsius);
        tv_situation=findViewById(R.id.tv_situation);

        im_pic=findViewById(R.id.im_pic);
        scrollView=findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefres.setEnabled(scrollView.getScrollY()==0);
            }
        });

        day_linelayout=findViewById(R.id.day_linelayout);
        tv_time=findViewById(R.id.tv_time);
        suggestion_linearlayout=findViewById(R.id.suggestion_linearlayout);

        swipeRefres=findViewById(R.id.swipeRefres);
        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                getPicImage();
            }
        });

    }

    @Override
    protected void initData() {
        weatherId=getIntent().getStringExtra(WeatherUtil.weatherId);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherVoString=sharedPreferences.getString(WeatherUtil.weatherVo,"");
        if (TextUtils.isEmpty(weatherVoString)){
            //获取每日天气数据
            requestWeather(weatherId);
        }else {
            Gson gson=new Gson();

            WeatherVo weatherVo=gson.fromJson(weatherVoString,WeatherVo.class);
            showText(weatherVo);

        }

        String picUrl=sharedPreferences.getString(WeatherUtil.picUrl,"");
        if (TextUtils.isEmpty(picUrl)){
            getPicImage();
        }else {
            Glide.with(WeatherActivity.this).load(picUrl).into(im_pic);
        }
    }

    private void requestWeather(String weatherId) {
        weatherId="广州";
        final Gson gson=new Gson();

        Observable observableNow= NetManager.INSTANCE.getShopClient()
                .getWeatherNow(Keys.key,weatherId)
                .map(new Function<Response<ResponseBody>, WeatherVo>() {
                    @Override
                    public WeatherVo apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToWeatherVo(response,gson);
                    }
                }).subscribeOn(Schedulers.io());
        Observable observableAirNow=NetManager.INSTANCE.getShopClient()
                .getAirNow(Keys.key,weatherId)
                .map(new Function<Response<ResponseBody>, AirNow>() {
                    @Override
                    public AirNow apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToAirNow(response,gson);
                    }
                }).subscribeOn(Schedulers.io());
        Observable observableForecast=NetManager.INSTANCE.getShopClient()
                .getWeatherForecast(Keys.key,weatherId)
                .map(new Function<Response<ResponseBody>, List<WeatherForecast>>() {
                    @Override
                    public List<WeatherForecast> apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToWeatherForecast(response,gson);
                    }
                }).subscribeOn(Schedulers.io());
        Observable observableLifestyle=NetManager.INSTANCE.getShopClient()
                .getWeatherLifeStyle(Keys.key,weatherId)
                .map(new Function<Response<ResponseBody>, List<LifestyleVo>>() {
                    @Override
                    public List<LifestyleVo> apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToLifestyle(response,gson);
                    }
                }).subscribeOn(Schedulers.io());

        Observable.zip(observableNow, observableAirNow, observableForecast, observableLifestyle,
                new Function4<WeatherVo, AirNow,List<WeatherForecast>,List<LifestyleVo>,WeatherVo>() {
                    @Override
                    public WeatherVo apply(
                            WeatherVo weatherVo,
                            com.gddst.lhy.weather.vo.AirNow airNow,
                            List<WeatherForecast> weatherForecasts,
                            List<LifestyleVo> lifestyles) throws Exception {
                        weatherVo.setAirNow(airNow);
                        weatherVo.setWeatherForecastList(weatherForecasts);
                        weatherVo.setLifestyleVoList(lifestyles);

                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences
                                (WeatherActivity.this).edit();
                        editor.putString(WeatherUtil.weatherVo,gson.toJson(weatherVo));
                        editor.apply();

                        return weatherVo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<WeatherVo>() {

                    @Override
                    public void onResponse(WeatherVo weatherVo) throws IOException {
                        showText(weatherVo);
                        swipeRefres.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this,weatherVo.getStatus(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(WeatherActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                        swipeRefres.setRefreshing(false);
                    }
                });

    }

    private void getPicImage(){
        final Gson gson=new Gson();
        NetManager.INSTANCE.getShopClient()
                .getPicUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<Response<ResponseBody>>() {
                    @Override
                    public void onResponse(Response<ResponseBody> s) throws IOException {
                        if (s.code()==200){
                            String picUrl=s.body().string();
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences
                                    (WeatherActivity.this).edit();
                            editor.putString(WeatherUtil.picUrl,gson.toJson(picUrl));
                            editor.apply();
                            Glide.with(WeatherActivity.this).load(picUrl).into(im_pic);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(WeatherActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showText(WeatherVo weatherVo) {
        if (weatherVo==null)
            return;
        List<LifestyleVo> lifestyleVos=weatherVo.getLifestyleVoList();
        List<WeatherForecast> weatherForecasts=weatherVo.getWeatherForecastList();
        AirNow airNow=weatherVo.getAirNow();
        Now now=weatherVo.getNow();

        if (lifestyleVos==null)
            return;
        suggestion_linearlayout.removeAllViews();
        for (LifestyleVo lifestyleBase:lifestyleVos){
            View view=getLayoutInflater().inflate(R.layout.item_suggestion_text,suggestion_linearlayout,false);
            TextView item_suggestion_text_tv=view.findViewById(R.id.item_suggestion_text_tv);
            item_suggestion_text_tv.setText(lifestyleBase.getBrf()+":"+lifestyleBase.getTxt());
            suggestion_linearlayout.addView(item_suggestion_text_tv);
        }

        if (weatherForecasts==null)
            return;
        day_linelayout.removeAllViews();
        for (WeatherForecast forecastBase:weatherForecasts){
            View view = getLayoutInflater().inflate(R.layout.item_day_text, day_linelayout, false);
            TextView tv_date=view.findViewById(R.id.tv_date);
            TextView tv_two=view.findViewById(R.id.tv_two);
            TextView tv_max=view.findViewById(R.id.tv_max);
            TextView tv_min=view.findViewById(R.id.tv_min);
            tv_date.setText(forecastBase.getDate());
            tv_two.setText(forecastBase.getCond_txt_d());
            tv_max.setText(forecastBase.getTmp_max());
            tv_min.setText(forecastBase.getTmp_min());
            day_linelayout.addView(view);
        }

        aqi_text.setText(airNow.getAqi());
        pm25_text.setText(airNow.getPm25());


        if (now!=null){
            tv_Celsius.setText(now.getFl()+"℃");
            tv_situation.setText(now.getCond_txt());
        }

        tv_title.setText(weatherVo.getBasic().getLocation());
        tv_time.setText(weatherVo.getUpdate().getLoc());
    }


    private WeatherVo ResponseToWeatherVo(Response<ResponseBody> response,Gson gson) throws IOException, JSONException {
        if (response.code()!=200||gson==null){
            return new WeatherVo();
        }
        String body=response.body().string();
        JSONObject jsonObject=new JSONObject(body);
        JSONArray jsonArray=jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        WeatherVo weatherVo=gson.fromJson(jsonArray.get(0).toString(),WeatherVo.class);
        return weatherVo;
    }
    private AirNow ResponseToAirNow(Response<ResponseBody> response,Gson gson) throws IOException, JSONException {
        if (response.code()!=200||gson==null){
            return new AirNow();
        }
        String body=response.body().string();
        JSONObject jsonObject=new JSONObject(body);
        JSONArray jsonArray=jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject=jsonArray.getJSONObject(0);
        AirNow airNow=gson.fromJson(weatherObject.getJSONObject(WeatherUtil.air_now_city).toString(),AirNow.class);
        return airNow;
    }
    private List<WeatherForecast> ResponseToWeatherForecast(Response<ResponseBody> response,Gson gson) throws IOException, JSONException {
        if (response.code()!=200||gson==null){
            return new ArrayList<>();
        }
        String body=response.body().string();
        JSONObject jsonObject=new JSONObject(body);
        JSONArray jsonArray=jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject=jsonArray.getJSONObject(0);
        JSONArray daily_forecastJsonArray=weatherObject.getJSONArray(WeatherUtil.daily_forecast);
        List<WeatherForecast> weatherForecasts=new ArrayList<>();
        for (int i = 0; i < daily_forecastJsonArray.length(); i++) {
            String daily_forecastStr=daily_forecastJsonArray.getJSONObject(i).toString();
            WeatherForecast weatherForecast=gson.fromJson(daily_forecastStr,WeatherForecast.class);
            weatherForecasts.add(weatherForecast);
        }
        return weatherForecasts;
    }
    private List<LifestyleVo> ResponseToLifestyle(Response<ResponseBody> response,Gson gson) throws IOException, JSONException {
        if (response.code()!=200||gson==null){
            return new ArrayList<>();
        }
        String body=response.body().string();
        JSONObject jsonObject=new JSONObject(body);
        JSONArray jsonArray=jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject=jsonArray.getJSONObject(0);
        JSONArray daily_forecastJsonArray=weatherObject.getJSONArray(WeatherUtil.lifestyle);
        List<LifestyleVo> weatherForecasts=new ArrayList<>();
        for (int i = 0; i < daily_forecastJsonArray.length(); i++) {
            String daily_forecastStr=daily_forecastJsonArray.getJSONObject(i).toString();
            LifestyleVo weatherForecast=gson.fromJson(daily_forecastStr,LifestyleVo.class);
            weatherForecasts.add(weatherForecast);
        }
        return weatherForecasts;
    }

}
