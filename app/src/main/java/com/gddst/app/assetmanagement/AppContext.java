package com.gddst.app.assetmanagement;

import com.gddst.app.lib_common.base.BaseApplication;

public class AppContext extends BaseApplication {
    private static AppContext mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    // AppContext单态
    public static AppContext getInstance() {
        return mInstance;
    }
}
