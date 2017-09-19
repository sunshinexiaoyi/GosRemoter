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

        startService();
        startConnectActivity();
        //finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ACTCollector.remove(ACTCollector.getByName(InitActivity.this));//从收集器移除
        setContentView(R.layout.activity_base);
        Log.e("消息", "InitACT死掉了");
        BitmapDrawable bd = (BitmapDrawable)view.getBackground();
        view.setBackgroundResource(0);
        bd.setCallback(null);
        bd.getBitmap().recycle();;
        TAG = null;
        actionBar = null;
        view = null;
        bd = null;
        System.gc();
        Log.e("消息", "回收完成111111");
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

    }

    void startService(){
        //start NetService
        Intent intent = new Intent(this, NetService.class);
        startService(intent);
    }

    void startConnectActivity(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //start main
                Intent intent = new Intent(InitActivity.this,ConnectActivity.class);
                startActivity(intent);

                Log.i(TAG,"start netService");
                finish();
            }
        },1000);
    }
}
