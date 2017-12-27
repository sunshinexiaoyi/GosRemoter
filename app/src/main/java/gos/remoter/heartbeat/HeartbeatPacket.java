package gos.remoter.heartbeat;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 心跳包
 * Created by wuxy on 2017/7/19.
 */


public class HeartbeatPacket implements HeartbeatStop {
    private final  String TAG = this.getClass().getSimpleName();

    private HeartbeatStop heartbeatStop = null; //心跳停止回调事件

    private final int setInterval;  //设置的间隔时间 s
    private int curInterval = 0;    //当前的间隔时间 s

    private Timer timer;

    /**
     * @param heartbeatStop 心跳停止回调事件
     * @param setInterval   设置的间隔时间s
     */
    public HeartbeatPacket(HeartbeatStop heartbeatStop, int setInterval)
    {
        this.heartbeatStop = heartbeatStop;
        this.curInterval = setInterval;
        this.setInterval = this.curInterval;
    }

    @Override
    public void callStopFun() {
        if(null != heartbeatStop)
        {
            heartbeatStop.callStopFun();
        }
    }

    /**
     * 重置当前间隔时间
     */
    public void recover()
    {
        synchronized (this){
            curInterval = setInterval;
        }
    }

    /**
     * 1s循环运行
     */
    private  void run(){
        if(null == timer){
            Log.e(TAG,"null == timer");
            return;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (this){
                    if((curInterval--)<0)
                    {
                        callStopFun();
                        recover();
                        stop();
                    }
                }
            }
        }, 0,1000);
    }

    /**
     * 重新开始
     */
    public void start(){
        recover();
        timer = new Timer() ;
        run();
    }

    public void stop(){
        timer.cancel();
        timer = null;
    }
}