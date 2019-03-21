package com.gddst.lhy.weather.vo;

import java.util.ArrayList;
import java.util.List;

public class WeatherVo {

    private BasicBean basic;
    private UpdateBean update;
    private String status;
    private NowBean now;
    private List<DailyForecastBean> daily_forecast;
    private List<HourlyBean> hourly;
    private List<LifestyleBean> lifestyle;

    private AirNow airNow;

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public UpdateBean getUpdate() {
        return update;
    }

    public void setUpdate(UpdateBean update) {
        this.update = update;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NowBean getNow() {
        return now;
    }

    public void setNow(NowBean now) {
        this.now = now;
    }

    public List<DailyForecastBean> getDaily_forecast() {
        if (daily_forecast == null) {
            return new ArrayList<>();
        }
        return daily_forecast;
    }

    public void setDaily_forecast(List<DailyForecastBean> daily_forecast) {
        this.daily_forecast = daily_forecast;
    }

    public List<HourlyBean> getHourly() {
        if (hourly == null) {
            return new ArrayList<>();
        }
        return hourly;
    }

    public void setHourly(List<HourlyBean> hourly) {
        this.hourly = hourly;
    }

    public List<LifestyleBean> getLifestyle() {
        if (lifestyle == null) {
            return new ArrayList<>();
        }
        return lifestyle;
    }

    public void setLifestyle(List<LifestyleBean> lifestyle) {
        this.lifestyle = lifestyle;
    }

    public AirNow getAirNow() {
        return airNow;
    }

    public void setAirNow(AirNow airNow) {
        this.airNow = airNow;
    }

    public static class BasicBean {
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

        private String cid;
        private String location;
        private String parent_city;
        private String admin_area;
        private String cnty;
        private String lat;
        private String lon;
        private String tz;

        public String getCid() {
            return cid == null ? "" : cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getLocation() {
            return location == null ? "" : location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getParent_city() {
            return parent_city == null ? "" : parent_city;
        }

        public void setParent_city(String parent_city) {
            this.parent_city = parent_city;
        }

        public String getAdmin_area() {
            return admin_area == null ? "" : admin_area;
        }

        public void setAdmin_area(String admin_area) {
            this.admin_area = admin_area;
        }

        public String getCnty() {
            return cnty == null ? "" : cnty;
        }

        public void setCnty(String cnty) {
            this.cnty = cnty;
        }

        public String getLat() {
            return lat == null ? "" : lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon == null ? "" : lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getTz() {
            return tz == null ? "" : tz;
        }

        public void setTz(String tz) {
            this.tz = tz;
        }
    }

    public static class UpdateBean {
        /**
         * loc : 2019-03-21 16:55
         * utc : 2019-03-21 08:55
         */

        private String loc;
        private String utc;

        public String getLoc() {
            return loc == null ? "" : loc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        public String getUtc() {
            return utc == null ? "" : utc;
        }

        public void setUtc(String utc) {
            this.utc = utc;
        }
    }

    public static class NowBean {
        /**
         * cloud : 91
         * cond_code : 104
         * cond_txt : 阴
         * fl : 14
         * hum : 77
         * pcpn : 0.0
         * pres : 1006
         * tmp : 15
         * vis : 18
         * wind_deg : 0
         * wind_dir : 北风
         * wind_sc : 2
         * wind_spd : 8
         */

        private String cloud;
        private String cond_code;
        private String cond_txt;
        private String fl;
        private String hum;
        private String pcpn;
        private String pres;
        private String tmp;
        private String vis;
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

        public String getFl() {
            return fl == null ? "" : fl;
        }

        public void setFl(String fl) {
            this.fl = fl;
        }

        public String getHum() {
            return hum == null ? "" : hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getPcpn() {
            return pcpn == null ? "" : pcpn;
        }

        public void setPcpn(String pcpn) {
            this.pcpn = pcpn;
        }

        public String getPres() {
            return pres == null ? "" : pres;
        }

        public void setPres(String pres) {
            this.pres = pres;
        }

        public String getTmp() {
            return tmp == null ? "" : tmp;
        }

        public void setTmp(String tmp) {
            this.tmp = tmp;
        }

        public String getVis() {
            return vis == null ? "" : vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
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

    public static class DailyForecastBean {
        /**
         * cond_code_d : 104
         * cond_code_n : 305
         * cond_txt_d : 阴
         * cond_txt_n : 小雨
         * date : 2019-03-21
         * hum : 65
         * mr : 19:26
         * ms : 07:13
         * pcpn : 1.0
         * pop : 55
         * pres : 977
         * sr : 06:55
         * ss : 19:06
         * tmp_max : 23
         * tmp_min : 14
         * uv_index : 2
         * vis : 21
         * wind_deg : -1
         * wind_dir : 无持续风向
         * wind_sc : 1-2
         * wind_spd : 4
         */

        private String cond_code_d;
        private String cond_code_n;
        private String cond_txt_d;
        private String cond_txt_n;
        private String date;
        private String hum;
        private String mr;
        private String ms;
        private String pcpn;
        private String pop;
        private String pres;
        private String sr;
        private String ss;
        private String tmp_max;
        private String tmp_min;
        private String uv_index;
        private String vis;
        private String wind_deg;
        private String wind_dir;
        private String wind_sc;
        private String wind_spd;

        public String getCond_code_d() {
            return cond_code_d == null ? "" : cond_code_d;
        }

        public void setCond_code_d(String cond_code_d) {
            this.cond_code_d = cond_code_d;
        }

        public String getCond_code_n() {
            return cond_code_n == null ? "" : cond_code_n;
        }

        public void setCond_code_n(String cond_code_n) {
            this.cond_code_n = cond_code_n;
        }

        public String getCond_txt_d() {
            return cond_txt_d == null ? "" : cond_txt_d;
        }

        public void setCond_txt_d(String cond_txt_d) {
            this.cond_txt_d = cond_txt_d;
        }

        public String getCond_txt_n() {
            return cond_txt_n == null ? "" : cond_txt_n;
        }

        public void setCond_txt_n(String cond_txt_n) {
            this.cond_txt_n = cond_txt_n;
        }

        public String getDate() {
            return date == null ? "" : date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getHum() {
            return hum == null ? "" : hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getMr() {
            return mr == null ? "" : mr;
        }

        public void setMr(String mr) {
            this.mr = mr;
        }

        public String getMs() {
            return ms == null ? "" : ms;
        }

        public void setMs(String ms) {
            this.ms = ms;
        }

        public String getPcpn() {
            return pcpn == null ? "" : pcpn;
        }

        public void setPcpn(String pcpn) {
            this.pcpn = pcpn;
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

        public String getSr() {
            return sr == null ? "" : sr;
        }

        public void setSr(String sr) {
            this.sr = sr;
        }

        public String getSs() {
            return ss == null ? "" : ss;
        }

        public void setSs(String ss) {
            this.ss = ss;
        }

        public String getTmp_max() {
            return tmp_max == null ? "" : tmp_max;
        }

        public void setTmp_max(String tmp_max) {
            this.tmp_max = tmp_max;
        }

        public String getTmp_min() {
            return tmp_min == null ? "" : tmp_min;
        }

        public void setTmp_min(String tmp_min) {
            this.tmp_min = tmp_min;
        }

        public String getUv_index() {
            return uv_index == null ? "" : uv_index;
        }

        public void setUv_index(String uv_index) {
            this.uv_index = uv_index;
        }

        public String getVis() {
            return vis == null ? "" : vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
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

    public static class HourlyBean {
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

    public static class LifestyleBean {
        /**
         * type : comf
         * brf : 舒适
         * txt : 白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。
         */

        private String type;
        private String brf;
        private String txt;

        public String getType() {
            return type == null ? "" : type;
        }

        public void setType(String type) {
            this.type = type;
        }

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
    }


}
