package com.gddst.lhy.weather.vo;

import java.util.ArrayList;
import java.util.List;

public class WeatherVo extends NowJson{

    /**
     * basic : {"cid":"CN101010100","location":"北京","parent_city":"北京","admin_area":"北京","cnty":"中国","lat":"39.90498734","lon":"116.40528870","tz":"8.0"}
     * now : {"cond_code":"101","cond_txt":"多云","fl":"16","hum":"73","pcpn":"0","pres":"1017","tmp":"14","vis":"1","wind_deg":"11","wind_dir":"北风","wind_sc":"微风","wind_spd":"6"}
     * status : ok
     * update : {"loc":"2017-10-26 17:29","utc":"2017-10-26 09:29"}
     */

    private List<WeatherForecast> weatherForecastList;
    private AirNow airNow;
    private List<LifestyleVo> lifestyleVoList;

    public List<WeatherForecast> getWeatherForecastList() {
        if (weatherForecastList == null) {
            return new ArrayList<>();
        }
        return weatherForecastList;
    }

    public void setWeatherForecastList(List<WeatherForecast> weatherForecastList) {
        this.weatherForecastList = weatherForecastList;
    }

    public AirNow getAirNow() {
        return airNow;
    }

    public void setAirNow(AirNow airNow) {
        this.airNow = airNow;
    }

    public List<LifestyleVo> getLifestyleVoList() {
        if (lifestyleVoList == null) {
            return new ArrayList<>();
        }
        return lifestyleVoList;
    }

    public void setLifestyleVoList(List<LifestyleVo> lifestyleVoList) {
        this.lifestyleVoList = lifestyleVoList;
    }
}
