package com.gddst.app.lib_common.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gddst.app.lib_common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseActivity extends AppCompatActivity {
    private SparseArray<View> mSparseArray = new SparseArray<>();
    private long                 mTimeMillis;
    private Toolbar toolbar;
    public ActionBar mActionBar;
    private boolean              isTransverse;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取全局的对象
        //获取activitymanager管理器,进行统一管理
        setContentView(BindLayout());

        //绑定黄油刀
        if (isNeedTranslucentStatus()) {

            setTranslucentStatus();

        } else {
            if (toolbar != null) {
                toolbar.setPadding(0, 0, 0, 0);
            }

        }

        //初始化布局
        initView();
        //是否横向
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && isTransverse){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //初始化数据
        initData();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
    }

    /**
     * 设置toolbar的标题
     *
     * @param title
     */
    public void setToolbarTitle(String title) {
        if (mActionBar == null) {
            mActionBar = getSupportActionBar();
        }
        mActionBar.setTitle(title);
    }

    /**
     * [绑定控件]
     *
     * @param resId
     * @return
     */
    protected <T extends View> T $(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }


    /**
     * 设置是否需要沉浸式
     *
     * @return
     */
    public boolean isNeedTranslucentStatus() {
        return false;
    }

    /**
     * 设置沉浸式
     */
    private void setTranslucentStatus() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 这个属性4.4算是全透明（有的机子是过渡形式的透明），5.0就是半透明了 我的模拟器、真机都是半透明，

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);// calculateStatusColor(Color.WHITE, (int) alphaValue)

        }

    }

    /**
     * 双击退出
     */
    @Override
    public void onBackPressed() {
        if (isDoubleExit()) {
            if (System.currentTimeMillis() - mTimeMillis > 2000) {
//                ToastUtils.showLongToast("在按一次退出");
                mTimeMillis = System.currentTimeMillis();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 并且释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 获取控件id,类似于findviewbyid
     *
     * @param id 传入一个控件的id,自动寻找
     * @return 返回一个控件
     */
    protected View getViewId(int id) {
        View view = mSparseArray.get(id);
        if (view == null) {
            view = this.findViewById(id);
            mSparseArray.put(id, view);
        }
        return view;
    }


    /*
     * 绑定一个布局id
     */
    @LayoutRes
    protected abstract int BindLayout();

    /**
     * 查找id,或者初始化toolbar的标题
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * [页面跳转] *
     */
    public void startActivity(Class<? extends Activity> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * [携带数据的页面跳转] *
     */
    public void startActivity(Class<? extends Activity> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    /**
     * [含有Bundle通过Class打开编辑界面] *
     */
    public void startActivityForResult(Class<? extends Activity> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public boolean isDoubleExit() {
        return false;
    }


    /**
     * eventbus处理
     */
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object event) {
        onMessageEvent(event);
    }

    protected void onMessageEvent(Object event) {

    }

    protected void showShortToast(String s) {
        ToastUtils.showShortToastSafe(s);
    }


    protected void showLongToast(String s) {
        ToastUtils.showLongToast(s);
    }


    public void setTransverse(boolean transverse) {
        isTransverse = transverse;
    }

}
