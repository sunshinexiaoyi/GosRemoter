package gos.remoter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.tool.LanguageUtil;

public class InitActivity extends AppCompatActivity{
    private String TAG = this.getClass().getSimpleName();
    View view;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        ACTCollector.add(this);//添加到收集器

        initLanguage();
        initView();
        startConnectActivity();
    }

    @Override
    public void onDestroy() {
        ACTCollector.remove(this);//从收集器移除
        super.onDestroy();
    }

    // 系统的设置会导致Configuration 的locale 属性发生变化，进行判断
    private void initLanguage() {
        if(!LanguageUtil.isSetValue()) {
            LanguageUtil.resetDefaultLanguage();
        }
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        view = findViewById(R.id.init);
        //沉浸式隐藏标题栏
        actionBar = getSupportActionBar();
        actionBar.hide();
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
        },2000);
    }
}
