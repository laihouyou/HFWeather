package com.gddst.lhy.weather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.com.sky.downloader.greendao.CityVoDao;
import com.gddst.app.lib_common.MPAndroidChart.LineChartManager;
import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.base.fragment.BaseFragment;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.utils.DateUtil;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.lhy.weather.R;
import com.gddst.lhy.weather.WeatherActivity;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.gddst.lhy.weather.vo.AirNow;
import com.gddst.lhy.weather.vo.WeatherVo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class WeatherFragment extends BaseFragment {
//    private SwipeRefreshLayout swipeRefres;
    //空气质量aqi
    private TextView aqi_text;
    private TextView pm25_text;
    //当日天气
    private TextView tv_Celsius;
    private TextView tv_situation;

    private NestedScrollView scrollView;

    private LinearLayout day_linelayout;
    private LinearLayout suggestion_linearlayout;

    private LineChart lineChart_host24;

    private WeatherActivity context;

    private String cityCid;

    public static WeatherFragment getFragment(String cityCid){
        WeatherFragment weatherFragment=new WeatherFragment();
        Bundle bundle=new Bundle();
        bundle.putString(WeatherUtil.cid,cityCid);
        weatherFragment.setArguments(bundle);
        return weatherFragment;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= (WeatherActivity) getActivity();
        return getLayoutInflater().inflate(R.layout.fragment_weather_layout,container,false);
    }

    @Override
    protected void initView(View view) {
        aqi_text = view.findViewById(R.id.aqi_text);
        pm25_text = view.findViewById(R.id.pm25_text);

        tv_Celsius = view.findViewById(R.id.tv_Celsius);
        tv_situation = view.findViewById(R.id.tv_situation);

        scrollView = view.findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                context.swipeRefres.setEnabled(scrollView.getScrollY() == 0);
            }
        });

        day_linelayout = view.findViewById(R.id.day_linelayout);
        suggestion_linearlayout = view.findViewById(R.id.suggestion_linearlayout);

        lineChart_host24 = view.findViewById(R.id.host_24);
    }

    private void intiLineChartView(List<WeatherVo.HourlyBean> hourlyBeanList) {
        //设置X轴数据格式
        XAxis xAxis=lineChart_host24.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i= (int) value;
                String time=hourlyBeanList.get(i).getTime();
                String timeStr=time.split(" ")[1];
                return timeStr;
            }
        });

        YAxis yAxis=lineChart_host24.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value+"°";
            }
        });

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < hourlyBeanList.size(); i++) {
            String tmp=hourlyBeanList.get(i).getTmp();
            values.add(new Entry(i,Float.parseFloat(tmp)));
        }

        LineChartManager.initSingleLineChart(context,lineChart_host24,values);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void lazyLoad() {
        if (getArguments()!=null){
            cityCid=getArguments().getString(WeatherUtil.cid);
        }
        initWeathert();
    }


    private void initWeathert() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(BaseApplication.getIns());
        String cityId=this.cityCid;
        String weatherVoString = sharedPreferences.getString(cityId, "");
        if (TextUtils.isEmpty(weatherVoString)) {
            //刚进来如果没有城市信息则去定位获取当前位置坐标
            showLocationBefore();
        } else {
            WeatherVoToGson(weatherVoString);
        }

        String picUrl = sharedPreferences.getString(WeatherUtil.picUrl, "");
        if (TextUtils.isEmpty(picUrl)) {
            getPicImage();
        } else {
            showPicImage(picUrl);
        }
    }

    private void WeatherVoToGson(String weatherVoString) {
        WeatherVo weatherVo = BaseApplication.getGson().fromJson(weatherVoString, WeatherVo.class);
        context.isLocationValue=true;
        long time= DateUtil.timeSub(weatherVo.getUpdateTime(),DateUtil.getNow());
        if (time>= WeatherUtil.weatherUpdateTimeInterval){
            showTimeOutBefore();
            requestWeather(weatherVo.getBasic().getCid(),weatherVo.getCityType());

        }else {
            showText(weatherVo);
        }
    }

    private void showLocationBefore() {
//        context.isLocationValue=false;
//        context.tv_title.setText("正在获取当前所在城市");
//        context.swipeRefres.setRefreshing(true);
        tv_Celsius.setText("暂无数据");
        tv_situation.setText("暂无数据");
    }

    private void showTimeOutBefore() {
        context.swipeRefres.setRefreshing(true);
        context.tv_title.setText("数据已过期正在更新");
    }

    private void showPicImage(String picUrl) {
        Glide.with(context).load(picUrl).into(context.im_pic);
    }

    public  Observable getWeatherObservable(String cityCid, final int cityType){
        return  Observable.just(cityCid)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<WeatherVo>>() {
                    @Override
                    public ObservableSource<WeatherVo> apply(String weatherCode) throws Exception {
                        return getZip(
                                getobservableNow(weatherCode,cityType),
                                getobservableAirNow(weatherCode),
                                cityType
                        );
                    }
                });
    }

    public void requestWeather(String cityCid, final int cityType) {
       getWeatherObservable(cityCid,cityType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<WeatherVo>() {
                    @Override
                    public void onResponse(WeatherVo weatherVo) throws IOException {
                        showText(weatherVo);
                        context.swipeRefres.setRefreshing(false);
                        Toast.makeText(context, weatherVo.getStatus(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                        context.swipeRefres.setRefreshing(false);
                    }
                });

    }

    private Observable getZip(Observable observableNow, Observable observableAirNow, final int cityType){
        return Observable.zip(observableNow, observableAirNow, new BiFunction<WeatherVo,AirNow,WeatherVo>() {
            @Override
            public WeatherVo apply(WeatherVo weatherVo, AirNow airNow) throws Exception {
                weatherVo.setAirNow(airNow);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(BaseApplication.getIns()).edit();
                editor.putString(weatherVo.getBasic().getCid(), BaseApplication.getGson().toJson(weatherVo));
                editor.apply();

                //保存城市信息
                CityVo cityVo = new CityVo();
                cityVo.setCid(weatherVo.getBasic().getCid());
                cityVo.setLocation(weatherVo.getBasic().getLocation());
                cityVo.setAdmin_area(weatherVo.getBasic().getAdmin_area());
                cityVo.setCnty(weatherVo.getBasic().getCnty());
                cityVo.setLat(weatherVo.getBasic().getLat());
                cityVo.setLon(weatherVo.getBasic().getLon());
                cityVo.setParent_city(weatherVo.getBasic().getParent_city());
                cityVo.setTz(weatherVo.getBasic().getTz());
                cityVo.setAddCityTime(DateUtil.getNow());
                cityVo.setCityType(cityType);
                List<CityVo> cityVoList = BaseApplication.getIns().getDaoSession().getCityVoDao()
                        .queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVo.getCid())).list();
                if (cityVoList.size() == 0) {
                    BaseApplication.getIns().getDaoSession().getCityVoDao().insertOrReplace(cityVo);

                    Map<String, Object> parMap = new HashMap<>();
                    parMap.put(WeatherUtil.add_action, cityVo);
                    //数据插入成功发送
                    EventBus.getDefault().post(parMap);
                }

                return weatherVo;
            }
        });

    }

    private Observable getobservableNow(String weatherId, final int cityType){
        return NetManager.INSTANCE.getShopClient()
                .getWeatherNow(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, WeatherVo>() {
                    @Override
                    public WeatherVo apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToWeatherVo(response,cityType);
                    }
                });
    }
    private Observable getobservableAirNow(String weatherId){
        return NetManager.INSTANCE.getShopClient()
                .getAirNow(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, AirNow>() {
                    @Override
                    public AirNow apply(Response<ResponseBody> response) throws Exception {
                        AirNow airNow=ResponseToAirNow(response);
                        return airNow;
                    }
                });

    }

    public void getPicImage() {
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
                                    (BaseApplication.getIns()).edit();
                            editor.putString(WeatherUtil.picUrl, picUrl);
                            editor.apply();
                            showPicImage(picUrl);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showText(WeatherVo weatherVo) {
        if (weatherVo == null)
            return;
//        context.newWeatherVo=weatherVo;
        List<WeatherVo.LifestyleBean> lifestyleVos = weatherVo.getLifestyle();
        List<WeatherVo.DailyForecastBean> weatherForecasts = weatherVo.getDaily_forecast();
        AirNow airNow = weatherVo.getAirNow();
        WeatherVo.NowBean now = weatherVo.getNow();

        if (lifestyleVos == null)
            return;
        suggestion_linearlayout.removeAllViews();
        for (WeatherVo.LifestyleBean lifestyleBase : lifestyleVos) {
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
        for (WeatherVo.DailyForecastBean forecastBase : weatherForecasts) {
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

        context.tv_title.setText(weatherVo.getBasic().getLocation());
        String timeStr=weatherVo.getUpdate().getLoc().split(" ")[1];
        context.tv_time.setText(timeStr);

        //设置24小时天气数据
        List<WeatherVo.HourlyBean> hourlyBeanList= weatherVo.getHourly();
        intiLineChartView(hourlyBeanList);
    }


    private WeatherVo ResponseToWeatherVo(Response<ResponseBody> response,int cityType) throws IOException, JSONException {
        if (response.code() != 200 ) {
            return new WeatherVo();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            WeatherVo weatherVo = BaseApplication.getGson().fromJson(weatherObject.toString(), WeatherVo.class);
            weatherVo.setUpdateTime(DateUtil.getNow());
            weatherVo.setCityType(cityType);
            return weatherVo;
        }
        return new WeatherVo();
    }

    private AirNow ResponseToAirNow(Response<ResponseBody> response) throws IOException, JSONException {
        if (response.code() != 200 ) {
            return new AirNow();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            AirNow airNow = BaseApplication.getGson().fromJson(weatherObject.getJSONObject(WeatherUtil.air_now_city).toString(), AirNow.class);
            return airNow;
        }
        return new AirNow();
    }

    @Override
    public boolean excueOnKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
