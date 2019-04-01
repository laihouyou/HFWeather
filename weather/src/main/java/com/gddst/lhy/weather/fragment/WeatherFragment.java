package com.gddst.lhy.weather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.sky.downloader.greendao.CityVoDao;
import com.gddst.app.lib_common.base.BaseApplication;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class WeatherFragment extends Fragment implements View.OnClickListener{
    private SwipeRefreshLayout swipeRefres;
    //空气质量aqi
    private TextView aqi_text;
    private TextView pm25_text;
    //标题
//    private TextView tv_title;
//    private TextView tv_time;
//    private ImageView title_image;
    //当日天气
    private TextView tv_Celsius;
    private TextView tv_situation;

    private ScrollView scrollView;

    private LinearLayout day_linelayout;
    private LinearLayout suggestion_linearlayout;

    private WeatherVo newWeatherVo;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            cityCid=getArguments().getString(WeatherUtil.cid);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context= (WeatherActivity) getActivity();
        View view=getLayoutInflater().inflate(R.layout.fragment_weather_layout,container,false);
        aqi_text = view.findViewById(R.id.aqi_text);
        pm25_text = view.findViewById(R.id.pm25_text);

//        tv_title = view.findViewById(R.id.tv_title);
//        tv_time = view.findViewById(R.id.tv_time);
//        title_image = view.findViewById(R.id.title_image);
//        title_image.setOnClickListener(this);

        tv_Celsius = view.findViewById(R.id.tv_Celsius);
        tv_situation = view.findViewById(R.id.tv_situation);

        swipeRefres = view.findViewById(R.id.swipeRefres);
        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(newWeatherVo.getBasic().getCid(),newWeatherVo.isLocationCity());
                getPicImage();
                Log.i("tag","天气请求刷新++++++++++++++++++++++++++");
            }
        });

        scrollView = view.findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefres.setEnabled(scrollView.getScrollY() == 0);
            }
        });

        day_linelayout = view.findViewById(R.id.day_linelayout);
        suggestion_linearlayout = view.findViewById(R.id.suggestion_linearlayout);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newWeatherVo=new WeatherVo();
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
            requestWeather(weatherVo.getBasic().getCid(),weatherVo.isLocationCity());
        }else {
            showText(weatherVo);
            //如果是数据过期，需要在这里设置一下城市名称
            context.tv_title.setText(weatherVo.getBasic().getLocation());
            String timeStr = weatherVo.getUpdate().getLoc().split(" ")[1];
            context.tv_time.setText(timeStr);
        }
    }

    private void showLocationBefore() {
        context.isLocationValue=false;
        swipeRefres.setRefreshing(true);
        context.tv_title.setText("正在获取当前所在城市");
        tv_Celsius.setText("暂无数据");
        tv_situation.setText("暂无数据");
    }

    private void showTimeOutBefore() {
        swipeRefres.setRefreshing(true);
        context.tv_title.setText("数据已过期正在更新");
    }

    private void showPicImage(String picUrl) {
        Glide.with(context).load(picUrl).into(context.im_pic);
    }

    public  Observable getWeatherObservable(String cityCid, final boolean isLocationCity){
        return  Observable.just(cityCid)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<WeatherVo>>() {
                    @Override
                    public ObservableSource<WeatherVo> apply(String weatherCode) throws Exception {
                        return getZip(
                                getobservableNow(weatherCode,isLocationCity),
                                getobservableAirNow(weatherCode),
                                isLocationCity
                        );
                    }
                });
    }

    public void requestWeather(String cityCid, final boolean isLocationCity) {
       getWeatherObservable(cityCid,isLocationCity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<WeatherVo>() {
                    @Override
                    public void onResponse(WeatherVo weatherVo) throws IOException {
                        showText(weatherVo);
                        swipeRefres.setRefreshing(false);
                        Toast.makeText(context, weatherVo.getStatus(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                        swipeRefres.setRefreshing(false);
                    }
                });

    }

    private Observable getZip(Observable observableNow, Observable observableAirNow, final boolean isLocationCity){
        return Observable.zip(observableNow, observableAirNow, new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) throws Exception {
                if (o instanceof WeatherVo&&o2 instanceof AirNow){
                    WeatherVo weatherVo= (WeatherVo) o;
                    AirNow airNow= (AirNow) o2;
                    weatherVo.setAirNow(airNow);
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(BaseApplication.getIns()).edit();
                    editor.putString(weatherVo.getBasic().getCid(), BaseApplication.getGson().toJson(weatherVo));
                    editor.apply();

                    //保存城市信息
                    CityVo cityVo=new CityVo();
                    cityVo.setCid(weatherVo.getBasic().getCid());
                    cityVo.setLocation(weatherVo.getBasic().getLocation());
                    cityVo.setAdmin_area(weatherVo.getBasic().getAdmin_area());
                    cityVo.setCnty(weatherVo.getBasic().getCnty());
                    cityVo.setLat(weatherVo.getBasic().getLat());
                    cityVo.setLon(weatherVo.getBasic().getLon());
                    cityVo.setParent_city(weatherVo.getBasic().getParent_city());
                    cityVo.setTz(weatherVo.getBasic().getTz());
//                    cityVo.setUpdateTime(DateUtil.getNow());
                    cityVo.setIsLocationCity(isLocationCity);
                    List<CityVo> cityVoList= BaseApplication.getIns().getDaoSession().getCityVoDao()
                            .queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVo.getCid())).list();
                    if (cityVoList.size()==0){
                        BaseApplication.getIns().getDaoSession().getCityVoDao().insertOrReplace(cityVo);
                        //数据插入成功发送
                        EventBus.getDefault().post(cityVo);
                    }
                    return weatherVo;
                }
                return new WeatherVo();
            }
        });

    }

    private Observable getobservableNow(String weatherId, final boolean isLocationCity){
        return NetManager.INSTANCE.getShopClient()
                .getWeatherNow(Keys.key, weatherId)
                .map(new Function<Response<ResponseBody>, WeatherVo>() {
                    @Override
                    public WeatherVo apply(Response<ResponseBody> response) throws Exception {
                        return ResponseToWeatherVo(response,isLocationCity);
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
        newWeatherVo=weatherVo;
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

//        context.tv_title.setText(weatherVo.getBasic().getLocation());
//        String timeStr=weatherVo.getUpdate().getLoc().split(" ")[1];
//        context.tv_time.setText(timeStr);
    }


    private WeatherVo ResponseToWeatherVo(Response<ResponseBody> response,boolean isLocationCity) throws IOException, JSONException {
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
            weatherVo.setLocationCity(isLocationCity);
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
    public void onClick(View v) {

    }


}
