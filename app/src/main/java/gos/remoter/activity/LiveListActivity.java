package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.player.widget.PlayStateParams;
import com.player.widget.PlayerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.ProgramUrl;
import gos.remoter.data.Respond;
import gos.remoter.define.CommandType;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.view.ErrorMaskView;

import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;
import static gos.remoter.define.CommandType.COM_SYS_EXIT;
import static gos.remoter.define.CommandType.COM_SYS_HEARTBEAT_STOP;


public class LiveListActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private PlayerView mVideoView;
    private ListView listView;
    private ErrorMaskView errorMaskView = null;

    private Program curProgram = null;
    ArrayList<Program> programList;

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_SYS_HEARTBEAT_STOP:
                    //delayFinish(0, getResources().getString(R.string.heartbeat_stop));
                    detach();
                    break;
                case CommandType.COM_LIVE_SET_PROGRAM_LIST:
                    setProgramList(parseProgramData(msg.getData()));
                    startDefaultPlay();
                    break;
                case CommandType.COM_LIVE_SET_PROGRAM_URL:
                    errorMaskView.setVisibleGone();
                    ProgramUrl programUrl = JSON.parseObject(msg.getData(),ProgramUrl.class);
                    if(curProgram == null)
                        break;
                    Log.i(TAG,"getIndex:"+curProgram.getIndex());
                    if(curProgram.getIndex() == programUrl.getIndex()) {//发送的节目索引与接收的节目索引相同
                        //startPlayByUrl(programUrl.getUrl());
                        startPlay(programUrl.getUrl());
                    }
                    break;
                case CommandType.COM_LIVE_UPDATE_PROGRAM_LIST:
                    sendFinishLive();
                    getProgramList();   //节目列表更新时，重新获取节目列表
                    break;
                case CommandType.COM_SYS_FINISH_LIVE:
                    delayFinish(3000, getResources().getString(R.string.switch_freq));
                    break;
                case COM_SYSTEM_RESPOND:{    //回应
                    Respond respond = DataParse.getRespond(msg.getData());
                    switch (respond.getCommand()) {
                        case COM_CONNECT_DETACH:
                            if (respond.getFlag()) {
                                detach();
                            } else {
                                Log.i(TAG, "断开连接失败");
                            }
                            break;
                        case COM_CONNECT_ATTACH:
                            if (respond.getFlag()) {
                                attach();
                            } else {
                                Log.i(TAG, "连接设备失败");
                            }
                            break;
                        case CommandType.COM_LIVE_STOP_PROGRAM:
                            Log.i(TAG,"停止当前播放节目成功");
                            curProgram = null;
                            break;
                        case CommandType.COM_LIVE_SET_PROGRAM_URL:
                            if(!respond.getFlag()){     //获取节目url失败
                                errorMaskView.setVisibleGone();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                }
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
        setContentView(R.layout.activity_livelist);
        System.gc();
        ACTCollector.add(this);//添加到收集器
        EventManager.register(this);

        initView();
        initPlayerView();
        initData();
        initScreen();

    }

    //初始化播放器布局
    private void initPlayerView() {

        View v = findViewById(R.id.app_box);//若播放器不是一个单独的布局中，放在其他布局中，需要考虑到--"v"

        mVideoView = new PlayerView(this,v);
        mVideoView.setScaleType(PlayStateParams.fitparent);
        mVideoView.forbidTouch(false);
        mVideoView.hideMenu(true);
        mVideoView.hideRotation(true);
        mVideoView.enableOrientationEventListener();
        //mVideoView.setOnlyFullScreen(false);

    }

    /**
     * 默认播放列表的第一个
     */
    private void startDefaultPlay() {
        if(SystemInfo.getInstance().getState() == SystemState.ATTACH) {
            if(null != programList) {
                curProgram = programList.get(0);
                Log.e(TAG, "播放第一个--getIndex:"+curProgram.getIndex());
                getProgramUrl(curProgram.getIndex());

            }
        }
    }

    /**
     * 直播播放
     * @param url
     */
    private void startPlay(String url){
        /*Bundle bundle = getIntent().getExtras();
        String url = bundle.getString(MsgKey.url);
        playVideo(url);*/
        if(null != url) {
            mVideoView.setLivePlay(true);
            mVideoView.setPlaySource(url);
            mVideoView.startPlay();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mVideoView.onConfigurationChanged(newConfig);//根据播放器的状态设置播放器高度
        initScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"销毁");
        ACTCollector.remove(this);//从收集器移除
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        EventManager.unregister(this);
        if(curProgram != null) {
            stopProgram(curProgram.getIndex());
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mVideoView != null && mVideoView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    void initView(){
        //new ImmersionLayout(this).setImmersion();//隐藏状态栏

        errorMaskView = (ErrorMaskView) findViewById(R.id.maskView);
        listView = (ListView) findViewById(R.id.programList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curProgram = programList.get(position);
                Log.e(TAG, "getIndex:\n"+curProgram.getIndex());
                getProgramUrl(curProgram.getIndex());
            }
        });

        if( SystemInfo.getInstance().getState() == SystemState.DETACH){
            detach();
        }

    }

    //判断屏幕方向
    private void initScreen() {
        Log.e(TAG, this.getResources().getConfiguration().orientation + "----Screen");
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if(SystemInfo.getInstance().getState() == SystemState.ATTACH) {
                listView.setVisibility(View.VISIBLE);
                errorMaskView.setVisibility(View.GONE);
            } else if(SystemInfo.getInstance().getState() == SystemState.DETACH) {
                listView.setVisibility(View.GONE);
                errorMaskView.setVisibility(View.VISIBLE);
            }
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            listView.setVisibility(View.GONE);
            errorMaskView.setVisibility(View.GONE);
        }
    }

    private void initData(){
        if( SystemInfo.getInstance().getState() == SystemState.ATTACH) {
            getProgramList();
        }
    }

    /**
     * 获取节目列表,应答
     */
    private void getProgramList(){
        Log.i(TAG,"获取节目列表:");
        errorMaskView.setLoadingStatus();
        EventManager.send(CommandType.COM_LIVE_GET_PROGRAM_LIST,"", EventMode.OUT);
    }

    /**
     * 获取节目url
     * @param index 节目索引
     */
    private void getProgramUrl(int index){
        Log.i(TAG,"获取节目url:"+index);
        //errorMaskView.setLoadingStatus();
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(CommandType.COM_LIVE_GET_PROGRAM_URL, JSON.toJSONString(indexClass), EventMode.OUT);
    }

    /**
     * 停止节目
     */
    private void stopProgram(int index ){
        Log.e(TAG,"停止节目:"+index);
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(CommandType.COM_LIVE_STOP_PROGRAM,JSON.toJSONString(indexClass), EventMode.OUT);
    }

    /**
     * 解析节目数据，新建一个字符串数组存储节目名
     * @param data
     * @return
     */
    private String[] parseProgramData(String data){
        //Log.i(TAG,"programList:\n"+data);
        programList = DataParse.getProgramList(data);
        Log.e(TAG,"programList:\n"+ JSON.toJSONString(programList));
        String[] programs = new String[programList.size()];
        int i = 0;
        for (Program p : programList) {
            programs[i++] = p.getName();
        }
        return programs;
    }

    private void setProgramList(String[] programs)
    {
        errorMaskView.setVisibleGone();
        //创建ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_expandable_list_item_1, programs);
        //获取ListView对象，通过调用setAdapter方法为ListView设置Adapter设置适配器
        listView.setAdapter(adapter);
    }

    /**
     * 结束直播activity
     */
    private void sendFinishLive(){
        EventManager.send(CommandType.COM_SYS_FINISH_LIVE,"",EventMode.IN);
    }

    private void detach(){
        Log.i(TAG,"断开连接成功");
        //Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemInfo.getInstance().setState(SystemState.DETACH);

        setProgramList(new String[0]);  //清空节目列表
        listView.setVisibility(View.GONE);
        errorMaskView.setErrorStatus(true,R.string.jump_connect);
        errorMaskView.setOnRetry(R.string.jump,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartConnect();
            }
        });
    }

    void restartConnect(){
        Log.e(TAG,"重启连接界面");
        Intent intent = new Intent(this,ConnectActivity.class);
        startActivity(intent);
        delayFinish(500, null);
    }

    /**
     * @param delay 延时多少毫秒结束
     * @param info  打印提示信息
     */
    private void delayFinish(long delay,String info ) {
        if (delay < 0) {
            Log.e("delayFinish", "delay <0");
            return;
        }
        Log.e("delayFinish", "delay " + delay);
        if (null != info) {
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("delayFinish", "finish");
                finish();
            }
        }, delay);
    }

    private void attach(){
        getProgramList();
    }

}
