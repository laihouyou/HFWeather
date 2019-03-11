package com.gddst.lhy.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNowCity;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.basic.Update;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

import java.util.List;

public class WeatherActivity extends BaseActivity {
    //空气质量aqi
    private TextView aqi_text;
    private TextView pm25_text;
//    //每天的天气预报情况
//    private TextView tv_date;
//    private TextView tv_two;
//    private TextView tv_max;
//    private TextView tv_min;
    //生活建议
//    private TextView comfort_text;
//    private TextView car_wash_text;
//    private TextView sport_text;
    //标题
    private TextView tv_title;
    private TextView tv_time;
    //当日天气
    private TextView tv_Celsius;
    private TextView tv_situation;

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

//        tv_date=findViewById(R.id.tv_date);
//        tv_two=findViewById(R.id.tv_two);
//        tv_max=findViewById(R.id.tv_max);
//        tv_min=findViewById(R.id.tv_min);

//        comfort_text=findViewById(R.id.comfort_text);
//        car_wash_text=findViewById(R.id.car_wash_text);
//        sport_text=findViewById(R.id.sport_text);

        tv_title=findViewById(R.id.tv_title);
        tv_time=findViewById(R.id.tv_time);

        tv_Celsius=findViewById(R.id.tv_Celsius);
        tv_situation=findViewById(R.id.tv_situation);

        day_linelayout=findViewById(R.id.day_linelayout);
        tv_time=findViewById(R.id.tv_time);
        suggestion_linearlayout=findViewById(R.id.suggestion_linearlayout);

