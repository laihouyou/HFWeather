package com.gddst.app.lib_common.weather.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class County  {
    @Id(autoincrement = true)
    private long id;

    private String countyName;

    private int countyCode;

    private long cityId;

    @Generated(hash = 1504691472)
    public County(long id, String countyName, int countyCode, long cityId) {
        this.id = id;
        this.countyName = countyName;
        this.countyCode = countyCode;
        this.cityId = cityId;
    }

    @Generated(hash = 1991272252)
    public County() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountyName() {
        return this.countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCountyCode() {
        return this.countyCode;
    }

    public void setCountyCode(int countyCode) {
        this.countyCode = countyCode;
    }

    public long getCityId() {
        return this.cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }


}
