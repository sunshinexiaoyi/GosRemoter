package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.service.NetService;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.tool.SystemClear;

public class InitActivity extends AppCompatActivity{
    private String TAG = this.getClass().getSimpleName();
    View view;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        view = findViewById(R.id.init);
        ACTCollector.add(this);//添加到收集器
        initView();
        //沉浸式隐藏标题栏
        actionBar = getSupportActionBar();
        actionBar.hide();

        startConnectActivity();
        //finish();
    }

    @Override
    public void onDestroy() {
        ACTCollector.remove(ACTCollector.getByName(InitActivity.this));//从收集器移除
        super.onDestroy();
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

    }


    void startConnectActivity(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //start main
                Intent intent = new Intent(InitActivity.this,ConnectActivity.class);
                startActivity(intent);

                finish();
            }
        },1000);
    }
}
