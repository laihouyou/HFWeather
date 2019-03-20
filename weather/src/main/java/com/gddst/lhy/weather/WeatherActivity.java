package com.gddst.lhy.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.app.lib_common.location.LocationInfoExt;
import com.gddst.app.lib_common.location.trace.Agps;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.lhy.weather.fragment.ProvinceCityFragment;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.gddst.lhy.weather.vo.*;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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

public class WeatherActivity extends BaseActivity implements View.OnClickListener,
        ProvinceCityFragment.OnFragmentToActivityListener {
    //空气质量aqi
    private TextView aqi_text;
    private TextView pm25_text;
    //标题
    private TextView tv_title;
    private TextView tv_time;
    private ImageView title_image;
    //当日天气
    private TextView tv_Celsius;
    private TextView tv_situation;

    private ImageView im_pic;
    private ScrollView scrollView;

    private LinearLayout day_linelayout;
    private LinearLayout suggestion_linearlayout;
    private SwipeRefreshLayout swipeRefres;
    private DrawerLayout drawerLayout;

    private String weatherId;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int BindLayout() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        return R.layout.activity_weather;
    }

    @Override
    protected void initView() {
        aqi_text = findViewById(R.id.aqi_text);
        pm25_text = findViewById(R.id.pm25_text);

        tv_title = findViewById(R.id.tv_title);
        tv_time = findViewById(R.id.tv_time);
        title_image = findViewById(R.id.title_image);
        title_image.setOnClickListener(this);

        tv_Celsius = findViewById(R.id.tv_Celsius);
        tv_situation = findViewById(R.id.tv_situation);

        im_pic = findViewById(R.id.im_pic);
        scrollView = findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefres.setEnabled(scrollView.getScrollY() == 0);
            }
        });

        day_linelayout = findViewById(R.id.day_linelayout);
        tv_time = findViewById(R.id.tv_time);
        suggestion_linearlayout = findViewById(R.id.suggestion_linearlayout);

        swipeRefres = findViewById(R.id.swipeRefres);
        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                getPicImage();
            }
        });

        drawerLayout = findViewById(R.id.drawerLayout);
    }

    @Override
    protected void initData() {
        if (gson==null)
            gson=new Gson();
        final Agps agps=new Agps();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherVoString = sharedPreferences.getString(WeatherUtil.weatherVo, "");
        weatherVoString="";
//        weatherId = getIntent().getStringExtra(WeatherUtil.weatherId);
        if (TextUtils.isEmpty(weatherVoString)) {
            while (Agps.getLocation()==null){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //刚进来如果没有城市信息则去定位获取当前位置坐标
            Observable.just(1)
                    .map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer integer) throws Exception {
                            LocationInfoExt locationInfoExt=Agps.getLocation();
                            return locationInfoExt.getLongitudeGcj02()+","+locationInfoExt.getLatitudeGcj02();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DlObserve<String>() {
                        @Override
                        public void onResponse(String s) throws IOException {
                            //获取每日天气
                            requestWeather(s);
                        }

                        @Override
                        public void onError(int errorCode, String errorMsg) {
                            Log.i("tag", errorMsg);
                        }
                    });

        } else {
            WeatherVo weatherVo = gson.fromJson(weatherVoString, WeatherVo.class);
            weatherId=weatherVo.getBasic().getCid();
            showText(weatherVo);

        }

        String picUrl = sharedPreferences.getString(WeatherUtil.picUrl, "");
        if (TextUtils.isEmpty(picUrl)) {
            getPicImage();
        } else {
            showPicImage(picUrl);
        }
    }

    private void showPicImage(String picUrl) {
        Glide.with(WeatherActivity.this).load(picUrl).into(im_pic);
    }

    private void requestWeather(String weatherId) {
//        weatherId = "CN101240703";

//        getZip(
//                getobservableNow(weatherId),
//                getobservableAirNow(weatherId),
//                getobservableForecast(weatherId),
//                getobservableLifestyle(weatherId))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DlObserve<WeatherVo>() {
//                    @Override
//                    public void onResponse(WeatherVo weatherVo) throws IOException {
//                        showText(weatherVo);
//                        swipeRefres.setRefreshing(false);
//                        Toast.makeText(WeatherActivity.this, weatherVo.getStatus(), Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorMsg) {
//                        Toast.makeText(WeatherActivity.this, errorMsg, Toast.LENGTH_LONG).show();
//                        swipeRefres.setRefreshing(false);
//                    }
//                });

        NetManager.INSTANCE.getShopClient()
                .getCityId(Keys.key,weatherId)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, String>() {
                    @Override
                    public String apply(Response<ResponseBody> response) throws Exception {
                        return getCityId(response, gson);
                    }
                })
                .flatMap(new Function<String, ObservableSource<WeatherVo>>() {
                    @Override
                    public ObservableSource<WeatherVo> apply(String weatherCode) throws Exception {
                        return getZip(
                                getobservableNow(weatherCode),
                                getobservableAirNow(weatherCode),
                                getobservableForecast(weatherCode),
                                getobservableLifestyle(weatherCode));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<WeatherVo>() {
                    @Override
                    public void onResponse(WeatherVo weatherVo) throws IOException {
                        showText(weatherVo);
                        swipeRefres.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, weatherVo.getStatus(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(WeatherActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        swipeRefres.setRefreshing(false);
                    }
                });

    }

    private Observable getZip(Observable observableNow,Observable observableAirNow,
                              Observable observableForecast,Observable observableLifestyle){
        return Observable.zip(observableNow, observableAirNow, observableForecast, observableLifestyle,
                new Function4<WeatherVo, AirNow, List<WeatherForecast>, List<LifestyleVo>, WeatherVo>() {
                    @Override
                    public WeatherVo apply(
                            WeatherVo weatherVo,
                            com.gddst.lhy.weather.vo.AirNow airNow,
                            List<WeatherForecast> weatherForecasts,
                            List<LifestyleVo> lifestyles) throws Exception {
                        weatherVo.setAirNow(airNow);
                        weatherVo.setWeatherForecastList(weatherForecasts);
                        weatherVo.setLifestyleVoList(lifestyles);

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
                                (WeatherActivity.this).edit();
                        editor.putString(WeatherUtil.weatherVo, gson.toJson(weatherVo));
                        editor.apply();

                        return weatherVo;
                    }
                });
    }

    private Observable getobservableNow(String weatherId){
        return NetManager.INSTANCE.getShopClient()
                .getWeatherNow(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, WeatherVo>() {
                    @Override
                    public WeatherVo apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToWeatherVo(response, gson);
                    }
                });
    }
    private Observable getobservableAirNow(String weatherId){
        return NetManager.INSTANCE.getShopClient()
                .getAirNow(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, AirNow>() {
                    @Override
                    public AirNow apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToAirNow(response, gson);
                    }
                });

    }
    private Observable getobservableForecast(String weatherId){
        return   NetManager.INSTANCE.getShopClient()
                .getWeatherForecast(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, List<WeatherForecast>>() {
                    @Override
                    public List<WeatherForecast> apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToWeatherForecast(response, gson);
                    }
                });
    }
    private Observable getobservableLifestyle(String weatherId){
        return   NetManager.INSTANCE.getShopClient()
                .getWeatherLifeStyle(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, List<LifestyleVo>>() {
                    @Override
                    public List<LifestyleVo> apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToLifestyle(response, gson);
                    }
                });
    }

    private void getPicImage() {
        NetManager.INSTANCE.getShopClient()
                .getPicUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<Response<ResponseBody>>() {
                    @Override
                    public void onResponse(Response<ResponseBody> s) throws IOException {
                        if (s.code() == 200) {
                            String picUrl = s.body().string();
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
                                    (WeatherActivity.this).edit();
                            editor.putString(WeatherUtil.picUrl, picUrl);
                            editor.apply();
                            showPicImage(picUrl);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(WeatherActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showText(WeatherVo weatherVo) {
        if (weatherVo == null)
            return;
        List<LifestyleVo> lifestyleVos = weatherVo.getLifestyleVoList();
        List<WeatherForecast> weatherForecasts = weatherVo.getWeatherForecastList();
        AirNow airNow = weatherVo.getAirNow();
        Now now = weatherVo.getNow();

        if (lifestyleVos == null)
            return;
        suggestion_linearlayout.removeAllViews();
        for (LifestyleVo lifestyleBase : lifestyleVos) {
            View view = getLayoutInflater().inflate(R.layout.item_suggestion_text, suggestion_linearlayout, false);
            TextView item_suggestion_text_tv = view.findViewById(R.id.item_suggestion_text_tv);
            String text = "";
            switch (lifestyleBase.getType()){
                case WeatherUtil.comf:
                    text="舒适度指数";
                    break;
                case WeatherUtil.cw:
                    text="洗车指数";
                    break;
                case WeatherUtil.drsg:
                    text="穿衣指数";
                    break;
                case WeatherUtil.flu:
                    text="感冒指数";
                    break;
                case WeatherUtil.sport:
                    text="运动指数";
                    break;
                case WeatherUtil.trav:
                    text="旅游指数";
                    break;
                case WeatherUtil.uv:
                    text="紫外线指数";
                    break;
                case WeatherUtil.air:
                    text="空气污染扩散条件指数";
                    break;
                default:

                    break;
            }
            if (lifestyleVos.indexOf(lifestyleBase)==lifestyleVos.size()){
                text+=":"+lifestyleBase.getBrf() + "  " + lifestyleBase.getTxt();
            }else {
                text+=":"+lifestyleBase.getBrf() + "  " + lifestyleBase.getTxt()+"\n";
            }
            item_suggestion_text_tv.setText(text);
            suggestion_linearlayout.addView(item_suggestion_text_tv);
        }

        if (weatherForecasts == null)
            return;
        day_linelayout.removeAllViews();
        for (WeatherForecast forecastBase : weatherForecasts) {
            View view = getLayoutInflater().inflate(R.layout.item_day_text, day_linelayout, false);
            TextView tv_date = view.findViewById(R.id.tv_date);
            TextView tv_two = view.findViewById(R.id.tv_two);
            TextView tv_max = view.findViewById(R.id.tv_max);
            TextView tv_min = view.findViewById(R.id.tv_min);
            tv_date.setText(forecastBase.getDate());
            tv_two.setText(forecastBase.getCond_txt_d());
            tv_max.setText(forecastBase.getTmp_max());
            tv_min.setText(forecastBase.getTmp_min());
            day_linelayout.addView(view);
        }

        aqi_text.setText(TextUtils.isEmpty(airNow.getAqi())?"暂无数据":airNow.getAqi());
        pm25_text.setText(TextUtils.isEmpty(airNow.getPm25())?"暂无数据":airNow.getPm25());


        if (now != null) {
            tv_Celsius.setText(now.getFl() + "℃");
            tv_situation.setText(now.getCond_txt());
        }

        tv_title.setText(weatherVo.getBasic().getLocation());
        tv_time.setText(weatherVo.getUpdate().getLoc());
    }


    private WeatherVo ResponseToWeatherVo(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
        if (response.code() != 200 || gson == null) {
            return new WeatherVo();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            WeatherVo weatherVo = gson.fromJson(weatherObject.toString(), WeatherVo.class);
            return weatherVo;
        }
        return new WeatherVo();
    }

    private AirNow ResponseToAirNow(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
        if (response.code() != 200 || gson == null) {
            return new AirNow();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            AirNow airNow = gson.fromJson(weatherObject.getJSONObject(WeatherUtil.air_now_city).toString(), AirNow.class);
            return airNow;
        }
        return new AirNow();
    }

    private List<WeatherForecast> ResponseToWeatherForecast(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
        if (response.code() != 200 || gson == null) {
            return new ArrayList<>();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            JSONArray daily_forecastJsonArray = weatherObject.getJSONArray(WeatherUtil.daily_forecast);
            List<WeatherForecast> weatherForecasts = new ArrayList<>();
            for (int i = 0; i < daily_forecastJsonArray.length(); i++) {
                String daily_forecastStr = daily_forecastJsonArray.getJSONObject(i).toString();
                WeatherForecast weatherForecast = gson.fromJson(daily_forecastStr, WeatherForecast.class);
                weatherForecasts.add(weatherForecast);
            }
            return weatherForecasts;
        }
        return new ArrayList<>();
    }

    private List<LifestyleVo> ResponseToLifestyle(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
        if (response.code() != 200 || gson == null) {
            return new ArrayList<>();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            JSONArray daily_forecastJsonArray = weatherObject.getJSONArray(WeatherUtil.lifestyle);
            List<LifestyleVo> weatherForecasts = new ArrayList<>();
            for (int i = 0; i < daily_forecastJsonArray.length(); i++) {
                String daily_forecastStr = daily_forecastJsonArray.getJSONObject(i).toString();
                LifestyleVo weatherForecast = gson.fromJson(daily_forecastStr, LifestyleVo.class);
                weatherForecasts.add(weatherForecast);
            }
            return weatherForecasts;
        }
        return new ArrayList<>();
    }

    private String getCityId(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
        if (response.code() != 200 || gson == null) {
            return "";
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        JSONObject jsonObjectCity=(weatherObject.getJSONArray(WeatherUtil.basic)).getJSONObject(0);
        String cityId = jsonObjectCity.getString(WeatherUtil.cid);
        return cityId;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_image) {
            ProvinceCityFragment fragment = new ProvinceCityFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.commit();
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onFragmentToActivityPutVaule(String vaule) {
        if (drawerLayout!=null){
            drawerLayout.closeDrawers();
        }
        weatherId=vaule;
        requestWeather(vaule);
        getPicImage();
    }
}
