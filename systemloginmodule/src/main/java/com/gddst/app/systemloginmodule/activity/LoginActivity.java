package com.gddst.app.systemloginmodule.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.gddst.app.baidu_tts.tts.InitTTS;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.app.lib_common.location.trace.Agps;
import com.gddst.app.lib_common.net.HttpUrl;
import com.gddst.app.lib_common.utils.MD5;
import com.gddst.app.lib_common.utils.ToastUtils;
import com.gddst.app.lib_common.utils.Utils;
import com.gddst.app.lib_common.widgets.ClearableEditText;
import com.gddst.app.rxpermissions.RxPermissionsUtil;
import com.gddst.app.systemloginmodule.R;
import com.gddst.app.systemloginmodule.url.URL;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity  {
    private static  final int DATA_CODE=54156;
    @Override
    protected int BindLayout() {
        return R.layout.login_activity2;
    }

    @Override
    protected void initView() {
       mUser = (ClearableEditText) findViewById(R.id.login_user_edit);
        mPassword = (EditText) findViewById(R.id.login_passwd_edit);

//		userRegedit = (TextView) findViewById(R.id.login_register);
//		userRegedit.setOnClickListener(this);
//		forget_password = (TextView) findViewById(R.id.forget_password);
//		forget_password.setOnClickListener(this);
        loginBtn = (TextView) findViewById(R.id.login_login_btn);
//        setSystem = (TextView) findViewById(R.id.user_set_system);

        login_map_spinner = (Spinner) findViewById(R.id.login_map_spinner);
        login_map_spinner.setVisibility(View.GONE);

        remUser = (CheckBox) findViewById(R.id.rem_user);

        // 默认显示登录用户名
        mUser.setText(Utils.getContext().getCurUserName());

        mUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPassword.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // 判断记住密码多选框的状态
        if (Utils.getContext().isRember()) {
            // 设置默认是记录密码状态
            remUser.setChecked(true);
            mPassword.setText(Utils.getContext().getUserPawword());
//			// 判断自动登陆多选框状态
//			if (Utils.getContext().isAutoLogin()) {
//				// 设置默认是自动登录状态
//				autoLogin.setChecked(true);
//				// 跳转界面
//				login_main(loginBtn);
//			}
        }

        // 监听记住密码多选框按钮事件
        remUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (remUser.isChecked()) {

                    Utils.getContext().setRember(true);

                } else {
                    Utils.getContext().setRember(false);
                }

            }
        });

