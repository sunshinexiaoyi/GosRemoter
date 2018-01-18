package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import gos.remoter.R;
import gos.remoter.define.SystemApplication;
import gos.remoter.enumkey.SystemState;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

public class SettingActivity extends Activity implements View.OnClickListener{
    private  final String TAG = this.getClass().getSimpleName();
    private final static int SETTING_SELECTED_LANGUAGE = 2;

    private TextView setLanguage;
    private TextView selectedLanguage;
    private TextView versionInform;
    private TextView parentLock;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedLanguage.setText(language);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

        language = getResources().getString(R.string.lanSystem);
        selectedLanguage.setText(language);
        setLanguage.setOnClickListener(this);
        versionInform.setOnClickListener(this);
        parentLock.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setLanguage:
                //调用系统的语言设置
                Intent intentLanguage = new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intentLanguage);
                //jumpToLanguage();

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
        Log.i(TAG,"jumpToScan");
        Intent intent = new Intent();
        intent.setClass(this,SettingLanguage.class);
        startActivityForResult(intent, SETTING_SELECTED_LANGUAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2) { //默认为0
            if(null != data) {
                language = data.getStringExtra("language");
                Log.e(TAG, "已设置的语言--" + language);
            }
            Log.e(TAG, "data 为空--" + language);
            selectedLanguage.setText(language);
        } else {
            Log.e(TAG, "data 为空, 请求码问题--");

        }
    }

    private void detach(){
        Log.i(TAG,"断开连接");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemApplication.getInstance().setState(SystemState.DETACH);
    }


}
