package com.gddst.lhy.weather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gddst.app.lib_common.base.fragment.BackFragment;
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter;
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.recyclerView.CommonItemDecoration;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.util.Keys;
import com.gddst.lhy.weather.R;
import com.gddst.lhy.weather.util.JsonUtils;
import com.gddst.lhy.weather.util.WeatherUtil;

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
    private CommonRecycleViewAdapter<CityVo> cityHostAdapter;
    private List<CityVo> cityHostList=new ArrayList<>();
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_search_layout,container,false);
    }

    @Override
    protected void initView(View view) {
        searchView=view.findViewById(R.id.searchView);
        city_recycler_recyclerView=view.findViewById(R.id.city_recycler_recyclerView);
        city_recycler_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        city_recycler_recyclerView.addItemDecoration(new CommonItemDecoration(30,40,15,30,15,30));
    }

    @Override
    protected void initListener() {

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
                viewHolder.setText(R.id.item_text,cityVo.getLocation());
                viewHolder.setBackgroundRes(R.id.item_text,R.drawable.city_search_host_list_styste);
            }
        };
        city_recycler_recyclerView.setAdapter(cityHostAdapter);

        NetManager.INSTANCE.getShopClient()
                .getHostCity(Keys.key,WeatherUtil.world,WeatherUtil.city_host_num,WeatherUtil.cn)
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
                        cityHostList.addAll(cityVos);
                        if (cityHostAdapter!=null){
                            cityHostAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
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
