package com.gddst.lhy.weather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.com.sky.downloader.greendao.CityVoDao;
import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.base.BaseFragment;
import com.gddst.app.lib_common.commonAdapter.adapterView.MyLinearLayoutManager;
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter;
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.utils.DateUtil;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.lhy.weather.R;
import com.gddst.lhy.weather.WeatherActivity;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.gddst.lhy.weather.vo.WeatherVo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CityListFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_city_back;
    private TextView tv_city_name;
    private TextView tv_city_edit;
    private RecyclerView city_recycler_list;
    private FloatingActionButton btn_city_add;

    private List<WeatherVo> weatherVoList;
    private CommonRecycleViewAdapter<WeatherVo> cityListAdapter;
    private WeatherActivity context;

    private boolean isEdit=false;     //是否是编辑模式
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.fragment_city_list_layout,container,false);
    }

    @Override
    protected void initView(View view) {
        tv_city_back=view.findViewById(R.id.tv_city_back);
        tv_city_name=view.findViewById(R.id.tv_city_name);
        tv_city_edit=view.findViewById(R.id.tv_city_edit);
        city_recycler_list=view.findViewById(R.id.city_recycler_list);
        btn_city_add=view.findViewById(R.id.btn_city_add);

    }

    @Override
    protected void initListener() {
        tv_city_back.setOnClickListener(this);
        tv_city_edit.setOnClickListener(this);
        btn_city_add.setOnClickListener(this);
    }

    @Override
    protected void lazyLoad() {
        context= (WeatherActivity) getActivity();
        weatherVoList=new ArrayList<>();
        intiRecycleList();
    }

    /**
     * 刷新数据
     */
    @Override
    protected void refreshLoad() {
        super.refreshLoad();
    }

    private void intiRecycleList() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, List<WeatherVo>>() {
                    @Override
                    public List<WeatherVo> apply(Integer integer) throws Exception {
                        return getWeatherVoList();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<List<WeatherVo>>() {
                    @Override
                    public void onResponse(List<WeatherVo> weatherVos) throws IOException {
                        setListData();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }

    private void setListData() {
        city_recycler_list.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        cityListAdapter=new CommonRecycleViewAdapter<WeatherVo>(context,R.layout.item_city_recycle,weatherVoList) {
            @Override
            protected void convert(ViewHolder viewHolder, final WeatherVo weatherVo, final int position) {
                final WeatherVo.BasicBean basicBean=weatherVo.getBasic();
                WeatherVo.NowBean nowBean=weatherVo.getNow();

                if (isEdit){
                    //定位城市不显示删除按钮
                    if (weatherVo.getCityType()!=1){
                        viewHolder.setVisible(R.id.im_city_delete, View.VISIBLE);
                    }

                    viewHolder.setVisible(R.id.tv_weather_oath, View.GONE);
                    viewHolder.setVisible(R.id.tv_weather_max_min_num, View.GONE);
                    viewHolder.setVisible(R.id.im_weather_icon, View.GONE);
                    viewHolder.setVisible(R.id.tv_weather_num, View.GONE);

                }else {
                    viewHolder.setVisible(R.id.im_city_delete, View.GONE);

                    viewHolder.setVisible(R.id.tv_weather_oath, View.VISIBLE);
                    viewHolder.setVisible(R.id.tv_weather_max_min_num, View.VISIBLE);
                    viewHolder.setVisible(R.id.im_weather_icon, View.VISIBLE);
                    viewHolder.setVisible(R.id.tv_weather_num, View.VISIBLE);
                }

                viewHolder.setText(R.id.tv_city_detailed,basicBean.getLocation());
                if (weatherVo.getCityType()==1){
                    viewHolder.setVisible(R.id.im_loaction_icon,View.VISIBLE);
                }else {
                    viewHolder.setVisible(R.id.im_loaction_icon,View.GONE);
                }
                /**
                 * 判断城市是否属于直辖市、省会城市
                 */
                //县与市名字一样
                if (basicBean.getParent_city().equals(basicBean.getLocation())){
                    if (basicBean.getLocation().equals(basicBean.getAdmin_area())){      //县与省名字一样
                        //属于直辖市  如北京市 直接显示国家信息
                        viewHolder.setText(R.id.tv_province,basicBean.getCnty());
                    }else {     //县与省名字不一样
                        //属于地级城市 如赣州市 直接显示省份信息
                        viewHolder.setText(R.id.tv_province,basicBean.getAdmin_area());
                    }
                }else {          //县与市名字一样
                    if (basicBean.getParent_city().equals(basicBean.getAdmin_area())){      //市与省名字一样
                        //属于县级城市 如 重庆-重庆-重庆-中国
                        viewHolder.setText(R.id.tv_province,basicBean.getLocation()+"-"+basicBean.getCnty());
                    }
                    else {        //市与省名字不一样
                        //属于县级城市 如 赣州-赣州-江西-中国
                        viewHolder.setText(R.id.tv_province,basicBean.getParent_city()+"-"+basicBean.getAdmin_area());
                    }
                }
                //当前温度
                viewHolder.setText(R.id.tv_weather_num,nowBean.getFl()+"℃");
                //实况天气图标
                viewHolder.setBackgroundRes(
                        R.id.im_weather_icon,
                        getResources().getIdentifier(
                        WeatherUtil.weather + weatherVo.getNow().getCond_code(),
                        WeatherUtil.drawable,
                        context.getPackageName()
                        )
                );

                //温度、方向
                viewHolder.setText(R.id.tv_weather_oath,
                        "湿度"+nowBean.getHum()+"%"+"|"
                        +nowBean.getWind_dir()+nowBean.getWind_sc()+"级"+"|"
                        +"能见度"+nowBean.getVis()+"公里"
                );

                //最低最高气温
                List<WeatherVo.DailyForecastBean> dailyForecastBeanList=weatherVo.getDaily_forecast();
                for (WeatherVo.DailyForecastBean dailyForecastBean:dailyForecastBeanList){
                    if (dailyForecastBean.getDate().equals(DateUtil.currDay())){
                        viewHolder.setText(R.id.tv_weather_max_min_num,
                                dailyForecastBean.getTmp_max()+"/"
                                        +dailyForecastBean.getTmp_min()+"℃"
                        );
                        break;
                    }
                }

                //删除城市
                viewHolder.setOnClickListener(R.id.im_city_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (weatherVo.getCityType()!=1){
                            CityVoDao cityVoDao=BaseApplication.getIns().getDaoSession().getCityVoDao();
                            List<CityVo> cityVoList=cityVoDao.queryBuilder()
                                    .where(CityVoDao.Properties.Cid.eq(basicBean.getCid())).list();
                            if (cityVoList.size()==1){
                                cityVoDao.delete(cityVoList.get(0));
                                weatherVoList.remove(position);
                                notifyItemChanged(position);
                                EventBus.getDefault().post(weatherVo);
                            }
                        }
                    }
                });
            }
        };
        city_recycler_list.setAdapter(cityListAdapter);
    }

    private List<WeatherVo> getWeatherVoList() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(BaseApplication.getIns());
        List<CityVo> cityVos= BaseApplication.getIns().getDaoSession().getCityVoDao().queryBuilder()
                .orderAsc(CityVoDao.Properties.CityType)
                .orderAsc(CityVoDao.Properties.Id)
                .list();
        for(CityVo cityVo:cityVos){
            String weatherStr=sharedPreferences.getString(cityVo.getCid(),"");
            if (TextUtils.isEmpty(weatherStr)){
                weatherVoList.add(new WeatherVo());
            }else {
                weatherVoList.add(BaseApplication.getGson().fromJson(weatherStr,WeatherVo.class));
            }
        }
        return weatherVoList;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //返回
        if (id == R.id.tv_city_back) {
            FragmentTransaction fragmentTransaction=context.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        }
        //编辑
        else if (id== R.id.tv_city_edit){
            if (isEdit){
                isEdit=false;
            }else {
                isEdit=true;
            }
            cityListAdapter.notifyDataSetChanged();
        }

    }
}
