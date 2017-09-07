package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import gos.remoter.R;
import gos.remoter.service.NetService;

public class InitActivity extends Activity {
    private  final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        immersionLayout();

        //start main
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        //start NetService
        intent = new Intent(this, NetService.class);
        startService(intent);

        Log.i(TAG,"start netService");
        //finish();

    }


    void immersionLayout(){
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }
}
