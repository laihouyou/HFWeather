package com.gddst.app.lib_common.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.com.sky.downloader.greendao.DaoMaster;
import com.com.sky.downloader.greendao.DaoSession;
import com.gddst.app.lib_common.constant.Constant;
import com.gddst.app.lib_common.net.NetManager;
import com.gddst.app.lib_common.utils.AppUtil;
import com.gddst.app.lib_common.utils.ToastUtils;
import com.gddst.app.lib_common.utils.Utils;
import com.google.gson.Gson;

/**
 * 要想使用BaseApplication，必须在组件中实现自己的Application，并且继承BaseApplication；
 * 组件中实现的Application必须在debug包中的AndroidManifest.xml中注册，否则无法使用；
 * 组件的Application需置于java/debug文件夹中，不得放于主代码；
 * 组件中获取Context的方法必须为:Utils.getContext()，不允许其他写法；
 *
 * @author 2016/12/2 17:02
 * @version V1.0.0
 * @name BaseApplication
 */
public class BaseApplication extends Application {

    public static final String ROOT_PACKAGE = "com.guiying.module";

    private static BaseApplication sInstance;

    private SharedPreferences pPrefere; // 定义数据存储
    private SharedPreferences.Editor pEditor; // sharedpreferred数据提交

    private static DaoSession daoSession;
    private static Gson gson;

    public static BaseApplication getIns() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
//        Logger.init("pattern").logLevel(LogLevel.FULL);
        Utils.init(this);
        ToastUtils.init(true);
        NetManager.INSTANCE.initShopClient();
        initManager();
        setSupDao();
        initLocation();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        Log.i("sha1",AppUtil.sHA1(this));
    }

    /**
     * 获取StudentDao
     */
    private void setSupDao() {
        // 创建数据
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "hlq.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        daoSession=daoMaster.newSession();
    }

    public DaoSession getDaoSession(){
        if (daoSession==null){
            setSupDao();
        }
        return daoSession;
    }


    private void initManager() {
        // 系统初始参数读取
        if (pPrefere == null) {
            pPrefere = getSharedPreferences(Constant.SPF_NAME, Context.MODE_PRIVATE);
        }
        pEditor = pPrefere.edit();
    }


    public String getCurUserName() {
        return pPrefere.getString(Constant.SPF_DEF_LOGIN_USER, "");
    }

    public void setCurUserName(String value) {
        pEditor.putString(Constant.SPF_DEF_LOGIN_USER, value).commit();
    }

    public String getUserPawword() {
        return pPrefere.getString(Constant.SPF_DEF_LOGIN_PASSWORD, "");
    }

    public void setUserPassword(String value) {
        pEditor.putString(Constant.SPF_DEF_LOGIN_PASSWORD, value).commit();
    }

    public boolean isRember() {
        return pPrefere.getBoolean(Constant.SPF_AUTO_REMBER, false);
    }

    public void setRember(boolean value) {
        pEditor.putBoolean(Constant.SPF_AUTO_REMBER, value).commit();
    }

    public void setAutoLogin(boolean value) {
        pEditor.putBoolean(Constant.SPF_AUTO_LOGIN, value).commit();
    }

    public String getPhoneIMEI() {
        return pPrefere.getString(Constant.SPF_PHONE_IMEI, "");
    }

    public static Gson getGson() {
        if (gson==null){
            gson=new Gson();
        }
        return gson;
    }
}
