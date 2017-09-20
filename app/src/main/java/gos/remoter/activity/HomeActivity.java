package gos.remoter.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.GridActivity;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;


public class HomeActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    AlertDialog logoutAlert;
    long firstTime;//保存第一次按退出键的时间

    ReuseAdapter<GridActivity> gridAdapter = new ReuseAdapter<GridActivity>(R.layout.item_home_grid) {
        @Override
        public void bindView(ViewHolder holder, GridActivity obj) {

            holder.setText(R.id.txt_icon,getResources().getString(obj.getName()) );
            holder.setImageResource(R.id.img_icon,obj.getIcon());
        }
    };

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_SYSTEM_RESPOND:    //回应
                    Respond respond = DataParse.getRespond(msg.getData());

                    break;
                case COM_SYS_EXIT:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.gc();
        ACTCollector.add(this);//添加到收集器
        EventManager.register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"销毁");
        sendDetachDevice();


        ACTCollector.remove(ACTCollector.getByName(this));//从收集器移除
        if(ACTCollector.isEmpty()){
            sendExitSystem();
        }


        EventManager.unregister(this);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if(System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, R.string.exit2, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                exitSystem();
            }
            return true; //不返回，一次就立马退出，
        }
        return super.onKeyDown(keyCode, event);
    }


    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setNullBackground();

        titleBar.setTextTitle(R.string.connect_title);
        titleBar.setImageRight(R.drawable.home_logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                logoutAlert();
            }
        });

        gridAdapter.add(new GridActivity(RemoterActivity.class,R.drawable.home_micontro,R.string.home_remoter));
        gridAdapter.add(new GridActivity(ProgramActivity.class,R.drawable.home_programlist,R.string.home_program_list));
        gridAdapter.add(new GridActivity(EpgActivity.class,R.drawable.home_epg,R.string.home_epg));
        gridAdapter.add(new GridActivity(LiveActivity.class,R.drawable.home_live,R.string.home_live));
        gridAdapter.add(new GridActivity(null,R.drawable.home_more,R.string.home_more));


        GridView gridView = (GridView)findViewById(R.id.gridActivity);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridActivity selectActivity = gridAdapter.getItem(position);
                if(selectActivity.getActivity()==null){
                    String info =  getResources().getString(R.string.home_info);
                    Toast.makeText(HomeActivity.this, info, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(HomeActivity.this,selectActivity.getActivity());
                startActivity(intent);
            }
        } );

    }

    void restartConnect(){
        Log.e(TAG,"重启连接界面");
        Intent intent = new Intent(this,ConnectActivity.class);
        startActivity(intent);
        delayFinish();
    }


    /**
     * 延时500 毫秒关闭
     */
    void delayFinish(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();

            }
        },500);
    }

    void initLogoutAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        logoutAlert = builder.setIcon(R.drawable.home_logout_black)
                .setTitle(R.string.homeTitle)
                .setMessage(R.string.home_alert_msg)
                .setNegativeButton(R.string.home_alert_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutAlert.dismiss();
                    }
                })
                .setPositiveButton(R.string.home_alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDetachDevice();
                        restartConnect();
                    }
                }).create();
    }

    void logoutAlert(){
        if(null == logoutAlert){
            initLogoutAlert();
        }

        logoutAlert.show();
    }


    //断开与服务器的连接
    private void sendDetachDevice(){
        if(SystemState.ATTACH == SystemInfo.getInstance().getState()) {
            Log.e(TAG,"发送断开与服务器的连接");
            SystemInfo.getInstance().setState(SystemState.DETACH);

            EventManager.send(COM_CONNECT_DETACH, "", EventMode.OUT);
        }
    }


    void exitSystem(){
        if(SystemState.ATTACH == SystemInfo.getInstance().getState()) {
            sendDetachDevice();
        }
        sendExitSystem();
    }

    /**
     * 退出系统
     */
    void sendExitSystem(){
        if(SystemState.EXIT != SystemInfo.getInstance().getState()) {
            Log.e(TAG,"发送退出系统");
            SystemInfo.getInstance().setState(SystemState.EXIT);
            EventManager.send(COM_SYS_EXIT,"",EventMode.IN);
        }
    }

}
