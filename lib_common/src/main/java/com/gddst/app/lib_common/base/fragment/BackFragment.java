package com.gddst.app.lib_common.base.fragment;

import android.view.KeyEvent;

public abstract class BackFragment extends BaseFragment{
    protected abstract boolean backFragment();

    @Override
    public boolean excueOnKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return backFragment();
        }
        return false;
    }

}
