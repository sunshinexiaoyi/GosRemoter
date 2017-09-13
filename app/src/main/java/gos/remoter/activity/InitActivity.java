package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.service.NetService;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

public class InitActivity extends Activity {
    private  final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        initView();
        startService();
        startConnectActivity();
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setNullBackground();
        titleBar.setTextTitle(R.string.connect_title);
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
