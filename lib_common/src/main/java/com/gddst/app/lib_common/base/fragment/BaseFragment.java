package com.gddst.app.lib_common.base.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements IFragmentKeyDownHandled, View.OnTouchListener {
    private String TAG = BaseFragment.class.getSimpleName();

    private View mRoot;

    /**
     * 是否执行了lazyLoad方法
     */
    private boolean isLoaded;
    /**
     * 是否创建了View
     */
    private boolean isCreateView;

    /**
     * 当从另一个activity回到fragment所在的activity
     * 当fragment回调onResume方法的时候，可以通过这个变量判断fragment是否可见，来决定是否要刷新数据
     */
    public boolean isVisible;

    /*
     * 此方法在viewpager嵌套fragment时会回调
     * 查看FragmentPagerAdapter源码中instantiateItem和setPrimaryItem会调用此方法
     * 在所有生命周期方法前调用
     * 这个基类适用于在viewpager嵌套少量的fragment页面
     * 该方法是第一个回调，可以将数据放在这里处理（viewpager默认会预加载一个页面）
     * 只在fragment可见时加载数据，加快响应速度
     * */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }


    /*
     * 防止view的重复加载 与FragmentPagerAdapter 中destroyItem方法取消调用父类的效果是一样的
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRoot == null){
            mRoot = createView(inflater,container,savedInstanceState);
            isCreateView = true;
            mRoot.setOnTouchListener(this);
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view!=null){
            initView(view);
            initListener();
            onVisible();
        }
    }

    protected void onVisible() {

        isVisible = true;

        if(isLoaded){
            refreshLoad();
        }
        if (!isLoaded && isCreateView && getUserVisibleHint()) {
            isLoaded = true;
            lazyLoad();
        }
    }

    protected void onInvisible() {
        isVisible = false;
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initView(View view);
    protected abstract void initListener();


    /**
     * fragment第一次可见的时候回调此方法
     */
    protected abstract void lazyLoad();

    /**
     * 在Fragment第一次可见加载以后，每次Fragment滑动可见的时候会回调这个方法，
     * 子类可以重写这个方法做数据刷新操作
     */
    protected void refreshLoad(){}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //返回true防止fragment点击事件重叠
        return true;
    }
}
