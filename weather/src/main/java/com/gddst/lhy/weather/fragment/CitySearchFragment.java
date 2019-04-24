package com.gddst.lhy.weather.fragment;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.com.sky.downloader.greendao.CityVoDao;
import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.base.fragment.BackFragment;
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter;
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.recyclerView.MyLinearLayoutManager;
import com.gddst.app.lib_common.recyclerView.TextHeader;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.app.lib_common.widgets.SignKeyWordTextView;
import com.gddst.lhy.weather.R;
import com.gddst.lhy.weather.util.JsonUtils;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;

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

public class CitySearchFragment extends BackFragment {
    private SearchView searchView;
    //搜索的关键字
    private String searchText;
    private LRecyclerView city_recycler_recyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private CommonRecycleViewAdapter<CityVo> cityHostAdapter;
    private List<CityVo> cityHostList=new ArrayList<>();
    private RecyclerView.ItemDecoration gridItemDecoration;
    private RecyclerView.ItemDecoration listItemDecoration;
    private RecyclerView.LayoutManager layoutManager;

    private LRecyclerView city_search_recycler;
    private LRecyclerViewAdapter search_lRecyclerViewAdapter;
    private CommonRecycleViewAdapter<CityVo> search_cityHostAdapter;
    private List<CityVo> search_cityList=new ArrayList<>();
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_search_layout,container,false);
    }

    @Override
    protected void initView(View view) {
        searchView=view.findViewById(R.id.searchView);
//        //设置搜索框直接展开显示。左侧有无放大镜(在搜索框中) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
//        searchView.onActionViewExpanded();
        //设置输入框提示语
        searchView.setQueryHint("请输入关键字");

        city_recycler_recyclerView=view.findViewById(R.id.city_recycler_recyclerView);

        gridItemDecoration=new GridItemDecoration.Builder(getActivity())
                .setHorizontal(R.dimen.dp_15)
                .setVertical(R.dimen.dp_20)
                .setColorResource(R.color.beige)
                .build();;
        city_recycler_recyclerView.addItemDecoration(gridItemDecoration);

        city_search_recycler=view.findViewById(R.id.city_search_recycler);
        city_search_recycler.setPullRefreshEnabled(false);
//        city_recycler_recyclerView.addItemDecoration(SpacesItemDecoration.newInstance(15,20,layoutManager.getChildCount()));
        setListRecyclerView();
    }

    private void setGridRecyclerView() {
        layoutManager=new GridLayoutManager(getActivity(),3);
        city_recycler_recyclerView.setLayoutManager(layoutManager);
    }

    private void setListRecyclerView() {
        layoutManager=new MyLinearLayoutManager(getActivity());
        city_search_recycler.setLayoutManager(layoutManager);
    }

    @Override
    protected void initListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchText=newText;
                    city_recycler_recyclerView.setVisibility(View.GONE);
                    city_search_recycler.setVisibility(View.VISIBLE);
                    //搜索城市，显示列表
                    getCitySearchList(newText);
                }else {
                    city_recycler_recyclerView.setVisibility(View.VISIBLE);
                    city_search_recycler.setVisibility(View.GONE);
                    //显示热门城市，显示网格
                    getCityHostList();
                }
                return false;
            }
        });

        city_recycler_recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestHostCityList();
            }
        });
    }

    private void getCityHostList() {
        SharedPreferences sharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(BaseApplication.getIns());
        String hostCityListStr=sharedPreferences.getString(WeatherUtil.hostCityListStr,"");
        if (TextUtils.isEmpty(hostCityListStr)){
            requestHostCityList();
        }else {
            List<CityVo> cityVoList=BaseApplication.getGson().fromJson(
                    hostCityListStr,
                    new TypeToken<List<CityVo>>(){}.getType());
            setHostCityData(cityVoList);
        }
    }

    private void getCitySearchList(String cityName) {
        NetManager.INSTANCE.getShopClient()
                .getCityId(Keys.key,WeatherUtil.world_scenic,WeatherUtil.city_search_num,cityName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, List<CityVo>>() {
                    @Override
                    public List<CityVo> apply(Response<ResponseBody> response) throws Exception {
                        return JsonUtils.getCityHostList(response);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<List<CityVo>>() {
                    @Override
                    public void onResponse(List<CityVo> cityVos) throws IOException {
                        setSearchCityData(cityVos);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }

    @Override
    protected void lazyLoad() {

        cityHostAdapter=new CommonRecycleViewAdapter<CityVo>(
                getActivity(),
                R.layout.item_text,
                cityHostList
        ) {
            @Override
            protected void convert(ViewHolder viewHolder, CityVo cityVo, int position) {
                TextView item_text=viewHolder.getView(R.id.item_text);
                item_text.setText(cityVo.getLocation());
                //如果是定位城市，动态设置定位图标
                if (cityVo.getCityType()==WeatherUtil.city_location){
                    Drawable drawableLeft=getResources().getDrawable(R.drawable.city_loaction);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    item_text.setCompoundDrawables(drawableLeft,null,null,null);
                }else {
                    item_text.setCompoundDrawables(null,null,null,null);
                }
                //判断是否选中
                if (cityVo.getIsSelected()){
                    item_text.setTextColor(getResources().getColor(R.color.cornflowerblue12));
                    item_text.setBackgroundResource(R.drawable.city_search_host_list_selected);
                }else {
                    item_text.setTextColor(getResources().getColor(R.color.point_facility11));
                    item_text.setBackgroundResource(R.drawable.city_search_host_list_no_selected);
                }

                viewHolder.setOnClickListener(R.id.item_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        lRecyclerViewAdapter=new LRecyclerViewAdapter(cityHostAdapter);
        city_recycler_recyclerView.setAdapter(lRecyclerViewAdapter);
        //禁止自动加载更多功能
        city_recycler_recyclerView.setLoadMoreEnabled(false);

        city_recycler_recyclerView.refresh();

        search_cityHostAdapter=new CommonRecycleViewAdapter<CityVo>(
                getActivity(),
                R.layout.item_text,
                search_cityList
        ) {
            @Override
            protected void convert(ViewHolder viewHolder, CityVo cityVo, int position) {
                SignKeyWordTextView item_text=viewHolder.getView(R.id.item_text);

                item_text.setTextColor(getResources().getColor(R.color.point_facility11));
                item_text.setBackgroundColor(getResources().getColor(R.color.white));
                //设置高亮关键字
                item_text.setSignText(searchText);
                //设置高亮颜色
                item_text.setSignTextColor(getResources().getColor(R.color.salmon));
                item_text.setText(
                        cityVo.getLocation()+","+cityVo.getParent_city()+","+cityVo.getAdmin_area(),
                        TextView.BufferType.NORMAL);

                viewHolder.setOnClickListener(R.id.item_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        search_lRecyclerViewAdapter=new LRecyclerViewAdapter(search_cityHostAdapter);
        city_search_recycler.setAdapter(search_lRecyclerViewAdapter);
        //禁止自动加载更多功能
        city_search_recycler.setLoadMoreEnabled(false);


    }

    private void addHeaderView() {
        TextHeader textHeader=new TextHeader(getActivity());
        textHeader.setHeaderText("热门城市");
        lRecyclerViewAdapter.addHeaderView(textHeader);
    }

    private void requestHostCityList() {
        NetManager.INSTANCE.getShopClient()
                .getHostCity(Keys.key, WeatherUtil.world,WeatherUtil.city_host_num,WeatherUtil.cn)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, List<CityVo>>() {
                    @Override
                    public List<CityVo> apply(Response<ResponseBody> responseBodyResponse) throws Exception {
                        return JsonUtils.getCityHostList(responseBodyResponse);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<List<CityVo>>() {
                    @Override
                    public void onResponse(List<CityVo> cityVos) throws IOException {
                        //将热门城市数据保存
                        SharedPreferences.Editor sharedPreferences= PreferenceManager
                                .getDefaultSharedPreferences(BaseApplication.getIns()).edit();
                        sharedPreferences.putString(WeatherUtil.hostCityListStr,BaseApplication.getGson().toJson(cityVos));
                        sharedPreferences.apply();

                        setHostCityData(cityVos);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
    }

    private void setHostCityData(List<CityVo> cityVos) {
        Observable.just(cityVos)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<List<CityVo>>() {
                    @Override
                    public void accept(List<CityVo> cityVos) throws Exception {
                        cityHostList.clear();
                        //添加定位城市数据
                        List<CityVo> cityVoLocationList=BaseApplication.getIns().getDaoSession().getCityVoDao().queryBuilder()
                                .where(CityVoDao.Properties.CityType.eq(WeatherUtil.city_location)).list();
                        if (cityVoLocationList.size()==1){
                            cityVoLocationList.get(0).setIsSelected(true);
                            cityHostList.add(cityVoLocationList.get(0));
                        }
                        //对比数据库，数据库已经存在的热门城市改为已选中
                        for (int j = 0; j < cityVos.size(); j++) {
                            CityVo cityVoHost=cityVos.get(j);
                            List<CityVo> cityDbVos=BaseApplication.getIns().getDaoSession()
                                    .getCityVoDao().queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVoHost.getCid())).list();
                            if (cityDbVos.size()==1){
                                CityVo cityVodb=cityDbVos.get(0);
                                cityVoHost.setIsSelected(cityVodb.getIsSelected());
                                cityVoHost.setAddCityTime(cityVodb.getAddCityTime());
                                cityVoHost.setCityType(cityVodb.getCityType());
                                continue;
                            }
                        }

                        //添加热门城市数据
                        cityHostList.addAll(cityVos);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<List<CityVo>>() {
                    @Override
                    public void onResponse(List<CityVo> cityVos) throws IOException {
                        setGridRecyclerView();
                        city_recycler_recyclerView.setAdapter(lRecyclerViewAdapter);//必须重新setAdapter
                        if (lRecyclerViewAdapter.getHeaderViewsCount()==0){
                            addHeaderView();
                        }
                        notifyChanged();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });

    }

    private void setSearchCityData(List<CityVo> cityVos) {
        search_cityList.clear();
        search_cityList.addAll(cityVos);
        notifyChanged_search();
    }

    private void notifyChanged_search() {
        if (search_lRecyclerViewAdapter!=null){
            search_lRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void notifyChanged() {
        if (cityHostAdapter!=null){
            city_recycler_recyclerView.refreshComplete(50);
            cityHostAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected boolean backFragment() {
        back();
        return true;
    }

    private void back() {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        //添加加载动画
        fragmentTransaction.setCustomAnimations(R.animator.city_search_add,R.animator.city_search_detele);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
