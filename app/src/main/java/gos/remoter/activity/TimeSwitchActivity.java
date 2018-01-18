package gos.remoter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.remoter.R;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemApplication;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;
import static gos.remoter.define.CommandType.COM_TIME_SET_OFF;
import static gos.remoter.define.CommandType.COM_TIME_SET_ON;

public class TimeSwitchActivity extends Activity {
    private  final String TAG = this.getClass().getSimpleName();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_switch);

        EventManager.register(this);
        initView();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg){
        if(msg.getEventMode() == EventMode.OUT)
            return;

        switch (msg.getCommand()){
            case COM_TIME_SET_ON:

                break;
            case COM_TIME_SET_OFF:

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
        titleBar.setTextTitle(R.string.home_timing);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setImageRight(R.drawable.home_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        listView = (ListView) findViewById(R.id.time_list);

    }

    private void detach(){
        Log.i(TAG,"断开连接");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemApplication.getInstance().setState(SystemState.DETACH);
    }


}
