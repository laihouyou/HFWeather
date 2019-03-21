package com.gddst.app.rxpermissions;

import android.app.Activity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class RxPermissionsUtil {
    //请求权限
    public static void requestRxPermissions(final Activity activity, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean granted) throws Exception {
                if (granted){
                    Toast.makeText(activity, "已获取权限", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(activity, "已拒绝一个或以上权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    public static void requestEachRxPermission(final Activity activity,String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.requestEach(permissions).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(@NonNull Permission permission) throws Exception {
                if (permission.granted) {
//                    Toast.makeText(activity, "已获取权限"+ permission.name , Toast.LENGTH_SHORT).show();
                } else if (permission.shouldShowRequestPermissionRationale){
                    //拒绝权限请求
                    Toast.makeText(activity, "已拒绝权限"+ permission.name , Toast.LENGTH_SHORT).show();
                } else {
                    // 拒绝权限请求,并不再询问
                    // 可以提醒用户进入设置界面去设置权限
                    Toast.makeText(activity, "已拒绝权限"+ permission.name +"并不再询问", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
