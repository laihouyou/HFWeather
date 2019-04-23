package com.gddst.lhy.weather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.base.fragment.BackFragment;
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter;
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.lhy.weather.R;
import com.gddst.lhy.weather.util.JsonUtils;
import com.gddst.lhy.weather.util.WeatherUtil;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CitySearchFragment extends BackFragment {
    private SearchView searchView;
    private RecyclerView city_recycler_recyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private CommonRecycleViewAdapter<CityVo> cityHostAdapter;
    private List<CityVo> cityHostList=new ArrayList<>();
    private boolean isGridLayoutManager=true;
    private RecyclerView.ItemDecoration gridItemDecoration;
    private RecyclerView.ItemDecoration listItemDecoration;
    private RecyclerView.LayoutManager layoutManager;
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
    }

    private void setGridRecyclerView() {
        isGridLayoutManager=true;
        layoutManager=new GridLayoutManager(getActivity(),3);
        city_recycler_recyclerView.setLayoutManager(layoutManager);
    }

    private void setListRecyclerView() {
        isGridLayoutManager=false;
        layoutManager=new LinearLayoutManager(getActivity());
        city_recycler_recyclerView.setLayoutManager(layoutManager);
        //列表需要移除头布局
        lRecyclerViewAdapter.removeHeaderView();
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
                if (!newText.equals("")){
                    //搜索城市，显示列表
                    getCitySearchList(newText);
                }else {
                    //显示热门城市，显示网格
                    getCityHostList();
                }
                return false;
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
                .getCityId(Keys.key,cityName)
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
        setGridRecyclerView();
        cityHostAdapter=new CommonRecycleViewAdapter<CityVo>(
                getActivity(),
                R.layout.item_text,
                cityHostList
        ) {
            @Override
            protected void convert(ViewHolder viewHolder, CityVo cityVo, int position) {
                if (isGridLayoutManager){
                    viewHolder.setText(R.id.item_text,cityVo.getLocation());
                    viewHolder.setBackgroundRes(R.id.item_text,R.drawable.city_search_host_list_styste);
                }else {
                    viewHolder.setText(R.id.item_text,cityVo.getLocation());
                    viewHolder.setBackgroundColor(R.id.item_text,R.color.white);

                }
            }
        };
        lRecyclerViewAdapter=new LRecyclerViewAdapter(cityHostAdapter);
        city_recycler_recyclerView.setAdapter(lRecyclerViewAdapter);

        requestHostCityList();
    }

    private void addFoltView() {
        TextView textView=new TextView(getActivity());
        textView.setText("热门城市");
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0,20,0,20);
        lRecyclerViewAdapter.addHeaderView(textView);
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
        cityHostList.clear();
        cityHostList.addAll(cityVos);
        setGridRecyclerView();
        city_recycler_recyclerView.setAdapter(lRecyclerViewAdapter);//必须重新setAdapter
        addFoltView();
        notifyChanged();
    }

    private void setSearchCityData(List<CityVo> cityVos) {
        cityHostList.clear();
        cityHostList.addAll(cityVos);
        setListRecyclerView();
        notifyChanged();
    }

    private void notifyChanged() {
        if (cityHostAdapter!=null){
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