//		// 监听自动登录多选框事件
//		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				if (autoLogin.isChecked()) {
//					Utils.getContext().setAutoLogin(true);
//				} else {
//					Utils.getContext().setAutoLogin(false);
//				}
//			}
//		});
    }

    @Override
    protected void initData() {
        RxPermissionsUtil.requestEachRxPermission(
                this,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
        initLocation();
        InitTTS.initialTts(this);
    }

    private ClearableEditText mUser; // 帐号编辑框
    private EditText mPassword; // 密码编辑框
    private TextView userRegedit;// 注册
    private TextView loginBtn;// 登录
//    private TextView setSystem;// 重新初始化
    private TextView forget_password;// 忘记密码
    private CheckBox remUser;
    private Spinner login_map_spinner;

    private ProgressDialog progress;


    public void login_main(View v) {
//        if (null == Utils.getContext().getServerUrl()) {
//            new AlertDialog.Builder(Login.this)
//                    .setIcon(
//                            getResources().getDrawable(
//                                    R.drawable.login_error_icon))
//                    .setTitle("登录错误").setMessage("无法连接服务器，请检查服务器连接设置或重新初始化！")
//                    .create().show();
//            return;
//        }

//        if ("?imei".equals(mUser.getText().toString())) {
//            mUser.setText(Utils.getContext().getPhoneIMEI());
//            return;
//        }

        if ("".equals(mUser.getText().toString())
                || "".equals(mPassword.getText().toString())) {
            new AlertDialog.Builder(this)
                    .setIcon(
                            getResources().getDrawable(
                                    R.drawable.login_error_icon))
                    .setTitle("登录错误").setMessage("帐号或者密码不能为空，\n请输入后再登录！")
                    .create().show();
            return;
        }
        String userName = mUser.getText().toString();
        String userPwd = mPassword.getText().toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("userName", userName);
        map.put("userPwd", userPwd);
        map.put("phoneIMEI", Utils.getContext().getPhoneIMEI());


        String url= HttpUrl.SHOP_URL + URL._REST_USERLOGIN
                + "uuid=" + map.get("phoneIMEI")
                + "&userName=" + map.get("userName") + "&password=" + MD5.getInstance().getMD5ofStr(map.get("userPwd")).toLowerCase();

        //语音合成测试
        InitTTS.synthesizer.speak(userName);


//        NetManager.INSTANCE.getShopClient()
//                .generalRequest(url)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DlObserve<String>(this,getString(R.string.login_msg)) {
//                    @Override
//                    public void onResponse(String s) throws IOException {
//                        Gson gson = new Gson();
//                        // 用户属性对象
//                        UserBeanVO userBean = gson.fromJson(s, UserBeanVO.class);
//                        if (userBean!=null){
////                            ToastUtils.showShortToast(userBean.get);
//                            if (userBean.getState().equals("1")){   //登入成功
//                            ToastUtils.showShortToast("登入成功");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorMsg) {
//                        ToastUtils.showShortToast(errorMsg);
//                    }
//                });

//        OkHttpUtils.post()
//                .url(url)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onBefore(Request request, int id) {
//                        super.onBefore(request, id);
//                        if (progress==null){
//                            progress = new ProgressDialog(Login.this);
//                            progress.setMessage("正在登录,请等待……");
//                            progress.setCancelable(false);
//                            progress.setCanceledOnTouchOutside(false);
//                            progress.show();
//                        }else {
//                            progress.show();
//                        }
//                    }
//                    @Override
//                    public void onAfter(int id) {
//                        super.onAfter(id);
//                        if (progress.isShowing()){
//                            progress.dismiss();
//                        }
//                    }
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        e.printStackTrace();
//                        ToastUtils.show("登入超时");
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        if (response != null) {
//                            Gson gson = new Gson();
//                            // 用户属性对象
//                            UserBeanVO userBean = gson.fromJson(response, UserBeanVO.class);
//                            if (userBean != null) {
//                                if ("1".equals(userBean.getState())) {        //登入成功
//                                    // 保留当前登录用户信息
//                                    Utils.getContext().setCurUser(userBean);
//                                    if (!login_map_spinner.isShown()) {
//                                        for (Sub sub : Utils.getContext().getSolution()) {
//                                            if (sub.getGroupNum().equals(userBean.getConfigFile())) {
//                                                Utils.getContext().setDefSolution(
//                                                        sub.getId());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    // 登录成功和记住密码框为选中状态才保存用户信息
//                                    if (remUser.isChecked()) {
//                                        // 记住用户名、密码、
//                                        // Editor editor = sp.edit();
//                                        Utils.getContext().setCurUserName(
//                                                mUser.getText().toString());
//                                        Utils.getContext().setUserPassword(
//                                                mPassword.getText().toString());
//                                    }
//
//                                    Intent intent = new Intent();
//                                    intent.setClass(Login.this, Main.class);
//                                    startActivity(intent);
//                                    Login.this.finish();
//                                } else {
//                                    ToastUtils.show(userBean.getStateStr());
//                                }
//
////								else if ("2".equals(userBean.getState())) {
////									AlertDialog.Builder bd = new AlertDialog.Builder(Login.this);
////									AlertDialog ad = bd.create();
////									ad.setTitle("错误");
////									ad.setMessage("手机已绑定用户,请联系管理员是否绑定新用户");
////									ad.show();
////								} else if ("-2".equals(userBean.getState())) {
////									// -2手机挂失 -1手机失效
////									//uploadData();
////									AlertDialog.Builder bd = new AlertDialog.Builder(Login.this);
////									AlertDialog ad = bd.create();
////									ad.setTitle("错误");
////									ad.setMessage("该手机已挂失，请联系失主");
////									ad.show();
////								} else if ("-1".equals(userBean.getState())) {
////									AlertDialog.Builder bd = new AlertDialog.Builder(Login.this);
////									AlertDialog ad = bd.create();
////									ad.setTitle("错误");
////									ad.setMessage("该手机已失效，请联系管理员");
////									ad.show();
////								} else {
//////								if (response.code() == 204) {
//////									Toast.makeText(Login.this, "登录失败，机器未注册授权,请联系管理员!",
//////											Toast.LENGTH_LONG).show();
//////								} else {
////									Toast.makeText(Login.this, "登录失败，机器未注册授权或用户名密码错误",
////											Toast.LENGTH_LONG).show();
//////								}
////								}
//                            }
//                        } else {
//                            Toast.makeText(Login.this, "连接服务器失败，请检查网络是否串通",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//
    }

    private void initLocation() {
        Agps agps=new Agps();
    }

    public void login_back(View v) { // 标题栏 返回按钮
        this.finish();
        Utils.getContext().onTerminate();
        // System.exit(0);
    }

    public void login_regedit(View v) { // 用户注册按钮
        /*
         * Intent intent = new Intent(); intent.setClass(Login.this,
         * UsRgtActivity.class); startActivity(intent);
         */
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Agps.closeLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==DATA_CODE&&resultCode==RESULT_OK){
            if (data!=null){
                String string=data.getStringExtra("data");
                ToastUtils.showShortToast(string);
            }
        }
    }
}
