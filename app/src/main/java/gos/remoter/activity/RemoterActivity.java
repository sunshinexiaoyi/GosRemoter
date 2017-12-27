package gos.remoter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.adapter.RemoterSetting;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Respond;
import gos.remoter.define.CommandType;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.R.id.remoteSet;
import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;
import static gos.remoter.define.CommandType.COM_SYS_EXIT;
import static gos.remoter.define.CommandType.COM_SYS_HEARTBEAT_STOP;
import static gos.remoter.define.KeyValue.KEYVALUE_0;
import static gos.remoter.define.KeyValue.KEYVALUE_1;
import static gos.remoter.define.KeyValue.KEYVALUE_2;
import static gos.remoter.define.KeyValue.KEYVALUE_3;
import static gos.remoter.define.KeyValue.KEYVALUE_4;
import static gos.remoter.define.KeyValue.KEYVALUE_5;
import static gos.remoter.define.KeyValue.KEYVALUE_6;
import static gos.remoter.define.KeyValue.KEYVALUE_7;
import static gos.remoter.define.KeyValue.KEYVALUE_8;
import static gos.remoter.define.KeyValue.KEYVALUE_9;
import static gos.remoter.define.KeyValue.KEYVALUE_BACK;
import static gos.remoter.define.KeyValue.KEYVALUE_DOWN;
import static gos.remoter.define.KeyValue.KEYVALUE_EXIT;
import static gos.remoter.define.KeyValue.KEYVALUE_FAV;
import static gos.remoter.define.KeyValue.KEYVALUE_FUNC1;
import static gos.remoter.define.KeyValue.KEYVALUE_LEFT;
import static gos.remoter.define.KeyValue.KEYVALUE_MENUE;
import static gos.remoter.define.KeyValue.KEYVALUE_MUTE;
import static gos.remoter.define.KeyValue.KEYVALUE_OK;
import static gos.remoter.define.KeyValue.KEYVALUE_POWER;
import static gos.remoter.define.KeyValue.KEYVALUE_PVR;
import static gos.remoter.define.KeyValue.KEYVALUE_RIGHT;
import static gos.remoter.define.KeyValue.KEYVALUE_UP;

