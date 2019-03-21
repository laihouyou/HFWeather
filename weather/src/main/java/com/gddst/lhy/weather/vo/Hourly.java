package com.gddst.lhy.weather.vo;

public class Hourly {

    /**
     * cloud : 66
     * cond_code : 104
     * cond_txt : 阴
     * dew : 10
     * hum : 78
     * pop : 7
     * pres : 982
     * time : 2019-03-21 19:00
     * tmp : 20
     * wind_deg : 30
     * wind_dir : 东北风
     * wind_sc : 1-2
     * wind_spd : 8
     */

    private String cloud;
    private String cond_code;
    private String cond_txt;
    private String dew;
    private String hum;
    private String pop;
    private String pres;
    private String time;
    private String tmp;
    private String wind_deg;
    private String wind_dir;
    private String wind_sc;
    private String wind_spd;

    public String getCloud() {
        return cloud == null ? "" : cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getCond_code() {
        return cond_code == null ? "" : cond_code;
    }

    public void setCond_code(String cond_code) {
        this.cond_code = cond_code;
    }

    public String getCond_txt() {
        return cond_txt == null ? "" : cond_txt;
    }

    public void setCond_txt(String cond_txt) {
        this.cond_txt = cond_txt;
    }

    public String getDew() {
        return dew == null ? "" : dew;
    }

    public void setDew(String dew) {
        this.dew = dew;
    }

    public String getHum() {
        return hum == null ? "" : hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getPop() {
        return pop == null ? "" : pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public String getPres() {
        return pres == null ? "" : pres;
    }

    public void setPres(String pres) {
        this.pres = pres;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTmp() {
        return tmp == null ? "" : tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getWind_deg() {
        return wind_deg == null ? "" : wind_deg;
    }

    public void setWind_deg(String wind_deg) {
        this.wind_deg = wind_deg;
    }

    public String getWind_dir() {
        return wind_dir == null ? "" : wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public String getWind_sc() {
        return wind_sc == null ? "" : wind_sc;
    }

    public void setWind_sc(String wind_sc) {
        this.wind_sc = wind_sc;
    }

    public String getWind_spd() {
        return wind_spd == null ? "" : wind_spd;
    }

    public void setWind_spd(String wind_spd) {
        this.wind_spd = wind_spd;
    }
}
