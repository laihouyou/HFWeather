package com.gddst.lhy.weather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.com.sky.downloader.greendao.CityDao;
import com.com.sky.downloader.greendao.CountyDao;
import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.net.DlObserve;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.utils.ToastUtils;
import com.gddst.app.lib_common.weather.db.City;
import com.gddst.app.lib_common.weather.db.County;
import com.gddst.app.lib_common.weather.db.Province;
import com.gddst.lhy.weather.R;
import com.gddst.lhy.weather.fragment.dummy.DummyContent;
import com.gddst.lhy.weather.fragment.dummy.DummyContent.DummyItem;
import com.gddst.lhy.weather.util.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ProvinceCityFragment extends Fragment   {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String PROVINCE = "PROVINCE-count";
    private static final String CITY = "CITY-count";
    private static final String COUNTY = "COUNTY-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    //省数据
    private List<Province> provinceList;
    //市数据
    private List<City> cityList;
    //县数据
    private List<County> countyList;

    private Province provinceSelected;
    private City citySelected;

    private ArrayAdapter adapter;

    private TextView textView;
    private TextView textView2;
    private ListView listView;

    private String url="http://guolin.tech/api/china";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProvinceCityFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProvinceCityFragment newInstance(int columnCount) {
        ProvinceCityFragment fragment = new ProvinceCityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provincecity_list, container, false);
        listView=view.findViewById(R.id.list);
        adapter=new ArrayAdapter<DummyContent.DummyItem>(
                getActivity(),
                R.layout.item_text,
                DummyContent.ITEMS);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DummyItem item=DummyContent.ITEMS.get(position);
                if (item.dataType.equals(PROVINCE)){
                    provinceSelected=new Province();
                    provinceSelected.setId(item.id);
                    provinceSelected.setProvinceName(item.name);
                    provinceSelected.setProvinceCode(item.code);
                    setCityData();
                }
                else if (item.dataType.equals(CITY)){
                    citySelected=new City();
                    citySelected.setId(item.id);
                    citySelected.setCityName(item.name);
                    citySelected.setCityCode(item.code);
                    citySelected.setProvinceId(item.fId);
                    setCountyData();
                }
                else if (item.dataType.equals(COUNTY)){
                    EventBus.getDefault().post(item);
                }
            }
        });

        textView=view.findViewById(R.id.textView);
        textView2=view.findViewById(R.id.textView2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (citySelected!=null){
                    setCityData();
                    citySelected=null;
                }else if (provinceSelected!=null){
                    setProvinceData();
                    provinceSelected=null;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setProvinceData();
    }

    private void setProvinceData() {
        textView.setText("国内省份");
        textView2.setVisibility(View.GONE);
        provinceList= BaseApplication.getIns().getDaoSession().getProvinceDao().queryBuilder().list();
        if (provinceList.size()>0){
            DummyContent.ITEMS.clear();
            for(Province province:provinceList){
                DummyContent.addItem(province.getId(),province.getProvinceName(),province.getProvinceCode(), PROVINCE,-1);
            }
            adapter.notifyDataSetChanged();
        }else {
            querFormService(url, PROVINCE);
        }
    }

    private void setCityData(){
        textView2.setVisibility(View.VISIBLE);
        textView.setText(provinceSelected.getProvinceName());
        cityList=BaseApplication.getIns().getDaoSession()
                .queryBuilder(City.class)
                .where(CityDao.Properties.ProvinceId.eq(provinceSelected.getProvinceCode())).list();
        if (cityList.size()>0){
            DummyContent.ITEMS.clear();
            for (City city:cityList) {
                DummyContent.addItem(city.getId(),city.getCityName(),city.getCityCode(), CITY,city.getProvinceId());
            }
            adapter.notifyDataSetChanged();
        }else {
            String cityUrl=url+"/"+provinceSelected.getProvinceCode();
            querFormService(cityUrl, CITY);
        }
    }

    private void setCountyData(){
        textView.setText(citySelected.getCityName());
        textView2.setVisibility(View.VISIBLE);
        countyList=BaseApplication.getIns().getDaoSession()
                .queryBuilder(County.class).where(CountyDao.Properties.CityId.eq(citySelected.getCityCode())).list();
        if (countyList.size()>0){
            DummyContent.ITEMS.clear();
            for (County county:countyList){
                DummyContent.addItem(county.getId(),county.getCountyName(),county.getCountyCode(), COUNTY,county.getCityId());
            }
            adapter.notifyDataSetChanged();
        }else {
            String countyUrl=url+"/"+provinceSelected.getProvinceCode()+"/"+citySelected.getCityCode();
            querFormService(countyUrl, COUNTY);
        }
    }

    private void querFormService(String url, final String type) {
        NetManager.INSTANCE.getShopClient()
                .getProvinceList(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DlObserve<Response<ResponseBody>>(getActivity(),"数据加载中，请稍后") {
                    @Override
                    public void onResponse(Response<ResponseBody> response) throws IOException {
                        if (response!=null&&response.code()==200){
                            String s=response.body().string();
                            boolean rest=false;
                            if (type.equals(PROVINCE)){
                                rest= JsonUtils.SaveProvince(s);
                            }else if (type.equals(CITY)){
                                rest=JsonUtils.SaveCity(s,provinceSelected.getProvinceCode());
                            }else if (type.equals(COUNTY)){
                                rest=JsonUtils.SaveCounty(s,citySelected.getCityCode());
                            }

                            if (rest){
                                if (type.equals(PROVINCE)){
                                    setProvinceData();
                                }else if (type.equals(CITY)){
                                    setCityData();
                                }else if (type.equals(COUNTY)){
                                    setCountyData();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        ToastUtils.showLongToast(errorCode);
                    }
                });

    }

}
