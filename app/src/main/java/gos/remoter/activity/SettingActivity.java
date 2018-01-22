package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.remoter.R;
import gos.remoter.define.CommandType;
import gos.remoter.define.SystemApplication;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.tool.LanguageUtil;
import gos.remoter.tool.SharedPreferencesUtils;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.activity.SettingLanguage.SETTING_LANGUAGE_SELECTED;

public class SettingActivity extends Activity implements View.OnClickListener{
    private  final String TAG = this.getClass().getSimpleName();

    private TextView setLanguage;
    private TextView selectedLanguage;
    private TextView versionInform;
    private TextView parentLock;
    private String language;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg) {
        if (msg.getEventMode() == EventMode.IN) {
            switch (msg.getCommand()) {
                case CommandType.COM_SET_SYSTEM_LANGUAGE:
                    LanguageUtil.resetDefaultLanguage();
                    Log.e(TAG, "configuration--" + LanguageUtil.getSetLocale().getDisplayLanguage());

                    recreate();//刷新界面
                    break;

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ACTCollector.add(this);
        EventManager.register(this);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*  LanguageUtil.resetDefaultLanguage();
        Log.e(TAG, "configuration--" + LanguageUtil.getSetLocale().getDisplayName());

      setContentView(R.layout.activity_setting);
        initView();*/

        language = SharedPreferencesUtils.get(SETTING_LANGUAGE_SELECTED, getResources().getString(R.string.lanSystem));
        if(selectedLanguage != null) {
            if(language.equals("Auto") || language.equals("跟随系统")) {
                selectedLanguage.setText(getResources().getString(R.string.lanSystem));
            }else {
                selectedLanguage.setText(language);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ACTCollector.remove(this);
        EventManager.unregister(this);
        Log.i(TAG,"销毁---------");

    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.home_setting);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setLanguage = (TextView) findViewById(R.id.setLanguage);
        selectedLanguage = (TextView) findViewById(R.id.selectedLanguage);
        versionInform = (TextView) findViewById(R.id.versionInform);
        parentLock = (TextView) findViewById(R.id.parentLock);

        language = SharedPreferencesUtils.get(SETTING_LANGUAGE_SELECTED, getResources().getString(R.string.lanSystem));
        if(language.equals("Auto") || language.equals("跟随系统")) {
            selectedLanguage.setText(getResources().getString(R.string.lanSystem));
        } else {
            selectedLanguage.setText(language);
        }
        setLanguage.setOnClickListener(this);
        versionInform.setOnClickListener(this);
        parentLock.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setLanguage:
                /*//调用系统的语言设置
                Intent intentLanguage = new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intentLanguage);*/
                jumpToLanguage();

                break;
            case R.id.versionInform:
                Intent intent =new Intent(this, SettingVersionInform.class);
                startActivity(intent);
                break;
            case R.id.parentLock:
                break;
        }
    }

    private void jumpToLanguage(){
        Log.i(TAG,"jumpToSettingLanguage");
        Intent intent = new Intent();
        /*intent.setClass(this,SettingLanguage.class);
        startActivityForResult(intent, SETTING_SELECTED_LANGUAGE);*/
        intent.setClass(SettingActivity.this, SettingLanguage.class);
        startActivity(intent);
    }

    private void detach(){
        Log.i(TAG,"断开连接");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemApplication.getInstance().setState(SystemState.DETACH);
    }


}
