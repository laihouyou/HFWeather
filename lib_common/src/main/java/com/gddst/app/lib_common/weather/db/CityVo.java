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
    @Id(autoincrement = true)
    private long id;
    @Index(unique = true) // 唯一性
    private String cid;
    private String location;
    private String parent_city;
    private String admin_area;
    private String cnty;
    private String lat;
    private String lon;
    private String tz;
    @Generated(hash = 1714164286)
    public CityVo(long id, String cid, String location, String parent_city,
            String admin_area, String cnty, String lat, String lon, String tz) {
        this.id = id;
        this.cid = cid;
        this.location = location;
        this.parent_city = parent_city;
        this.admin_area = admin_area;
        this.cnty = cnty;
        this.lat = lat;
        this.lon = lon;
        this.tz = tz;
    }
    @Generated(hash = 54631091)
    public CityVo() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
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

}
