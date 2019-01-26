package com.gddst.app.lib_common.location;


import com.gddst.app.lib_common.location.coordinate.Gcj022Bd09;
import com.gddst.app.lib_common.location.coordinate.Gcj022Gps;
import com.gddst.app.lib_common.location.coordinate.Gps2Mct;

import java.io.Serializable;
import java.util.Map;

public class LocationInfo implements Serializable {
	
	private double latitude; // 经度
	
	private double longitude; // 纬度

	private double latitudeGcj02; // Gcj02经度

	private double longitudeGcj02; // Gcj02纬度

	private double altitude; // 海拨
	
	private float accuracy; // 精度
	
	private float speed; // 速度
	
	private float bearing;// 方位角
	
	private String time; // 卫星时间
	
	private double mapx;
	
	private double mapy;
	
	private int satellites; //卫星数量
	
	private String locationModel; //定位模式:gps、网络、百度
	
	private String addr; //所在地址
	
	private String imei;
	
	private String usnum;
	
	private Long usid;
	
	private Long speedType; // 前进方式 （1、步行 2、车载）
	
//	private String locResult;
/*	public LocationInfo(double latitude, double longitude, double altitude,
			float accuracy, float speed, float bearing, String time, double mapx,
			double mapy) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.speed = speed;
		this.bearing = bearing;
		this.time = time;
		this.mapx = mapx;
		this.mapy = mapy;
	}*/

	public LocationInfo(){
		;
	}
	
	public LocationInfo(double latitude, double longitude, double altitude,
                        float accuracy, float speed, float bearing, String time, double mapx,
                        double mapy, String locationModel, String addr, int satellites) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.speed = speed;
		this.bearing = bearing;
		this.time = time;
		this.mapx = mapx;
		this.mapy = mapy;
		this.locationModel = locationModel;
		this.addr = addr;
		this.satellites = satellites;
	}
	
	public Long getSpeedType() {
		return speedType;
	}

	public void setSpeedType(Long speedType) {
		this.speedType = speedType;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getMapx() {
		return mapx;
	}

	public void setMapx(double mapx) {
		this.mapx = mapx;
	}

	public double getMapy() {
		return mapy;
	}

	public void setMapy(double mapy) {
		this.mapy = mapy;
	}

	public int getSatellites() {
		return satellites;
	}

	public void setSatellites(int satellites) {
		this.satellites = satellites;
	}

	public String getLocationModel() {
		return locationModel;
	}

	public void setLocationModel(String locationModel) {
		this.locationModel = locationModel;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getUsnum() {
		return usnum;
	}

	public void setUsnum(String usnum) {
		this.usnum = usnum;
	}

	public Long getUsid() {
		return usid;
	}

	public void setUsid(Long usid) {
		this.usid = usid;
	}

	public double getLatitudeGcj02() {
		return latitudeGcj02;
	}

	public void setLatitudeGcj02(double latitudeGcj02) {
		this.latitudeGcj02 = latitudeGcj02;
	}

	public double getLongitudeGcj02() {
		return longitudeGcj02;
	}

	public void setLongitudeGcj02(double longitudeGcj02) {
		this.longitudeGcj02 = longitudeGcj02;
	}

	//	public String getLocResult() {
//		return locResult;
//	}
//
//	public void setLocResult(String locResult) {
//		this.locResult = locResult;
//	}

	//获取当前GPS坐标位置
	public String getCurGpsPosition(){
		return this.longitude +" "+this.latitude;
		//AppContext.getInstance().getCurLocation().getLongitude()+" "+AppContext.getInstance().getCurLocation().getLatitude()
	}
	
	//获取当前地图坐标系统的坐标位置
	public String getCurMapPosition(){
		String x = String.valueOf(this.mapx);
		String y = String.valueOf(this.mapy);
		
		if(x.contains("E")){
			x = _transformE(String.valueOf(this.mapx));
		}
		if (y.contains("E")){
			y = _transformE(String.valueOf(this.mapx));
		}
		return x+" "+y;
	}
	
	public String getCurGcjPosition(){
		Map<String, Double> lonlat = Gcj022Gps.wgs2gcj(this.longitude, this.latitude);
		return	 lonlat.get("lon")+" "+lonlat.get("lat");
		//AppContext.getInstance().getCurLocation().getLongitude()+" "+AppContext.getInstance().getCurLocation().getLatitude()
	}
	
	public String getCurBd09Position(){
		Map<String, Double> lonlat = Gcj022Gps.wgs2gcj(this.longitude, this.latitude);
		double bdlonlat[] = Gcj022Bd09.bd09Encrypt(lonlat.get("lat"), lonlat.get("lon"));
		return	 bdlonlat[0]+" "+bdlonlat[1];
		//AppContext.getInstance().getCurLocation().getLongitude()+" "+AppContext.getInstance().getCurLocation().getLatitude()
	}
	
	//当地图坐标值为web莫卡托时,转换为经纬度时国内的坐标值为gcj02，所以不需要转为84.可直接通过gcj02转为百度的bd09
	public String getCurWebM2Bd09Position(){
		double wgslonlat[] = Gps2Mct.mercator2lonLat(this.mapx, this.mapy);
		//Map<String, Double> lonlat = Gcj022Gps.wgs2gcj(wgslonlat[0], wgslonlat[1]);
		double bdlonlat[] = Gcj022Bd09.bd09Encrypt(wgslonlat[1], wgslonlat[0]);
		return bdlonlat[0]+" "+bdlonlat[1];
		//AppContext.getInstance().getCurLocation().getLongitude()+" "+AppContext.getInstance().getCurLocation().getLatitude()
	}
	
	public String getCurWebM2Bd09Position(double x, double y){
		double wgslonlat[] = Gps2Mct.mercator2lonLat(x, y);
		//Map<String, Double> lonlat = Gcj022Gps.wgs2gcj(wgslonlat[0], wgslonlat[1]);
		double bdlonlat[] = Gcj022Bd09.bd09Encrypt(wgslonlat[1], wgslonlat[0]);
		return bdlonlat[0]+" "+bdlonlat[1];
		//AppContext.getInstance().getCurLocation().getLongitude()+" "+AppContext.getInstance().getCurLocation().getLatitude()
	}
	

	private static String _transformE(String x){
		StringBuffer coord = new StringBuffer();
		int idx = x.indexOf("E");
		if (idx<0){
			return x;
		}
		String startStr = x.substring(0, idx);
		String endStr = x.substring(idx+1);
		idx = startStr.indexOf(".");
		coord.append(startStr.substring(0, idx));
		startStr = startStr.substring(idx+1);
		idx = Integer.valueOf(endStr);
		coord.append(startStr.substring(0, idx));
		if(idx<startStr.length()){
			coord.append(".");
			coord.append(startStr.substring(idx));
		}
		return coord.toString();
	}
	
	public String toString(){
		return this.time+String.valueOf(this.mapx)+String.valueOf(this.mapy);
	}
}
