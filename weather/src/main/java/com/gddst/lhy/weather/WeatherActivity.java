package com.gddst.lhy.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.gddst.lhy.weather.fragment.ProvinceCityFragment;
import com.gddst.lhy.weather.fragment.WeatherFragment;
import com.gddst.lhy.weather.fragment.dummy.DummyContent;
import com.gddst.lhy.weather.util.JsonUtils;
import com.gddst.lhy.weather.vo.WeatherVo;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class WeatherActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    public DrawerLayout drawerLayout;
    public ImageView im_pic;
    public TextView tv_title;
    public TextView tv_time;
    public ImageView title_image;
    public LinearLayout linelayout_indicator;

    public boolean isLocationValue=false;

    private ViewPager weatherViewPager;
    private WeatherAdapter weatherAdapter;
    private List<WeatherFragment> weatherFragmentList;
    private List<CityVo> cityVoList;
    private int enabledNum=0;   //指示灯当前显示位置
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

        im_pic = findViewById(R.id.im_pic);
        drawerLayout = findViewById(R.id.drawerLayout);
        weatherViewPager=findViewById(R.id.viewpager_weather);
        weatherViewPager.addOnPageChangeListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_time = findViewById(R.id.tv_time);
        title_image = findViewById(R.id.title_image);
        title_image.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        linelayout_indicator = findViewById(R.id.linelayout_indicator);
    }

    @Override
    protected void initData() {
        requestPermission();
        startLocationService();
        initWeatherViewPager();
    }

    private void initWeatherViewPager() {
        weatherFragmentList=new ArrayList<>();
        weatherAdapter=new WeatherAdapter(getSupportFragmentManager());
        weatherViewPager.setAdapter(weatherAdapter);
        cityVoList= BaseApplication.getIns().getDaoSession()
                .getCityVoDao().queryBuilder().orderAsc(CityVoDao.Properties.Id).list();
        int currentItem=0;
        for (int i = 0; i < cityVoList.size(); i++) {
            WeatherFragment weatherFragment=WeatherFragment.getFragment(cityVoList.get(i).getCid());
            weatherFragmentList.add(weatherFragment);

            //创建指示器
            ImageView imageView=new ImageView(this);
            imageView.setBackgroundResource(R.drawable.background);
            imageView.setEnabled(false);

            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(15,15);
            if (i!=0){
                layoutParams.leftMargin=10;
            }
            linelayout_indicator.addView(imageView,layoutParams);

            CityVo cityVo=cityVoList.get(i);
            if (cityVo.getIsLocationCity()){
                currentItem=i;
            }

        }
        weatherAdapter.notifyDataSetChanged();
        weatherViewPager.setCurrentItem(currentItem);
        showCityName(cityVoList.get(currentItem).getCid());

        //设置当只有一个城市的时候不显示指示器
        if (cityVoList.size()==1){
            linelayout_indicator.setVisibility(View.GONE);
        }else {
            linelayout_indicator.setVisibility(View.VISIBLE);
        }
    }

    private void showCityName(String cityId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherVoString = sharedPreferences.getString(cityId, "");
        Gson gson=new Gson();
        WeatherVo weatherVo=gson.fromJson(weatherVoString,WeatherVo.class);

        tv_title.setText(weatherVo.getBasic().getLocation());
        String timeStr=weatherVo.getUpdate().getLoc().split(" ")[1];
        tv_time.setText(timeStr);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (cityVoList.size()>position){
            CityVo cityVo=cityVoList.get(position);
            showCityName(cityVo.getCid());
        }

        if (linelayout_indicator.getChildCount()>position){
            linelayout_indicator.getChildAt(enabledNum).setEnabled(false);
            linelayout_indicator.getChildAt(position).setEnabled(true);
            enabledNum=position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

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

            if (weatherFragmentList.size()==0){
                //这里需要通过坐标信息去拿城市id
                getCityId(location,true);
            }
        }
        else if (event instanceof DummyContent.DummyItem){
            DummyContent.DummyItem item= (DummyContent.DummyItem) event;
            String cityName=item.name;

            if (drawerLayout!=null){
                drawerLayout.closeDrawers();
            }
            //这里需要通过城市名字去拿城市id
            getCityId(cityName,false);
        }
        else if (event instanceof CityVo ){
            //插入成功后查询数据库并将数据源发送出去
            List<CityVo> cityVos=BaseApplication.getIns().getDaoSession().getCityVoDao()
                    .queryBuilder().list();
            cityVoList.clear();
            cityVoList.addAll(cityVos);
        }
    }

    private void getCityId(final String cityName, final boolean isLocationCity) {
        final WeatherFragment[] weatherFragment = new WeatherFragment[1];
        NetManager.INSTANCE.getShopClient()
                .getCityId(Keys.key,cityName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, String>() {
                    @Override
                    public String apply(Response<ResponseBody> response) throws Exception {
                        return JsonUtils.getCityId(response);
                    }
                })
                .flatMap(new Function<String, ObservableSource<WeatherVo>>() {
                    @Override
                    public ObservableSource<WeatherVo> apply(String s) throws Exception {
                        weatherFragment[0] =WeatherFragment.getFragment(s);
                        return weatherFragment[0].getWeatherObservable(s,isLocationCity);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<WeatherVo>() {
                    @Override
                    public void onResponse(WeatherVo weatherVo) throws IOException {
//                        WeatherFragment weatherFragment=WeatherFragment.getFragment(s);
//                        weatherFragmentList.add(weatherFragment);
//                        weatherAdapter.notifyDataSetChanged();

                        weatherFragmentList.add(weatherFragment[0]);
                        weatherAdapter.notifyDataSetChanged();
                        weatherViewPager.setCurrentItem(weatherFragmentList.indexOf(weatherFragment[0]));
                        weatherFragment[0].requestWeather(weatherVo.getBasic().getCid(),weatherVo.isLocationCity());
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }
}
