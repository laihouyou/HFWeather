package com.gddst.app.lib_common.weather.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CityVo {
    /**
     * cid : CN101040100
     * location : 重庆
     * parent_city : 重庆
     * admin_area : 重庆
     * cnty : 中国
     * lat : 29.56376076
     * lon : 106.55046082
     * tz : +8.00
     */
    @Id()
    private Long id;
    @Index(unique = true) // 唯一性
    private String cid;
    private String location;
    private String parent_city;
    private String admin_area;
    private String cnty;
    private String lat;
    private String lon;
    private String tz;
    private String addCityTime;
    private int cityType;    //1 为定位城市  2为城市列表选择城市
    private boolean isSelected;     //该城市本地是否已经选择
    @Generated(hash = 54631091)
    public CityVo() {
    }
    @Generated(hash = 180632876)
    public CityVo(Long id, String cid, String location, String parent_city,
            String admin_area, String cnty, String lat, String lon, String tz,
            String addCityTime, int cityType, boolean isSelected) {
        this.id = id;
        this.cid = cid;
        this.location = location;
        this.parent_city = parent_city;
        this.admin_area = admin_area;
        this.cnty = cnty;
        this.lat = lat;
        this.lon = lon;
        this.tz = tz;
        this.addCityTime = addCityTime;
        this.cityType = cityType;
        this.isSelected = isSelected;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCid() {
        return this.cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getParent_city() {
        return this.parent_city;
    }
    public void setParent_city(String parent_city) {
        this.parent_city = parent_city;
    }
    public String getAdmin_area() {
        return this.admin_area;
    }
    public void setAdmin_area(String admin_area) {
        this.admin_area = admin_area;
    }
    public String getCnty() {
        return this.cnty;
    }
    public void setCnty(String cnty) {
        this.cnty = cnty;
    }
    public String getLat() {
        return this.lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        return this.lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public String getTz() {
        return this.tz;
    }
    public void setTz(String tz) {
        this.tz = tz;
    }
    public String getAddCityTime() {
        return this.addCityTime;
    }
    public void setAddCityTime(String addCityTime) {
        this.addCityTime = addCityTime;
    }
    public int getCityType() {
        return this.cityType;
    }
    public void setCityType(int cityType) {
        this.cityType = cityType;
    }
    public boolean getIsSelected() {
        return this.isSelected;
    }
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
