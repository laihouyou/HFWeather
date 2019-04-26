package com.gddst.app.assetmanagement;

import android.Manifest;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gddst.app.lib_common.base.BaseActivity;
import com.gddst.app.lib_common.widgets.QRCodeActivity;
import com.gddst.app.rxpermissions.RxPermissionsUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends BaseActivity {
    private static final  int CODE=52514;

    @Override
    protected int BindLayout() {
        return R.layout.activity_main_app;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RxPermissionsUtil.requestEachRxPermission(
                this,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.VIBRATE
        );
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //天气
                ARouter.getInstance().build("/lhy/weather").navigation();
            }
        });

        TextView tv_main=findViewById(R.id.tv_main);
        tv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //二维码
                Intent intent=new Intent(MainActivity.this, QRCodeActivity.class);
                startActivityForResult(intent,CODE);
            }
        });
    }

    @Override
    protected void initData() {
//        Agps agps=new Agps(this);
    }

    @Override
    protected void onMessageEvent(Object event) {
        super.onMessageEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODE&&resultCode==RESULT_OK){
            if (data!=null){
                String rest=data.getStringExtra(QRCodeActivity.DATA);
                Toast.makeText(this,"扫描的二维码为"+rest,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
