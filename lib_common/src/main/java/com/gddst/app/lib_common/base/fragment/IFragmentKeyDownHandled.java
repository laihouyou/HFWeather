package com.gddst.app.lib_common.base.fragment;

import android.view.KeyEvent;

public interface IFragmentKeyDownHandled {
    /**
     *
     * @param keyCode
     * @param event
     * @return  true表示自己处理返回事件， false表示不处理
     */
    public abstract boolean excueOnKeyDown(int keyCode, KeyEvent event);
}
