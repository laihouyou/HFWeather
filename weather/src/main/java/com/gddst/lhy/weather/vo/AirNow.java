package com.gddst.lhy.weather.vo;

public class AirNow {

    /**
     * aqi : 19
     * co : 0
     * main :
     * no2 : 34
     * o3 : 31
     * pm10 : 18
     * pm25 : 8
     * pub_time : 2017-11-07 22:00
     * qlty : 优
     * so2 : 2
     */

    private String aqi;
    private String co;
    private String main;
    private String no2;
    private String o3;
    private String pm10;
    private String pm25;
    private String pub_time;
    private String qlty;
    private String so2;

    public String getAqi() {
        return aqi == null ? "" : aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getCo() {
        return co == null ? "" : co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getMain() {
        return main == null ? "" : main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getNo2() {
        return no2 == null ? "" : no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getO3() {
        return o3 == null ? "" : o3;
    }

    public void setO3(String o3) {
        this.o3 = o3;
    }

    public String getPm10() {
        return pm10 == null ? "" : pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm25() {
        return pm25 == null ? "" : pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPub_time() {
        return pub_time == null ? "" : pub_time;
    }

    public void setPub_time(String pub_time) {
        this.pub_time = pub_time;
    }

    public String getQlty() {
        return qlty == null ? "" : qlty;
    }

    public void setQlty(String qlty) {
        this.qlty = qlty;
    }

    public String getSo2() {
        return so2 == null ? "" : so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }
}
