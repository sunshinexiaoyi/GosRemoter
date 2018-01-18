package gos.remoter.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

public class SettingVersionInform extends Activity implements View.OnClickListener{
    private  final String TAG = this.getClass().getSimpleName();

    private TextView setVersion;
    private TextView checkVersion;
    private TextView copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_version);

        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.versionInform);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setVersion = (TextView) findViewById(R.id.setVersion);
        checkVersion = (TextView) findViewById(R.id.checkVersion);
        copyright = (TextView) findViewById(R.id.copyright);

        checkVersion.setOnClickListener(this);

    }

    private void initData() {
        getPackageVersionName();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkVersion:
                Toast.makeText(this, R.string.checkVersionResult, Toast.LENGTH_SHORT).show();

                break;

        }
    }

    /**
     * 获取软件版本名称
     */
    public void getPackageVersionName() {
        PackageManager manager = getBaseContext().getPackageManager();
        String versionName = "";
        try {
            PackageInfo info = manager.getPackageInfo(getBaseContext().getPackageName(), 0);
            versionName = info.versionName;//info.versionCode
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("versionName-------", versionName);
        setVersion.setText( getResources().getString(R.string.app_name) + " V " + versionName);
    }

    private void detach(){
        Log.i(TAG,"断开连接");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemApplication.getInstance().setState(SystemState.DETACH);
    }


}
