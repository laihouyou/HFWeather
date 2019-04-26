package com.gddst.lhy.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.com.sky.downloader.greendao.CityVoDao;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.base.fragment.IFragmentKeyDownHandled;
import com.gddst.app.lib_common.location.LocationInfoExt;
import com.gddst.app.lib_common.location.LocationIntentService;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.utils.DateUtil;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.app.lib_common.widgets.MySwipeRefreshLayout;
import com.gddst.app.rxpermissions.RxPermissionsUtil;
import com.gddst.lhy.weather.fragment.CityListFragment;
import com.gddst.lhy.weather.fragment.CitySearchFragment;
import com.gddst.lhy.weather.fragment.CommonPageAdapter;
import com.gddst.lhy.weather.fragment.WeatherFragment;
import com.gddst.lhy.weather.fragment.dummy.DummyContent;
import com.gddst.lhy.weather.util.JsonUtils;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.gddst.lhy.weather.vo.WeatherVo;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Route(path = "/lhy/weather")
public class WeatherActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
//    public DrawerLayout drawerLayout;
    public MySwipeRefreshLayout swipeRefres;
    public ImageView im_pic;
    public TextView tv_title;
    public TextView tv_time;
    public ImageView title_image;
    public LinearLayout linelayout_indicator;

    public boolean isLocationValue=false;
    public FragmentTransaction fragmentTransaction;

    private ViewPager weatherViewPager;
    private CommonPageAdapter weatherAdapter;
    private List<Fragment> weatherFragmentList;
    private WeatherFragment newWeatherFragment;
    private List<CityVo> cityVoAllList;
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
//        drawerLayout = findViewById(R.id.drawerLayout);
        weatherViewPager=findViewById(R.id.viewpager_weather);
        weatherViewPager.addOnPageChangeListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        title_image = findViewById(R.id.title_image);
        title_image.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        linelayout_indicator = findViewById(R.id.linelayout_indicator);

        swipeRefres = findViewById(R.id.swipeRefres);
        swipeRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newWeatherFragment.requestWeather(cityVo.getCid(),cityVo.getCityType());
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
        weatherAdapter=new CommonPageAdapter(getSupportFragmentManager());
        weatherViewPager.setAdapter(weatherAdapter);
        cityVoAllList= new ArrayList<>();
        updatList();
    }

    private void updatList(){
        //清空数据源，将指示器重置为0
        cityVoAllList.clear();
        weatherFragmentList.clear();
        linelayout_indicator.removeAllViews();
        enabledNum=0;

        cityVoAllList.addAll(BaseApplication.getIns().getDaoSession()
                .getCityVoDao().queryBuilder()
                .orderAsc(CityVoDao.Properties.CityType)
                .orderAsc(CityVoDao.Properties.Id).list());
        int currentItem=0;
        for (int i = 0; i < cityVoAllList.size(); i++) {
            WeatherFragment weatherFragment=WeatherFragment.getFragment(cityVoAllList.get(i).getCid());
            weatherFragmentList.add(weatherFragment);

            CityVo cityVo=cityVoAllList.get(i);
            if (cityVo.getCityType()==WeatherUtil.city_location){
                currentItem=i;
            }

            createImage(i==0,cityVoAllList.size()>1&&i==0);

        }
        weatherAdapter.updatePage(weatherFragmentList);
        weatherViewPager.setCurrentItem(currentItem);
        if (cityVoAllList.size()>0){
            showCityName(cityVoAllList.get(currentItem).getCid());
            newWeatherFragment= (WeatherFragment) weatherFragmentList.get(currentItem);
        }else {
            //说明没有城市信息，需要
            showLocationBefore();
        }

        //设置当只有一个城市的时候不显示指示器
        if (cityVoAllList.size()==1){
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

        if (linelayout_indicator.getChildCount()>1){
            linelayout_indicator.setVisibility(View.VISIBLE);
        }else {
            linelayout_indicator.setVisibility(View.GONE);
        }
    }

    private void showCityName(String cityId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherVoString = sharedPreferences.getString(cityId, "");
        Gson gson=new Gson();
        WeatherVo weatherVo=gson.fromJson(weatherVoString,WeatherVo.class);
        if (weatherVo!=null){
            WeatherVo.BasicBean basicBean=weatherVo.getBasic();
            if (basicBean!=null){
                String location=basicBean.getLocation();
                tv_title.setText(location==null?"":location);
            }
            WeatherVo.UpdateBean updateBean=weatherVo.getUpdate();
            if (updateBean!=null){
                String loc=updateBean.getLoc();
                if (loc!=null){
                    String timeStr=loc.split(" ")[1];
                    tv_time.setText(timeStr);
                }
            }
        }
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
//        if (v.getId() == R.id.title_image) {
//            ProvinceCityFragment fragment = new ProvinceCityFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fragment, fragment);
//            fragmentTransaction.commit();
//            drawerLayout.openDrawer(GravityCompat.START);
//        }
        if (v.getId()==R.id.title_image){
            CityListFragment cityListFragment=new CityListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //添加加载动画
            fragmentTransaction.setCustomAnimations(R.animator.city_list_add,R.animator.city_list_detele);
            fragmentTransaction.replace(R.id.cityManagementFarmeLayout, cityListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("tag",position+"");
        if (cityVoAllList.size()>position){
            cityVo=cityVoAllList.get(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (cityVoAllList.size()>position){
            cityVo=cityVoAllList.get(position);
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
            newWeatherFragment= (WeatherFragment) weatherFragmentList.get(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class WeatherAdapter extends FragmentStatePagerAdapter {

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

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
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
                getCityId(location, WeatherUtil.city_location);

                //动态添加指示器
                createImage(true,false);
            }
        }
        else if (event instanceof DummyContent.DummyItem){
            DummyContent.DummyItem item= (DummyContent.DummyItem) event;
            String cityName=item.name;

//            if (drawerLayout!=null){
//                drawerLayout.closeDrawers();
//            }
            //这里需要通过城市名字去拿城市id
            getCityId(cityName,WeatherUtil.city_select);

            //动态添加指示器
            createImage(false,false);
        }
        else if (event instanceof CityVo ){
            //从搜索页面跳回主页面
            searchToActivity((CityVo) event);
        }
        //删除城市成功刷新viewpage数据
        else if (event instanceof WeatherVo){
            WeatherVo weatherVo= (WeatherVo) event;
            updateViewPathe(weatherVo);
        }
        //其他
        else if (event instanceof String){
            String action= (String) event;
            //从城市管理页面回到主页面
            if (action.equals(WeatherUtil.back_action)){

            }
            else if (action.equals(WeatherUtil.delete_action)){
                //插入成功后查询数据库并将数据源发送出去
                cityListUpdate();
            }
        }
    }

    private void cityListUpdate() {
        List<CityVo> cityVos = BaseApplication.getIns().getDaoSession().getCityVoDao()
                .queryBuilder().list();
        cityVoAllList.clear();
        cityVoAllList.addAll(cityVos);
    }

    private void searchToActivity(CityVo event) {
        Observable.just(event)
                .doOnNext(new Consumer<CityVo>() {  //清除fragment返回栈
                    @Override
                    public void accept(CityVo cityVo) throws Exception {
                        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        List<Fragment> fragmentList= getSupportFragmentManager()
                                .getFragments();
                        if (fragmentList.size()>0){
                            for (int i = 0; i < fragmentList.size(); i++) {
                                Fragment fragment=  fragmentList.get(i);
                                if (fragment instanceof CityListFragment || fragment instanceof CitySearchFragment){
                                    fragmentTransaction.remove(fragment);
                                }
                            }
                        }
                        fragmentTransaction.commit();
                    }
                })
                .doOnNext(new Consumer<CityVo>() {  //数据处理
                    @Override
                    public void accept(CityVo cityVo) throws Exception {
                        List<CityVo> cityVos = BaseApplication.getIns().getDaoSession().getCityVoDao().queryBuilder()
                                .where(CityVoDao.Properties.Cid.eq(cityVo.getCid())).list();
                        if (cityVos.size()==0){
                            cityVo.setCityType(WeatherUtil.city_select);
                            cityVo.setAddCityTime(DateUtil.getNow());
                            BaseApplication.getIns().getDaoSession().getCityVoDao().insert(cityVo);
                            cityListUpdate();

                            //动态添加指示器
                            createImage(false,false);
                            //添加天气页面
                            addWertherFragment(cityVo.getCid(),WeatherUtil.city_select);
                        }else {
                            //数据库存在
                            //viewpage跳转到所选择的城市页面
                            CityVo cityVoSelected= cityVo;
                            for (int i = 0; i < cityVoAllList.size(); i++) {
                                if (cityVoSelected.getCid().equals(cityVoAllList.get(i).getCid())){
                                    weatherViewPager.setCurrentItem(i);
                                    break;
                                }
                            }
                        }
                    }
                }).subscribe(new DlObserve<CityVo>() {
            @Override
            public void onResponse(CityVo cityVo) throws IOException {

            }

            @Override
            public void onError(int errorCode, String errorMsg) {

            }
        });



    }

    private void updateViewPathe(WeatherVo weatherVo) {
        String cid=weatherVo.getBasic().getCid();
        for (int i=0;i<weatherFragmentList.size();i++){
            WeatherFragment weatherFragment= (WeatherFragment) weatherFragmentList.get(i);
            if (weatherFragment.getArguments()!=null){
                String fragmentCid=weatherFragment.getArguments().getString(WeatherUtil.cid);
                if (cid.equals(fragmentCid)){
                    weatherFragmentList.remove(weatherFragment);
                    updatList();
//                    //指示器删除对应
//                    linelayout_indicator.removeViewAt(i);
//                    //设置当只有一个城市的时候不显示指示器
//                    if (weatherFragmentList.size()==1){
//                        linelayout_indicator.setVisibility(View.GONE);
//                    }else {
//                        linelayout_indicator.setVisibility(View.VISIBLE);
//                    }
//                    //刷新数据源
//                    weatherAdapter.notifyDataSetChanged();
//                    //每次删除成功后设置第一个城市显示
//                    weatherViewPager.setCurrentItem(0);
                    break;
                }
            }
        }
    }

    private void getCityId(final String cityName, final int cityType) {
        NetManager.INSTANCE.getShopClient()
                .getCityId(Keys.key,WeatherUtil.world,WeatherUtil.city_host_num,cityName)
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
                        addWertherFragment(s, cityType);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }

    private void addWertherFragment(String cityId, int cityType) {
        WeatherFragment weatherFragment=WeatherFragment.getFragment(cityId);
        weatherAdapter.addPage(weatherFragment);
        weatherViewPager.setCurrentItem(weatherAdapter.getCount()-1);

        weatherFragment.requestWeather(cityId,cityType);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount() > 0 && fm.getFragments().get(fm.getFragments().size()-1) instanceof IFragmentKeyDownHandled){
            if(((IFragmentKeyDownHandled) fm.getFragments().get(fm.getFragments().size()-1)).excueOnKeyDown(keyCode, event)){
                return true;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }
        else{
            return super.onKeyDown(keyCode, event);
        }
    }

}