public class RemoterActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    private Button remoteNumber;
    private TitleBarNew titleBar;
    private RemoterSetting remoterSet;

    private View viewNumber;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;

    public float density;// 屏幕密度（0.75 / 1.0 / 1.5 / 2.0）
    public int densityDpi;// 屏幕密度DPI（120 / 160 / 240 / 320）
    private Timer timer = null;

    HashMap<Integer,Integer> keysMap = new HashMap();
    private boolean isLongKey = false;  //是否长按
    private enum KeyStatus {
        NORMAL, //正常调台
        LONG,  // 长按
        UP    //取消长按
    }

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_SYS_HEARTBEAT_STOP:
                    detach();
                    break;
                case CommandType.COM_SYS_REMOTE_ID:
                    IndexClass IndexClass = DataParse.getIndexClass(msg.getData());
                    sendRemoteKey(IndexClass.getIndex(),KeyStatus.NORMAL);
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
                                //attach();
                            } else {
                                Log.i(TAG, "连接设备失败");
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
        setContentView(R.layout.activity_remoter);

        initLayout();
        ACTCollector.add(this);//添加到收集器
        EventManager.register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ACTCollector.remove(this);//从收集器移除
        EventManager.unregister(this);
    }

    /*@Override
    public boolean onLongClick(View view) {
        if (sendRemoteKey(view.getId(), KeyStatus.LONG)) {
            isLongKey = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            if(isLongKey) {
                isLongKey = false;
                if (sendRemoteKey(view.getId(), KeyStatus.UP)) {
                    return true;
                }
            }
        }
        return false;
    }*/

    void initLayout(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setAlpha(255);

        titleBar.setTextTitle(R.string.remoter_title);
        titleBar.setImageLeft(R.drawable.activity_return, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setImageRight(R.drawable.remoter_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                Toast.makeText(RemoterActivity.this, "waiting...", Toast.LENGTH_SHORT).show();

            }
        });

        initCustomLayout();
        remoteNumber = (Button) findViewById(R.id.remoteNumber);
        remoteNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        initNumberLayout();
        initViewValue();

    }

    /**
     * 方向键,
     */
    private void initCustomLayout() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;//屏幕宽度（像素）
        int displayHeight = displayMetrics.heightPixels;
        density = displayMetrics.density; // 屏幕密度
        densityDpi = displayMetrics.densityDpi;
        Log.e(TAG, displayWidth + "--" + displayHeight + "宽高");
        Log.e(TAG, density + "--" + densityDpi + "密度");

        remoterSet = (RemoterSetting) findViewById(remoteSet);
        remoterSet.setKeyValue(KEYVALUE_UP, KEYVALUE_DOWN, KEYVALUE_LEFT, KEYVALUE_RIGHT, KEYVALUE_OK);
        remoterSet.setOnTouchListener(new RemoterSetting.onTouchListener() {
            @Override
            public void click(int keyValue) {
                sendKeyValue(keyValue, KeyStatus.NORMAL);
            }

            @Override
            public void longClick(final int keyValue) {
                /*runOnUiThread(new Runnable() {//更新到ui线程
                    @Override
                    public void run() {
                        sendKeyValue(keyValue, KeyStatus.LONG);
                    }
                });*/
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.e(TAG, "longClick 长按");
                        sendKeyValue(keyValue, KeyStatus.NORMAL);
                    }
                }, 800, 500);
            }

            @Override
            public void cancelLong(int keyValue) {
//                sendKeyValue(keyValue, KeyStatus.UP);
                timer.cancel();
                Log.e(TAG, "cancelLong 取消长按");
            }
        });
    }

    private void initNumberLayout() {
        builder = new AlertDialog.Builder(this);
        viewNumber = LayoutInflater.from(this).inflate(R.layout.remoter_number_dialog, null, false);
        builder.setView(viewNumber);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void initViewValue() {

        keysMap.put(R.id.remoteBack, KEYVALUE_EXIT);
        keysMap.put(R.id.remoteOnOff, KEYVALUE_POWER);
        keysMap.put(R.id.remoteMute, KEYVALUE_MUTE);

        keysMap.put(R.id.remoteMenu, KEYVALUE_MENUE);
        keysMap.put(R.id.remoteFav, KEYVALUE_FAV);
        keysMap.put(R.id.remotePvr, KEYVALUE_PVR);
        keysMap.put(R.id.remoteExit, KEYVALUE_EXIT);
        //keysMap.put(R.id.info, KEYVALUE_INFO);

        //设置popupWindow/dialog里的按钮的事件
        keysMap.put(R.id.numberOne, KEYVALUE_1);
        keysMap.put(R.id.numberTwo, KEYVALUE_2);
        keysMap.put(R.id.numberThree, KEYVALUE_3);
        keysMap.put(R.id.numberFour, KEYVALUE_4);
        keysMap.put(R.id.numberFive, KEYVALUE_5);
        keysMap.put(R.id.numberSix, KEYVALUE_6);
        keysMap.put(R.id.numberSeven, KEYVALUE_7);
        keysMap.put(R.id.numberEight, KEYVALUE_8);
        keysMap.put(R.id.numberNine, KEYVALUE_9);
        keysMap.put(R.id.numberBack, KEYVALUE_BACK);//回看
        keysMap.put(R.id.numberZero, KEYVALUE_0);
        keysMap.put(R.id.numberTv, KEYVALUE_FUNC1);

    }

    /**
     * 判断id对应键值是否存在，Y：发送遥控器键值
     * @param id
     * @param keyStatus
     * @return
     */
    public boolean sendRemoteKey(int id, KeyStatus keyStatus) {
        int keyValue = -1;
        Integer key = new Integer(id);
        if(keysMap.containsKey(key))
        {
            keyValue = keysMap.get(key);
        }
        if (-1 != keyValue) {
            sendKeyValue(keyValue, keyStatus);
        }
        return false;
    }

    public boolean sendKeyValue(int keyValue, KeyStatus keyStatus) {
//        Log.i("fragment_remote","keyValue:" + keyValue);
        IndexClass indexClass = new IndexClass(keyValue);
        if(keyStatus == KeyStatus.LONG) {
            EventManager.send(CommandType.COM_REMOTE_SET_LONG_KEY, JSON.toJSONString(indexClass), EventMode.OUT);
        }else if(keyStatus == KeyStatus.UP){
            EventManager.send(CommandType.COM_REMOTE_SET_KEY_UP, JSON.toJSONString(indexClass), EventMode.OUT);
        }else{
            EventManager.send(CommandType.COM_REMOTE_SET_KEY, JSON.toJSONString(indexClass), EventMode.OUT);
        }

        return true;
    }

    // PopupWindow(悬浮框)
    private void initPopWindows(View view) {
        viewNumber = LayoutInflater.from(this).inflate(R.layout.remoter_number_dialog, null, false);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(viewNumber,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //为了点击非PopupWindow区域，PopupWindow会消失
        //避免无论按多少次后退键，PopupWindow都不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return false;// 返回true，touch事件将被拦截，拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x6f000000));
        //设置popupWindow显示的位置，参数依次是参照View，相对于父控件的位置,x轴的偏移量，y轴的偏移量
        popWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
       // popWindow.dismiss();
    }

    private void detach(){
        Log.i(TAG,"断开连接成功");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemInfo.getInstance().setState(SystemState.DETACH);
    }

	//查找对应id，
    public void remoterFuncOnClick(View source) {
        Log.i("remoterFuncOnClick","onclick.............");
        IndexClass indexClass = new IndexClass(source.getId());
        EventManager.send(CommandType.COM_SYS_REMOTE_ID, JSON.toJSONString(indexClass),EventMode.IN);
    }

}
