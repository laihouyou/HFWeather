package com.gddst.lhy.weather.vo;

public class LifestyleVo {

    /**
     * type : comf
     * brf : 舒适
     * txt : 白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。
     */

    private String type;
    private String brf;
    private String txt;

    public String getBrf() {
        return brf == null ? "" : brf;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }

    public String getTxt() {
        return txt == null ? "" : txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
