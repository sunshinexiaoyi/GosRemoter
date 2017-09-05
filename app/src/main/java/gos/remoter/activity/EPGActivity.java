package gos.remoter.activity;

import gos.remoter.EPG_toast;
import gos.remoter.define.CS;//静态常量
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import gos.remoter.R;

public class EPGActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        Toolbar epg_toolbar = (Toolbar) findViewById(R.id.epg_TOOLBAR);
        setSupportActionBar(epg_toolbar);//用Toolbar代替ActionBar

        //要时刻检测连接是否断开
        //获取节目信息
    }

    @Override
    public void onDestroy() {
        Log.e(CS.EPG_TAG, CS.EPG_ONDESTROY);//流程顺序索引
        super.onDestroy();
    }
}
