package com.gddst.app.lib_common.utils;

public class WeatherUtil {
    public static final String weatherVo="weatherVo";
    public static final String weather="weather_";
    public static final String drawable="drawable";
    public static final String forecastBaseList="forecastBaseList";
    public static final String weatherId="weatherId";

    public static final String HeWeather6="HeWeather6";
    public static final String air_now_city="air_now_city";
    public static final String daily_forecast="daily_forecast";
    public static final String lifestyle="lifestyle";
    public static final String basic="basic";
    public static final String cid="cid";
    public static final String cityVoList="cityVoList";
    public static final String picUrl="picUrl";
    public static final String status="status";
    public static final String ok="ok";

    //天气生活指数
    public static final String comf="comf";
    public static final String cw="cw";
    public static final String drsg="drsg";
    public static final String flu="flu";
    public static final String sport="sport";
    public static final String trav="trav";
    public static final String uv="uv";
    public static final String air="air";
    public static final String ac="ac";

    //从城市管理页面返回天气主页面
    public static final String back_action="back_action";
    //旧版选择县级城市后更新主页面数据
    public static final String updata_action="updata_action";
    //删除城市后更新主页面数据
    public static final String delete_action="delete_action";
    //定位成功插入城市后更新主页面数据
    public static final String add_action="add_action";

    //保存本地数据的热门城市数据
    public static final String hostCityListStr="hostCityListStr";

    //天气自动更新间隔时间
    public static final long weatherUpdateTimeInterval=4*60*60;

    //定位城市类型
    public static final int city_location=1;
    //城市管理选择的城市类型
    public static final int city_select=2;
    //获取全球的热门城市
    public static final String world="world";
    //获取全球的城市与中国景区
    public static final String world_scenic="world,scenic";
    //返回中国热门城市
    public static final String cn="cn";
    //查询海外热门城市（不含中国）
    public static final String overseas="overseas";
    //返回的热门城市数量
    public static final int city_host_num=50;
    //返回的搜索城市数量
    public static final int city_search_num=20;
}
