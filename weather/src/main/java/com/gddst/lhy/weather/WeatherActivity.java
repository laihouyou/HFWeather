package com.gddst.lhy.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.gddst.app.lib_common.widgets.MySwipeRefreshLayout;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class WeatherActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    public DrawerLayout drawerLayout;
    public MySwipeRefreshLayout swipeRefres;
    public ImageView im_pic;
    public TextView tv_title;
    public TextView tv_time;
    public ImageView title_image;
    public LinearLayout linelayout_indicator;

    public boolean isLocationValue=false;

    private ViewPager weatherViewPager;
    private WeatherAdapter weatherAdapter;
    private List<WeatherFragment> weatherFragmentList;
    private WeatherFragment newWeatherFragment;
    private List<CityVo> cityVoList;
    private CityVo cityVo;
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

        swipeRefres = findViewById(R.id.swipeRefres);
        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newWeatherFragment.requestWeather(cityVo.getCid(),cityVo.getIsLocationCity());
                newWeatherFragment.getPicImage();
                Log.i("tag","天气请求刷新++++++++++++++++++++++++++");
            }
        });
    }

    @Override
    protected void initData() {
        requestPermission();
        startLocationService();
        initWeatherViewPager();
    }

    private void initWeatherViewPager() {
        cityVo=new CityVo();
        weatherFragmentList=new ArrayList<>();
        weatherAdapter=new WeatherAdapter(getSupportFragmentManager());
        weatherViewPager.setAdapter(weatherAdapter);
        cityVoList= BaseApplication.getIns().getDaoSession()
                .getCityVoDao().queryBuilder().orderAsc(CityVoDao.Properties.Id).list();
        int currentItem=0;
        for (int i = 0; i < cityVoList.size(); i++) {
            WeatherFragment weatherFragment=WeatherFragment.getFragment(cityVoList.get(i).getCid());
            weatherFragmentList.add(weatherFragment);

            CityVo cityVo=cityVoList.get(i);
            if (cityVo.getIsLocationCity()){
                currentItem=i;
            }

            createImage(i==0,cityVoList.size()>1&&i==0);

        }
        weatherAdapter.notifyDataSetChanged();
        weatherViewPager.setCurrentItem(currentItem);
        if (cityVoList.size()>0){
            showCityName(cityVoList.get(currentItem).getCid());
            newWeatherFragment=weatherFragmentList.get(currentItem);
        }else {
            //说明没有城市信息，需要
            showLocationBefore();
        }

        //设置当只有一个城市的时候不显示指示器
        if (cityVoList.size()==1){
            linelayout_indicator.setVisibility(View.GONE);
        }else {
            linelayout_indicator.setVisibility(View.VISIBLE);
        }
    }

    private void showLocationBefore() {
        isLocationValue=false;
        tv_title.setText("正在获取当前所在城市");
        swipeRefres.setRefreshing(true);
    }

    private void createImage(boolean isFirst,boolean isEnabled) {
        //创建指示器
        ImageView imageView=new ImageView(this);

        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(15,15);
        layoutParams.gravity= Gravity.CENTER;
        if (!isFirst){
            layoutParams.leftMargin=10;
            imageView.setBackgroundResource(R.drawable.background);
            imageView.setEnabled(isEnabled);
        }else {
            layoutParams.width=25;
            layoutParams.height= 25;
            if(isEnabled){
                imageView.setBackgroundResource(R.drawable.location_white);
            }else {
                imageView.setBackgroundResource(R.drawable.location_black);
            }
        }
        linelayout_indicator.addView(imageView,layoutParams);
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
        Log.i("tag",position+"");
    }

    @Override
    public void onPageSelected(int position) {
        if (cityVoList.size()>position){
            cityVo=cityVoList.get(position);
            showCityName(cityVo.getCid());
        }

        if (linelayout_indicator.getChildCount()>position){
            linelayout_indicator.getChildAt(enabledNum).setEnabled(false);
            enabledNum=position;
            if (position==0){
                linelayout_indicator.getChildAt(position).setEnabled(true);
                linelayout_indicator.getChildAt(position).setBackgroundResource(R.drawable.location_white);
            }else {
                linelayout_indicator.getChildAt(position).setEnabled(true);
                linelayout_indicator.getChildAt(0).setBackgroundResource(R.drawable.location_black);
            }
        }

        if (weatherFragmentList.size()>position){
            newWeatherFragment=weatherFragmentList.get(position);
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

        /*
         * 重写该方法，取消调用父类该方法
         * 可以避免在viewpager切换，fragment不可见时执行到onDestroyView，可见时又从onCreateView重新加载视图
         * 因为父类的destroyItem方法中会调用detach方法，将fragment与view分离，（detach()->onPause()->onStop()->onDestroyView()）
         * 然后在instantiateItem方法中又调用attach方法，此方法里判断如果fragment与view分离了，
         * 那就重新执行onCreateView，再次将view与fragment绑定（attach()->onCreateView()->onActivityCreated()->onStart()->onResume()）
         * */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
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

            //动态添加指示器
            createImage(false,false);
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
                        WeatherFragment weatherFragment=WeatherFragment.getFragment(s);
                        weatherFragmentList.add(weatherFragment);
                        weatherAdapter.notifyDataSetChanged();
                        weatherViewPager.setCurrentItem(weatherFragmentList.size()-1);

                        weatherFragment.requestWeather(s,isLocationCity);

                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }
}