        swipeRefres=findViewById(R.id.swipeRefres);
        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

    }

    @Override
    protected void initData() {
        weatherId=getIntent().getStringExtra(WeatherUtil.weatherId);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String nowBaseString=sharedPreferences.getString(WeatherUtil.nowBase,"");
        String forecastBaseListString=sharedPreferences.getString(WeatherUtil.forecastBaseList,"");
        String airNowCityString=sharedPreferences.getString(WeatherUtil.airNowCity,"");
        String lifestyleBaseListString=sharedPreferences.getString(WeatherUtil.lifestyleBaseList,"");
        if (isNullStr(nowBaseString,forecastBaseListString,airNowCityString,lifestyleBaseListString)){
            //获取每日天气数据
            requestWeather(weatherId);
        }else {
            Gson gson=new Gson();

            Now now=gson.fromJson(nowBaseString,Now.class);
            showNowText(now);

            List<ForecastBase> forecastBaseList=gson.fromJson(forecastBaseListString,
                    new TypeToken<List<ForecastBase>>(){}.getType());
            showForecastText(forecastBaseList);

            AirNowCity airNowCity=gson.fromJson(airNowCityString,AirNowCity.class);
            showAirNowCityText(airNowCity);

            List<LifestyleBase> lifestyleBaseList=gson.fromJson(lifestyleBaseListString,
                    new TypeToken<List<LifestyleBase>>(){}.getType());
            showLifestyleBaseText(lifestyleBaseList);
        }
    }

    private boolean isNullStr(String nowBaseString,String forecastBaseListString,
                              String airNowCityString,String lifestyleBaseListString) {
        return TextUtils.isEmpty(nowBaseString)
                ||TextUtils.isEmpty(forecastBaseListString)
                ||TextUtils.isEmpty(airNowCityString)
                ||TextUtils.isEmpty(lifestyleBaseListString);
    }

    private void requestWeather(String weatherId) {
        weatherId="广州";
        final Gson gson=new Gson();
        final boolean[] weatherNow = {false};
        final boolean[] weatherForecast = {false};
        final boolean[] weatherAirNow = {false};
        final boolean[] weatherLifeStyle = {false};
        HeWeather.getWeatherNow(this, weatherId, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i("tag",throwable.getMessage());
                weatherNow[0] =false;
            }

            @Override
            public void onSuccess(List<Now> list) {
                Log.i("tag",list.toString());
                if (list.size()>0){
                    final Now now=list.get(0);
                    String startus=now.getStatus();
                    if (startus.equals("ok")){
                        weatherNow[0] =true;
                    }
                    NowBase nowBase= now.getNow();
                    if (nowBase!=null){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences
                                (WeatherActivity.this).edit();
                        editor.putString(WeatherUtil.nowBase,gson.toJson(nowBase));
                        editor.apply();

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNowText(now);
                        }
                    });
                }
            }
        });
        HeWeather.getWeatherForecast(this, weatherId, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                weatherForecast[0] =false;
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                Log.i("tag",list.toString());
                if (list.size()>0){
                    Forecast forecast=list.get(0);
                    if (forecast.getStatus().equals("ok")){
                        weatherForecast[0] =true;
                    }
                    final List<ForecastBase> forecastBaseList= forecast.getDaily_forecast();
                    if (forecastBaseList!=null){
                        String forecastBaseListStr=gson.toJson(forecastBaseList);
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences
                                (WeatherActivity.this).edit();
                        editor.putString(WeatherUtil.forecastBaseList,forecastBaseListStr);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               showForecastText(forecastBaseList);
                            }
                        });
                    }
                }
            }
        });
        HeWeather.getAirNow(this, weatherId, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                weatherAirNow[0] =false;
            }

            @Override
            public void onSuccess(List<AirNow> list) {
                Log.i("tag",list.toString());
                if (list.size()>0){
                    AirNow airNow=list.get(0);
                    if (airNow.getStatus().equals("ok")){
                        weatherAirNow[0] =true;
                    }
                    final AirNowCity airNowCity= airNow.getAir_now_city();
                    if (airNowCity!=null){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences
                                (WeatherActivity.this).edit();
                        editor.putString(WeatherUtil.airNowCity,gson.toJson(airNowCity));
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAirNowCityText(airNowCity);
                            }
                        });
                    }
                }
            }
        });
        HeWeather.getWeatherLifeStyle(this, weatherId, new HeWeather.OnResultWeatherLifeStyleBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                weatherLifeStyle[0] =false;
            }

            @Override
            public void onSuccess(List<Lifestyle> list) {
                Log.i("tag",list.toString());
                if (list.size()>0){
                    Lifestyle lifestyle=list.get(0);
                    if (lifestyle.getStatus().equals("ok")){
                        weatherLifeStyle[0] =true;
                    }
                    final List<LifestyleBase> lifestyleBaseList= lifestyle.getLifestyle();
                    if (lifestyleBaseList!=null){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences
                                (WeatherActivity.this).edit();
                        editor.putString(WeatherUtil.lifestyleBaseList,gson.toJson(lifestyleBaseList));
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLifestyleBaseText(lifestyleBaseList);
                            }
                        });
                    }
                }
            }
        });

    }

    private void showLifestyleBaseText(List<LifestyleBase> lifestyleBaseList) {
        if (lifestyleBaseList==null)
            return;
        suggestion_linearlayout.removeAllViews();
        for (LifestyleBase lifestyleBase:lifestyleBaseList){
            View view=getLayoutInflater().inflate(R.layout.item_suggestion_text,suggestion_linearlayout,false);
            TextView item_suggestion_text_tv=view.findViewById(R.id.item_suggestion_text_tv);
            item_suggestion_text_tv.setText(lifestyleBase.getBrf()+":"+lifestyleBase.getTxt());
            suggestion_linearlayout.addView(item_suggestion_text_tv);
        }

    }

    private void showAirNowCityText(AirNowCity airNowCity) {
        aqi_text.setText(airNowCity.getAqi());
        pm25_text.setText(airNowCity.getPm25());
    }

    private void showNowText(Now now) {
        NowBase nowBase=now.getNow();
        if (nowBase!=null){
            tv_Celsius.setText(nowBase.getFl()+"℃");
            tv_situation.setText(nowBase.getCond_txt());
        }
        Basic basic=now.getBasic();
        if (basic!=null){
            tv_title.setText(basic.getLocation());
        }
        Update update=now.getUpdate();
        if (update!=null){
            tv_time.setText(update.getLoc());
        }
    }

    private void showForecastText(List<ForecastBase> forecastBaseList) {
        if (forecastBaseList==null)
            return;
        day_linelayout.removeAllViews();
        for (ForecastBase forecastBase:forecastBaseList){
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
    }
}
