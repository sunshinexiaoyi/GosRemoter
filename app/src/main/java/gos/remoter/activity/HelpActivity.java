package gos.remoter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.remoter.R;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_LIVE_SET_PROGRAM_LIST;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;

public class HelpActivity extends Activity implements View.OnClickListener{
    private  final String TAG = this.getClass().getSimpleName();

    private TextView versionInform;
    private TextView versionUpdate;
    private TextView versionGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        EventManager.register(this);
        initView();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg){
        if(msg.getEventMode() == EventMode.OUT)
            return;

        switch (msg.getCommand()){
            case COM_LIVE_SET_PROGRAM_LIST:

                break;

            case COM_SYSTEM_RESPOND:    //回应
                Respond respond = DataParse.getRespond(msg.getData());
                switch (respond.getCommand()){
                    case COM_CONNECT_DETACH:
                        if(respond.getFlag()){
                            detach();
                        }
                        break;
                    case COM_CONNECT_ATTACH:
                        if(respond.getFlag()){
                            //attach();
                        }
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
        }

    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.helpTitle);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        versionInform = (TextView) findViewById(R.id.versionInform);
        versionUpdate = (TextView) findViewById(R.id.versionUpdate);
        versionGuide = (TextView) findViewById(R.id.versionGuide);

        versionInform.setOnClickListener(this);
        versionUpdate.setOnClickListener(this);
        versionGuide.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.versionInform:
                break;
            case R.id.versionUpdate:
                break;
            case R.id.versionGuide:
                break;
        }
    }

    private void detach(){
        Log.i(TAG,"断开连接");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemInfo.getInstance().setState(SystemState.DETACH);
    }


}
