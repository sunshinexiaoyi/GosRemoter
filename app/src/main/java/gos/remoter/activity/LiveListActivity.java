package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.player.listener.OnShowEpgListListener;
import com.player.listener.OnShowProgramListListener;
import com.player.listener.OnShowThumbnailListener;
import com.player.widget.PlayStateParams;
import com.player.widget.PlayerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.adapter.LivePagerAdapter;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.Date;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.ProgramUrl;
import gos.remoter.data.Respond;
import gos.remoter.data.Time;
import gos.remoter.define.CommandType;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemApplication;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.DensityUtils;
import gos.remoter.view.ErrorMaskView;

import static gos.remoter.define.CommandType.*;

public class LiveListActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private final int HANDLE_HIDE_LIST = 0;
    private final int HANDLE_HIDE_EPG = 1;

    private View view;
    private PlayerView mVideoView;
    private ListView listView;
    private ViewPager viewPager;
    private LivePagerAdapter pagerAdapter;
    private ErrorMaskView errorMaskView = null;
    private PopupWindow mListPopupWindow;
    private PopupWindow mEpgPopupWindow;

    private Program curProgram = null;
    private int curPosition = -1;
    ArrayList<Program> programList = null;
    ReuseAdapter<Program> listAdapter;

    private ArrayList<Date> progDate = null;//节目日期信息
    private ArrayList<Time> progTime = null;//一个日期的节目Epg信息，包含在ExpandableTime中


    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            Log.e(TAG,"收到的命令" + msg.getCommand());
            Log.e(TAG,"收到的数据" + msg.getData());
            switch (msg.getCommand()){
                case COM_SYS_HEARTBEAT_STOP:
                    //delayFinish(0, getResources().getString(R.string.heartbeat_stop));
                    detach();
                    break;
                case CommandType.COM_LIVE_SET_PROGRAM_LIST:
//                    setProgramList(parseProgramData(msg.getData()));
                    setProgramList(msg.getData());
//                    startDefaultPlay();
                    break;
                case CommandType.COM_LIVE_SET_PROGRAM_URL:
                    errorMaskView.setVisibleGone();
                    listView.setVisibility(View.VISIBLE);
                    ProgramUrl programUrl = JSON.parseObject(msg.getData(),ProgramUrl.class);
                    if(curProgram == null) {
                        curPosition = -1;
                        break;
                    }
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
                case COM_EPG_SET_INFORM_LIST: {//收到节目epg信息
                    makeProgramData(msg.getData());
                    break;
                }
                case COM_NET_DISABLE:
                    errorMaskView.setErrorStatus(false, R.string.netError);
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
                                clearProgramData();
                                Log.i(TAG, "连接设备失败");
                            }
                            break;
                        case CommandType.COM_LIVE_STOP_PROGRAM:
                            Log.i(TAG,"停止当前播放节目成功");
                            curProgram = null;
                            curPosition = -1;
                            break;
                        case CommandType.COM_LIVE_SET_PROGRAM_URL:
                            if(!respond.getFlag()){     //获取节目url失败
                                errorMaskView.setVisibleGone();
                                listView.setVisibility(View.VISIBLE);
//                                view.setBackgroundResource(R.drawable.details_bg_window);
//                                autoPlayNext();
                                Toast.makeText(this, "播放失败，请重选节目", Toast.LENGTH_SHORT).show();
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_HIDE_LIST:
                    mListPopupWindow.dismiss();
                    break;
                case HANDLE_HIDE_EPG:
                    mEpgPopupWindow.dismiss();
            }

        }
    };

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

        view = findViewById(R.id.app_box);//若播放器不是一个单独的布局中，放在其他布局中，需要考虑到--"view"

        mVideoView = new PlayerView(this,view);
        mVideoView.setScaleType(PlayStateParams.fitparent);
        mVideoView.forbidTouch(false);
        mVideoView.hideMenu(true);
        mVideoView.hideRotation(true);
        mVideoView.enableOrientationEventListener();
        //mVideoView.setOnlyFullScreen(false);
        mVideoView.showThumbnail(new OnShowThumbnailListener() {
            @Override
            public void onShowThumbnail(ImageView ivThumbnail) {
//                Log.e(TAG, "ivThumbnail------未播放时的缩略图");
                ivThumbnail.setBackgroundResource(R.drawable.details_bg_window);
            }
        });
        mVideoView.setShowProgramListListener(new OnShowProgramListListener() {
            @Override
            public void OnShowProgramList(ImageView ivProgramList) {
                if(null != mEpgPopupWindow) {
                    mEpgPopupWindow.dismiss();
                }
                showLiveProgramList();
            }
        });
        mVideoView.setShowEpgListListener(new OnShowEpgListListener() {
            @Override
            public void OnShowEpgList(ImageView ivEpgList) {
                Log.e(TAG, "OnShowEpgList------EPG信息");
                if(null != mListPopupWindow) {
                    mListPopupWindow.dismiss();
                }
                if(null != programList && null != curProgram) {
                    /*if(null == curProgram) {
                        getSelectedEpgInfo(programList.get(0).getIndex());
                    } else {*/
                        getSelectedEpgInfo(curProgram.getIndex());
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLiveEpgList();
                    }
                }, 100L);
            }
        });

    }

    /**
     * 默认播放列表的第一个
     */
    private void startDefaultPlay() {
        if(SystemApplication.getInstance().getState() == SystemState.ATTACH) {
            if(null != programList) {
                curProgram = programList.get(0);
                Log.e(TAG, "播放第一个--getIndex:"+curProgram.getIndex());
                getProgramUrl(curProgram.getIndex());

                curPosition = 0;
                listAdapter.setSelectedId(curPosition);
            }
        }
    }

    /**
     * 获取url失败，则自动播放下一个节目
     */
    private void autoPlayNext() {
        Log.e(TAG, "当前:"+curPosition);

        if(curPosition == (programList.size() - 1)) {
            startDefaultPlay();
        } else {
            curPosition += 1;
            curProgram = programList.get(curPosition);
            getProgramUrl(curProgram.getIndex());
            listAdapter.setSelectedId(curPosition);
            Log.e(TAG, "当前---next--:"+curPosition);

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
        if(SystemState.DETACH == SystemApplication.getInstance().getState()) {//横竖屏切换后，由于配置原因按钮失效，但会直接加载此方法
            clearProgramData();
        }
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
        if(null != listAdapter) {
            listAdapter.setSelectedId(-1);
            listAdapter.notifyDataSetInvalidated();
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
                curPosition = position;
                Log.e(TAG, "getIndex:\n"+curProgram.getIndex());
                getProgramUrl(curProgram.getIndex());

                listAdapter.setSelectedId(position);
                //刷新数据
                listAdapter.notifyDataSetInvalidated();
            }
        });

        if( SystemApplication.getInstance().getState() == SystemState.DETACH){
            detach();
        }

    }

    //判断屏幕方向
    private void initScreen() {
        Log.e(TAG, this.getResources().getConfiguration().orientation + "----Screen");
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if(null != mListPopupWindow) {
                mListPopupWindow.dismiss();
            }
            if(null != mEpgPopupWindow) {
                mEpgPopupWindow.dismiss();
            }
            if(SystemApplication.getInstance().getState() == SystemState.ATTACH) {
                listView.setVisibility(View.VISIBLE);
                errorMaskView.setVisibleGone();

            } else if(SystemApplication.getInstance().getState() == SystemState.DETACH) {
                listView.setVisibility(View.GONE);
                errorMaskView.setVisibility(View.VISIBLE);
            }
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            listView.setVisibility(View.GONE);
            errorMaskView.setVisibleGone();

        }
    }

    private void initData(){
        if( SystemApplication.getInstance().getState() == SystemState.ATTACH) {
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
     * 获取当前播放节目的EPG信息
     * @param index
     */
    private void getSelectedEpgInfo(int index){
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_EPG_GET_INFORM_LIST, JSON.toJSONString(indexClass), EventMode.OUT);//获取选中的节目epg信息
    }

    /**
     * 获取节目url
     * @param index 节目索引
     */
    private void getProgramUrl(int index){
        Log.i(TAG,"获取节目url:"+index);
        listView.setVisibility(View.GONE);
        errorMaskView.setLoadingStatus();
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
     * 重写了item布局，为item设置点击响应，添加颜色变化
     * @param data
     */
    private void setProgramList(String data) {
        programList = DataParse.getProgramList(data);
        if(null == programList) {
            errorMaskView.setEmptyStatus();
        }else {
            errorMaskView.setVisibleGone();
            listAdapter = new ReuseAdapter<Program>(programList, R.layout.item_live_programlist) {
                @Override
                public void bindView(ViewHolder holder, Program obj, int position) {
                    holder.setText(R.id.liveProgramItem,obj.getName() );
                    holder.setColor(R.id.liveProgramItem);
                }
            };
            listView.setAdapter(listAdapter);

        }

    }

    /**
     * 解析得到指定界面的所有EPG信息，7天
     * @param data
     */
    private void makeProgramData(String data) {
        progDate = DataParse.getEpgProgram(data).getDateArray();//得到日期所有信息
        progTime = progDate.get(0).getTimeArray();//拿到第0个日期中的epg信息列表
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
        SystemApplication.getInstance().setState(SystemState.DETACH);
        clearProgramData();
    }

    /**
     * 清除信息
     */
    private void clearProgramData() {
//        setProgramList(new String[0]);  //清空节目列表
        listView.setAdapter(null);
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
                Log.e("delayFinish", "finish-----------------");
//                finish();//销毁当前activity
                mVideoView.stopPlay();
            }
        }, delay);
    }

    private void attach(){
        getProgramList();
    }

    /**
     * 直播节目显示
     */
    private void showLiveProgramList() {
        View view = LayoutInflater.from(this).inflate(R.layout.live_program_list, null);
        GridView gridView = (GridView) view.findViewById(R.id.live_program_list);
        TextView textView = (TextView) view.findViewById(R.id.live_programTitle_null);
        if (programList == null || programList.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
        ReuseAdapter<Program> gridAdapter = new ReuseAdapter<Program>(programList, R.layout.item_live_itemlist) {
            @Override
            public void bindView(ViewHolder holder, Program obj, int position) {
                holder.setText(R.id.liveProgramItem,obj.getName() );
                holder.setTextColor(R.id.liveProgramItem, Color.WHITE);
                holder.setTextSize(R.id.liveProgramItem, 20);
            }
        };
        gridView.setAdapter(gridAdapter);

        Log.i("getScreenHeight", "getScreenHeight " + DensityUtils.getScreenHeight(this));
        mListPopupWindow = new PopupWindow(view, DensityUtils.getScreenWidth(this) * 2 / 3, DensityUtils.getScreenHeight(this) * 3 / 5, true);//WindowManager.LayoutParams.MATCH_PARENT
        mListPopupWindow.setTouchable(true);
        mListPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mListPopupWindow.setOutsideTouchable(true);
        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.live_player, null);
        mListPopupWindow.setAnimationStyle(R.style.LiveAnim);
        mListPopupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);//若是直接加载，activity可能为空，此时需要延时,如epg获取

        if (mHandler.hasMessages(HANDLE_HIDE_LIST)) {
            mHandler.removeMessages(HANDLE_HIDE_LIST);//避免信息还存在，可选
        }
