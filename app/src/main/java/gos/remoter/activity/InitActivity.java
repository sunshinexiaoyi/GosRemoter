package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import gos.remoter.R;
import gos.remoter.service.NetService;

public class InitActivity extends Activity {
    private  final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //start main
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        //start NetService
        intent = new Intent(this, NetService.class);
        startService(intent);

        Log.i(TAG,"start netService");
        finish();
    }
}
