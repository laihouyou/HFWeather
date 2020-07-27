package com.lhy.wanandroid.base

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gddst.app.lib_common.base.BaseActivity

abstract class MyBaseVmActivity<VM:BaseViewModel> : BaseActivity() {

    protected open lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        observe()
        // 因为Activity恢复后savedInstanceState不为null，
        // 重新恢复后会自动从ViewModel中的LiveData恢复数据，
        // 不需要重新初始化数据。
        if (savedInstanceState == null) {
            initData()
        }
    }

    private fun initViewModel() {
        mViewModel=ViewModelProvider(this).get(viewModeClass())
    }

    protected abstract fun viewModeClass():Class<VM>

    open fun observe(){
        // 登录失效，跳转登录页
        mViewModel.loginStatusInvalid.observe(this, Observer {
//            if (it) {
//                Bus.post(USER_LOGIN_STATE_CHANGED, false)
//                ActivityManager.start(LoginActivity::class.java)
//            }
        })
    }
}