//        mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_LIST, 5000);

    }

    /**
     * EPG信息显示
     */
    private void showLiveEpgList() {
        View view = LayoutInflater.from(this).inflate(R.layout.live_epg_viewpager, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.line_epg_null);
        viewPager = (ViewPager) view.findViewById(R.id.live_epg_pager);

//        Log.e("showLiveEpgList", "progTime " + progTime.get(0).getEvent());
        if (programList == null || programList.size() == 0 || progTime == null || progTime.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
        initViewPager();

        mEpgPopupWindow = new PopupWindow(view, DensityUtils.getScreenWidth(this) * 2 / 3, DensityUtils.getScreenHeight(this) * 3 / 5, true);//DensityUtils.getScreenWidth(this) * 2 / 3
        mEpgPopupWindow.setTouchable(true);
        mEpgPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mEpgPopupWindow.setOutsideTouchable(true);
        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.live_player, null);
        mEpgPopupWindow.setAnimationStyle(R.style.LiveAnim);
        mEpgPopupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);//若是直接加载，activity可能为空，此时需要延时
    }

    private void initViewPager() {
        pagerAdapter = new LivePagerAdapter(this, progTime, curProgram);
        viewPager.setOffscreenPageLimit(4);//限制四张
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (position <= 0.0f) {//第一页,被滑动的那页
                    page.setAlpha(1.0f);
//                    Log.e("onTransform", "position <= 0.0f ==>" + position);
                    page.setTranslationY(0f);
                    //控制停止滑动切换的时候，只有最上面的一张卡片可以点击
                    page.setClickable(true);
                    page.setScaleX(0.9f);
                } else if (position <= 3.0f) {
//                    Log.e("onTransform", "position <= 3.0f ==>" + position);
                    float scale = (float) (page.getWidth() - DensityUtils.dip2px(getBaseContext(), 60 * position)) / (float) (page.getWidth());
                    //控制下面卡片的可见度
                    page.setAlpha(0.7f);
                    //控制停止滑动切换的时候，只有最上面的一张卡片可以点击
                    page.setClickable(false);
                    page.setPivotX(page.getWidth() / 2f);
                    page.setPivotY(page.getHeight() / 2f);
                    page.setScaleX((float)(0.9 * scale));
                    page.setScaleY(scale);
                    page.setTranslationX(-page.getWidth() * position + (page.getWidth() * 0.5f) * (1 - scale) + DensityUtils.dip2px(getBaseContext(), 20) * position);
//                    page.setTranslationX((-page.getWidth() * position));
                }
            }
        });
        viewPager.setAdapter(pagerAdapter);
    }

}
