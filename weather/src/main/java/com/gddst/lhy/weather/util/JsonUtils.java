package com.gddst.lhy.weather.util;

import android.text.TextUtils;

import com.gddst.app.lib_common.base.BaseApplication;
import com.gddst.app.lib_common.weather.db.City;
import com.gddst.app.lib_common.weather.db.CityVo;
import com.gddst.app.lib_common.weather.db.County;
import com.gddst.app.lib_common.weather.db.Province;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class JsonUtils {
    public static boolean SaveProvince(String json)  {
        if (!TextUtils.isEmpty(json)){
            try {
                JSONArray jsonArray=new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setId(jsonObject.getLong("id"));
                    BaseApplication.getIns().getDaoSession().getProvinceDao().insert(province);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static boolean SaveCity(String json,long provinceId)  {
        if (!TextUtils.isEmpty(json)){
            try {
                JSONArray jsonArray=new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    City city=new City();
                    city.setId(jsonObject.getLong("id"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.setProvinceId(provinceId);
                    BaseApplication.getIns().getDaoSession().getCityDao().insert(city);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static boolean SaveCounty(String json,long cityId)  {
        if (!TextUtils.isEmpty(json)){
            try {
                JSONArray jsonArray=new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    County county=new County();
                    county.setId(jsonObject.getLong("id"));
                    county.setCountyCode(jsonObject.getInt("id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.setCityId(cityId);
                    BaseApplication.getIns().getDaoSession().getCountyDao().insert(county);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static String getCityId(Response<ResponseBody> response) throws IOException, JSONException {
        if (response.code() != 200) {
            return "";
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        JSONObject jsonObjectCity=(weatherObject.getJSONArray(WeatherUtil.basic)).getJSONObject(0);
        String cityId = jsonObjectCity.getString(WeatherUtil.cid);
        return cityId;
    }

    public static List<CityVo> getCityHostList(Response<ResponseBody> response) throws IOException, JSONException {
        if (response.code() != 200) {
            return new ArrayList<>();
        }
        String body = response.body().string();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(WeatherUtil.HeWeather6);
        JSONObject weatherObject = jsonArray.getJSONObject(0);
        String status=weatherObject.getString(WeatherUtil.status);
        if (status.equals(WeatherUtil.ok)){
            JSONArray cityHostArray=(weatherObject.getJSONArray(WeatherUtil.basic));
            List<CityVo> cityVoList=BaseApplication.getGson().fromJson(
                    cityHostArray.toString(),
                    new TypeToken<List<CityVo>>(){}.getType());
            return cityVoList;
        }
        return new ArrayList<>();
    }
}
