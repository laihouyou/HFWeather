package com.gddst.app.lib_common.net.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ParseException;
import android.os.NetworkOnMainThreadException;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

import static com.gddst.app.lib_common.net.base.ErrorCode.EXCHANGE_DATA_ERROR;
import static com.gddst.app.lib_common.net.base.ErrorCode.HTTP_EXCEPTION;
import static com.gddst.app.lib_common.net.base.ErrorCode.INTERRUPTED_IOEXCEPTION;
import static com.gddst.app.lib_common.net.base.ErrorCode.OTHER_IOEXCEPTION;
import static com.gddst.app.lib_common.net.base.ErrorCode.UNKONW_EXCEPTION;


/**
 * @author chenzj
 * @Title: BaseObserver
 * @Description: 类的描述 -
 * @date 2017/2/28 11:45
 * @email admin@chenzhongjin.cn
 */
public abstract class BaseObserver<T> extends DisposableObserver<T> {

    private Context mContext;
    private String msg;
    private ProgressDialog mProgressDialog;

    public BaseObserver(){

    }

    public BaseObserver(Context context, String msg){
        this.mContext=context;
        this.msg=msg;
        if (mProgressDialog==null){
            mProgressDialog=new ProgressDialog(mContext);
            mProgressDialog.setMessage(this.msg);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mProgressDialog!=null&&!mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (null != mProgressDialog) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
        if (throwable instanceof IOException) {
            if (throwable instanceof UnknownHostException) {
                //服务器异常
                handleError(INTERRUPTED_IOEXCEPTION, "服务器异常，请稍后重试");
            } else if (throwable instanceof InterruptedIOException) {
                //超时异常
                handleError(INTERRUPTED_IOEXCEPTION, "请求超时，请稍后重试");
            } else {
                handleError(OTHER_IOEXCEPTION, "网络异常，请稍后重试");
            }
        } else if (throwable instanceof HttpException) {
            //retrofit请求木有返回
            handleError(HTTP_EXCEPTION, "没有数据返回,请稍后重试");
        } else if (throwable instanceof JsonParseException
                || throwable instanceof JSONException
                || throwable instanceof ParseException) {
            //解释数据错误
            handleError(EXCHANGE_DATA_ERROR, "解释数据错误");
        } else if (throwable instanceof DlException) {
            DlException dlException = (DlException) throwable;
            handleError(dlException.getErrorCode(), dlException.getErrorMessage());
        }
        else if (throwable instanceof NetworkOnMainThreadException){
            handleError(UNKONW_EXCEPTION, "请求无法运行在主线程");
        }
        else {
            handleError(UNKONW_EXCEPTION, "网络异常，请稍后重试");
        }
    }

    @Override
    public void onComplete() {
        if (null != mProgressDialog) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    public abstract void handleError(int errorCode, String errorMsg);

}
