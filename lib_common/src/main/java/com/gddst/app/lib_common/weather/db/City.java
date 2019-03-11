package com.gddst.app.lib_common.weather.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class City {
    @Id(autoincrement = true)
    private long id;

    private String cityName;

    private int cityCode;

    private long provinceId;

    @Generated(hash = 610858581)
    public City(long id, String cityName, int cityCode, long provinceId) {
        this.id = id;
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.provinceId = provinceId;
    }

    @Generated(hash = 750791287)
    public City() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }


}
