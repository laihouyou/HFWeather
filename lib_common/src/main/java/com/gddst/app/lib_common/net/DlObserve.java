package com.gddst.app.lib_common.net;


import android.content.Context;

import com.gddst.app.lib_common.net.base.BaseObserver;

import java.io.IOException;

/**
 * @author chenzj
 * @Title: DlObserve
 * @Description: 类的描述 -
 * @date 2017/3/1 16:18
 * @email admin@chenzhongjin.cn
 */
public abstract class DlObserve<T> extends BaseObserver<T> {

    public DlObserve(){
        super();
    }

    public DlObserve(Context context, String msg) {
        super(context,msg);
    }
    @Override
    public void handleError(int errorCode, String errorMsg) {
        try {
            //需要进行一层全局的拦截.例如登录信息过期等全局弹框
            onError(errorCode, errorMsg);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            onAfter();
        }
    }

    @Override
    public void onNext(T t) {
        try {
            onResponse(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAfter() {
    }

    public abstract void onResponse(T t) throws IOException;

    public abstract void onError(int errorCode, String errorMsg);
}
