package gos.remoter.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gos.remoter.R;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

public class RemoterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remoter);
        initView();
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setAlpha(255);

        titleBar.setTextTitle(R.string.remoter_title);
        titleBar.setImageRight(R.drawable.remoter_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                //Toast.makeText(HomeActivity.this, "logout", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
