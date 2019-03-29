package com.gddst.lhy.weather;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.com.sky.downloader.greendao.CityVoDao;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.location.LocationInfoExt;
import com.gddst.app.lib_common.location.LocationIntentService;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.app.rxpermissions.RxPermissionsUtil;
import com.gddst.lhy.weather.fragment.WeatherFragment;
import com.gddst.lhy.weather.fragment.dummy.DummyContent;
import com.gddst.lhy.weather.util.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class WeatherActivity extends BaseActivity {
//    public SwipeRefreshLayout swipeRefres;
    public DrawerLayout drawerLayout;
    public ImageView im_pic;
//    //空气质量aqi
//    private TextView aqi_text;
//    private TextView pm25_text;
//    //标题
//    private TextView tv_title;
//    private TextView tv_time;
//    private ImageView title_image;
//    //当日天气
//    private TextView tv_Celsius;
//    private TextView tv_situation;
//
//    private ScrollView scrollView;
//
//    private LinearLayout day_linelayout;
//    private LinearLayout suggestion_linearlayout;
//
//    private String weatherId;
//
//    private Gson gson;
//
//    private WeatherVo newWeatherVo;

    public boolean isLocationValue=false;

//    private WeatherFragment weatherFragment;
    private ViewPager weatherViewPager;
    private WeatherAdapter weatherAdapter;
    private List<WeatherFragment> weatherFragmentList;
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
//        aqi_text = findViewById(R.id.aqi_text);
//        pm25_text = findViewById(R.id.pm25_text);
//
//        tv_title = findViewById(R.id.tv_title);
//        tv_time = findViewById(R.id.tv_time);
//        title_image = findViewById(R.id.title_image);
//        title_image.setOnClickListener(this);
//
//        tv_Celsius = findViewById(R.id.tv_Celsius);
//        tv_situation = findViewById(R.id.tv_situation);
//
//        scrollView = findViewById(R.id.scrollView);
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                swipeRefres.setEnabled(scrollView.getScrollY() == 0);
//            }
//        });
//
//        day_linelayout = findViewById(R.id.day_linelayout);
//        tv_time = findViewById(R.id.tv_time);
//        suggestion_linearlayout = findViewById(R.id.suggestion_linearlayout);

//        swipeRefres = findViewById(R.id.swipeRefres);
//        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
////                requestWeather(newWeatherVo);
////                getPicImage();
//                Log.i("tag","天气请求刷新++++++++++++++++++++++++++");
//            }
//        });

        im_pic = findViewById(R.id.im_pic);
        drawerLayout = findViewById(R.id.drawerLayout);
        weatherViewPager=findViewById(R.id.viewpager_weather);

    }

    @Override
    protected void initData() {
//        newWeatherVo=new WeatherVo();
//        if (gson==null)
//            gson=new Gson();
        requestPermission();
        startLocationService();
//        initWeathert();

        initWeatherViewPager();
    }

    private void initWeatherViewPager() {
        weatherFragmentList=new ArrayList<>();
        List<CityVo> cityVoList= BaseApplication.getIns().getDaoSession()
                .getCityVoDao().queryBuilder().orderAsc(CityVoDao.Properties.Id).list();
        for (int i = 0; i < cityVoList.size(); i++) {
            WeatherFragment weatherFragment=WeatherFragment.getFragment(cityVoList.get(i).getCid());
            weatherFragmentList.add(weatherFragment);
        }
        weatherAdapter=new WeatherAdapter(getSupportFragmentManager());
        weatherViewPager.setAdapter(weatherAdapter);
    }

    private void requestPermission() {
        RxPermissionsUtil.requestEachRxPermission(
                this,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }

    private void startLocationService() {
        Intent locationIntentServiceIntent=new Intent(WeatherActivity.this, LocationIntentService.class);
        locationIntentServiceIntent.setPackage(getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(locationIntentServiceIntent);
        }else {
            startService(locationIntentServiceIntent);
        }
    }

//    private void initWeathert() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        final List<CityVo> cityVoList= BaseApplication.getIns().getDaoSession()
//                .getCityVoDao().queryBuilder().orderAsc(CityVoDao.Properties.Id).list();
//        if (cityVoList.size()>0){
//            String cityId=cityVoList.get(0).getCid();
//            String weatherVoString = sharedPreferences.getString(cityId, "");
//            if (TextUtils.isEmpty(weatherVoString)) {
//                //刚进来如果没有城市信息则去定位获取当前位置坐标
//                showLocationBefore();
//            } else {
//                WeatherVoToGson(weatherVoString);
//            }
//        }else {
//            //刚进来如果没有城市信息则去定位获取当前位置坐标
//            showLocationBefore();
//        }
//
//        String picUrl = sharedPreferences.getString(WeatherUtil.picUrl, "");
//        if (TextUtils.isEmpty(picUrl)) {
//            getPicImage();
//        } else {
//            showPicImage(picUrl);
//        }
//    }
//
//    private void WeatherVoToGson(String weatherVoString) {
//        WeatherVo weatherVo = gson.fromJson(weatherVoString, WeatherVo.class);
//        weatherId=weatherVo.getBasic().getCid();
//        isLocationValue=true;
//        long time= DateUtil.timeSub(weatherVo.getUpdateTime(),DateUtil.getNow());
//        if (time>= WeatherUtil.weatherUpdateTimeInterval){
//            showTimeOutBefore();
//            requestWeather(weatherVo);
//        }else {
//            showText(weatherVo);
//        }
//    }
//
//    private void showLocationBefore() {
//        isLocationValue=false;
//        swipeRefres.setRefreshing(true);
//        tv_title.setText("正在获取当前所在城市");
//        tv_Celsius.setText("暂无数据");
//        tv_situation.setText("暂无数据");
//    }
//
//    private void showTimeOutBefore() {
//        swipeRefres.setRefreshing(true);
//        tv_title.setText("数据已过期正在更新");
//    }
//
//    private void showPicImage(String picUrl) {
//        Glide.with(WeatherActivity.this).load(picUrl).into(im_pic);
//    }
//
//    private void requestWeather(String cityName) {
//        NetManager.INSTANCE.getShopClient()
//                .getCityId(Keys.key,cityName)
//                .subscribeOn(Schedulers.io())
//                .map(new Function<Response<ResponseBody>, String>() {
//                    @Override
//                    public String apply(Response<ResponseBody> response) throws Exception {
//                        return getCityId(response, gson);
//                    }
//                })
//                .flatMap(new Function<String, ObservableSource<WeatherVo>>() {
//                    @Override
//                    public ObservableSource<WeatherVo> apply(String weatherCode) throws Exception {
//                        return getZip(
//                                getobservableNow(weatherCode),
//                                getobservableAirNow(weatherCode));
//                    }
//                })
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
//
//    }
//
//    private void requestWeather(WeatherVo weatherVo) {
//        Observable.just(weatherVo)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<WeatherVo, ObservableSource<WeatherVo>>() {
//                    @Override
//                    public ObservableSource<WeatherVo> apply(WeatherVo weatherVo) throws Exception {
//                        return getZip(getobservableNow(weatherVo.getBasic().getCid()),
//                                getobservableAirNow(weatherVo.getBasic().getCid()));
//                    }
//                })
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
//
//    }
//
//    private Observable getZip(Observable observableNow,Observable observableAirNow){
//        return Observable.zip(observableNow, observableAirNow, new BiFunction() {
//            @Override
//            public Object apply(Object o, Object o2) throws Exception {
//                if (o instanceof WeatherVo&&o2 instanceof AirNow){
//                    WeatherVo weatherVo= (WeatherVo) o;
//                    AirNow airNow= (AirNow) o2;
//                    weatherVo.setAirNow(airNow);
//                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
//                            (WeatherActivity.this).edit();
//                    editor.putString(weatherVo.getBasic().getCid(), gson.toJson(weatherVo));
//                    editor.apply();
//
//                    //保存城市信息
//                    CityVo cityVo=new CityVo();
//                    cityVo.setCid(weatherVo.getBasic().getCid());
//                    cityVo.setLocation(weatherVo.getBasic().getLocation());
//                    cityVo.setAdmin_area(weatherVo.getBasic().getAdmin_area());
//                    cityVo.setCnty(weatherVo.getBasic().getCnty());
//                    cityVo.setLat(weatherVo.getBasic().getLat());
//                    cityVo.setLon(weatherVo.getBasic().getLon());
//                    cityVo.setParent_city(weatherVo.getBasic().getParent_city());
//                    cityVo.setTz(weatherVo.getBasic().getTz());
//                    BaseApplication.getIns().getDaoSession().getCityVoDao().insertOrReplace(cityVo);
//
//                    return weatherVo;
//                }
//                return new WeatherVo();
//            }
//        });
//
//    }
//
//    private Observable getobservableNow(String weatherId){
//        return NetManager.INSTANCE.getShopClient()
//                .getWeatherNow(Keys.key, weatherId)
//                .map(new Function<Response<ResponseBody>, WeatherVo>() {
//                    @Override
//                    public WeatherVo apply(Response<ResponseBody> response) throws Exception {
//                        return ResponseToWeatherVo(response, gson);
//                    }
//                });
//    }
//    private Observable getobservableAirNow(String weatherId){
//        return NetManager.INSTANCE.getShopClient()
//                .getAirNow(Keys.key, weatherId)
//                .map(new Function<Response<ResponseBody>, AirNow>() {
//                    @Override
//                    public AirNow apply(Response<ResponseBody> response) throws Exception {
//                        return ResponseToAirNow(response, gson);
//                    }
//                });
//
//    }
//
//    private void getPicImage() {
//        NetManager.INSTANCE.getShopClient()
//                .getPicUrl()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DlObserve<Response<ResponseBody>>() {
//                    @Override
//                    public void onResponse(Response<ResponseBody> s) throws IOException {
//                        if (s.code() == 200) {
//                            String picUrl = s.body().string();
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
//                                    (WeatherActivity.this).edit();
//                            editor.putString(WeatherUtil.picUrl, picUrl);
//                            editor.apply();
//                            showPicImage(picUrl);
//                        }
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorMsg) {
//                        Toast.makeText(WeatherActivity.this, errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    private void showText(WeatherVo weatherVo) {
//        if (weatherVo == null)
//            return;
//        newWeatherVo=weatherVo;
//        List<WeatherVo.LifestyleBean> lifestyleVos = weatherVo.getLifestyle();
//        List<WeatherVo.DailyForecastBean> weatherForecasts = weatherVo.getDaily_forecast();
//        AirNow airNow = weatherVo.getAirNow();
//        WeatherVo.NowBean now = weatherVo.getNow();
//
//        if (lifestyleVos == null)
//            return;
//        suggestion_linearlayout.removeAllViews();
//        for (WeatherVo.LifestyleBean lifestyleBase : lifestyleVos) {
//            View view = getLayoutInflater().inflate(R.layout.item_suggestion_text, suggestion_linearlayout, false);
//            TextView item_suggestion_text_tv = view.findViewById(R.id.item_suggestion_text_tv);
//            String text = "";
//            switch (lifestyleBase.getType()){
//                case WeatherUtil.comf:
//                    text="舒适度指数";
//                    break;
//                case WeatherUtil.cw:
//                    text="洗车指数";
//                    break;
//                case WeatherUtil.drsg:
//                    text="穿衣指数";
//                    break;
//                case WeatherUtil.flu:
//                    text="感冒指数";
//                    break;
//                case WeatherUtil.sport:
//                    text="运动指数";
//                    break;
//                case WeatherUtil.trav:
//                    text="旅游指数";
//                    break;
//                case WeatherUtil.uv:
//                    text="紫外线指数";
//                    break;
//                case WeatherUtil.air:
//                    text="空气污染扩散条件指数";
//                    break;
//                default:
//
//                    break;
//            }
//            if (lifestyleVos.indexOf(lifestyleBase)==lifestyleVos.size()){
//                text+=":"+lifestyleBase.getBrf() + "  " + lifestyleBase.getTxt();
//            }else {
//                text+=":"+lifestyleBase.getBrf() + "  " + lifestyleBase.getTxt()+"\n";
//            }
//            item_suggestion_text_tv.setText(text);
//            suggestion_linearlayout.addView(item_suggestion_text_tv);
//        }
//
//        if (weatherForecasts == null)
//            return;
//        day_linelayout.removeAllViews();
//        for (WeatherVo.DailyForecastBean forecastBase : weatherForecasts) {
//            View view = getLayoutInflater().inflate(R.layout.item_day_text, day_linelayout, false);
//            TextView tv_date = view.findViewById(R.id.tv_date);
//            TextView tv_two = view.findViewById(R.id.tv_two);
//            TextView tv_max = view.findViewById(R.id.tv_max);
//            TextView tv_min = view.findViewById(R.id.tv_min);
//            tv_date.setText(forecastBase.getDate());
//            tv_two.setText(forecastBase.getCond_txt_d());
//            tv_max.setText(forecastBase.getTmp_max());
//            tv_min.setText(forecastBase.getTmp_min());
//            day_linelayout.addView(view);
//        }
//
//        aqi_text.setText(TextUtils.isEmpty(airNow.getAqi())?"暂无数据":airNow.getAqi());
//        pm25_text.setText(TextUtils.isEmpty(airNow.getPm25())?"暂无数据":airNow.getPm25());
//
//
//        if (now != null) {
//            tv_Celsius.setText(now.getFl() + "℃");
//            tv_situation.setText(now.getCond_txt());
//        }
//
//        tv_title.setText(weatherVo.getBasic().getLocation());
//        String timeStr=weatherVo.getUpdate().getLoc().split(" ")[1];
//        tv_time.setText(timeStr);
//    }
//
//
//    private WeatherVo ResponseToWeatherVo(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
//        if (response.code() != 200 || gson == null) {
//            return new WeatherVo();
//        }
//        String body = response.body().string();
//        JSONObject jsonObject = new JSONObject(body);
//        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
//        JSONObject weatherObject = jsonArray.getJSONObject(0);
//        String status=weatherObject.getString(WeatherUtil.status);
//        if (status.equals(WeatherUtil.ok)){
//            WeatherVo weatherVo = gson.fromJson(weatherObject.toString(), WeatherVo.class);
//            weatherVo.setUpdateTime(DateUtil.getNow());
//            return weatherVo;
//        }
//        return new WeatherVo();
//    }
//
//    private AirNow ResponseToAirNow(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
//        if (response.code() != 200 || gson == null) {
//            return new AirNow();
//        }
//        String body = response.body().string();
//        JSONObject jsonObject = new JSONObject(body);
//        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
//        JSONObject weatherObject = jsonArray.getJSONObject(0);
//        String status=weatherObject.getString(WeatherUtil.status);
//        if (status.equals(WeatherUtil.ok)){
//            AirNow airNow = gson.fromJson(weatherObject.getJSONObject(WeatherUtil.air_now_city).toString(), AirNow.class);
//            return airNow;
//        }
//        return new AirNow();
//    }
//
//    private String getCityId(Response<ResponseBody> response, Gson gson) throws IOException, JSONException {
//        if (response.code() != 200 || gson == null) {
//            return "";
//        }
//        String body = response.body().string();
//        JSONObject jsonObject = new JSONObject(body);
//        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
//        JSONObject weatherObject = jsonArray.getJSONObject(0);
//        JSONObject jsonObjectCity=(weatherObject.getJSONArray(WeatherUtil.basic)).getJSONObject(0);
//        String cityId = jsonObjectCity.getString(WeatherUtil.cid);
//        return cityId;
//    }

    private class WeatherAdapter extends FragmentPagerAdapter {

        public WeatherAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return weatherFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return weatherFragmentList.size();
        }
    }

    @Override
    protected void onMessageEvent(Object event) {
        super.onMessageEvent(event);
        //页面启动后首次成功定位来回调此方法
        if (event instanceof LocationInfoExt&&!isLocationValue){
            LocationInfoExt locationInfoExt= (LocationInfoExt) event;
            String location=locationInfoExt.getLongitude()+","+locationInfoExt.getLatitude();
//            weatherFragment.requestWeather(location);

            if (weatherFragmentList.size()==0){
                //这里需要通过坐标信息去拿城市id
                getCityId(location);
            }
        }
        else if (event instanceof DummyContent.DummyItem){
            DummyContent.DummyItem item= (DummyContent.DummyItem) event;
            String cityName=item.name;

            if (drawerLayout!=null){
                drawerLayout.closeDrawers();
            }
            //这里需要通过城市名字去拿城市id
            getCityId(cityName);
        }
    }

    private void getCityId(String cityName) {
        NetManager.INSTANCE.getShopClient()
                .getCityId(Keys.key,cityName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, String>() {
                    @Override
                    public String apply(Response<ResponseBody> response) throws Exception {
                        return JsonUtils.getCityId(response);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<String>() {
                    @Override
                    public void onResponse(String s) throws IOException {
                        if (Thread.currentThread() == Looper.getMainLooper().getThread()){
                            Log.i("tag",Thread.currentThread().getName());
                        }
                        Log.i("tag",Thread.currentThread().getName());
                        WeatherFragment weatherFragment=WeatherFragment.getFragment(s);
                        weatherFragmentList.add(weatherFragment);
                        weatherAdapter.notifyDataSetChanged();
                        weatherViewPager.setCurrentItem(weatherFragmentList.indexOf(weatherFragment));
                        weatherFragment.requestWeather(s);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }
}
