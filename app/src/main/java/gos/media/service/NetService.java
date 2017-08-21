package gos.media.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import gos.media.data.Respond;
import gos.media.define.*;
import gos.media.event.EventManager;
import gos.media.event.EventMode;
import gos.media.event.EventMsg;
import gos.media.heartbeat.HeartbeatPacket;
import gos.media.heartbeat.HeartbeatStop;

import static gos.media.define.CommandType.*;   //导入静态命令集

public class NetService extends Service {
    private  final String TAG = this.getClass().getSimpleName();
    public static NetProtocol.UdpUnicastSocket netSender = null;
    public static NetProtocol.UdpUnicastSocket netReceiver = null;

    private Timer wifiTimer = null;
    private Timer serverTimer = null;

    private String localIP;
    private String deviceIP;

    private HeartbeatPacket heartbeatPacket = null;
    private int heartbeatInterval = 1000; //设置心跳包超时时间为10s

    public NetService() {
    }

    /**
     * 接收内部模块事件
     * udp发送给盒子端
     * @param msg    接收的信息
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.OUT == msg.getEventMode()){//对外
            DataPackage dataPackage = new DataPackage();
            dataPackage.command = msg.getCommand();
            dataPackage.setData(msg.getData());
            Log.i(TAG,"发送命令:"+dataPackage.getCommand());
            Log.i(TAG,"发送数据:"+new String(dataPackage.getData()));

            switch (dataPackage.getCommand())
            {
                case COM_CONNECT_GET_DEVICE:    //获取设备，发送udp广播
                    Log.i(TAG,"command:"+"获取设备，发送udp广播");
                    try {
                        NetProtocol.UdpUnicastSocket broadSender = new NetProtocol.UdpUnicastSocket("255.255.255.255", NetProtocol.sendPort,NetProtocol.SocketType.SEND);
                        broadSender.send(dataPackage.toByte());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    if (null != netSender) {
                        try{
                            netSender.send(dataPackage.toByte());
                        }catch (Exception e){e.printStackTrace();}
                    }
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventManager.register(this);//订阅
        initHeartbeatPacket();
        initNetServer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);//取消订阅
    }

    void initNetServer(){
        initWifi();
    }

    void initWifi (){
        wifiTimer = new Timer();

        wifiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                WifiManager wifiMng = (WifiManager)(getApplicationContext().getSystemService(Context.WIFI_SERVICE));
                int wifiState = wifiMng.getWifiState();

                switch (wifiState){
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.i(TAG,"wifi 已经打开");
                        WifiInfo wifiInfo = wifiMng.getConnectionInfo();
                        localIP = getLocalIP(wifiInfo);
                        if(null != localIP){
                            wifiTimer.cancel();
                            startServer();
                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.i(TAG,"wifi 未开启!尝试启动");
                        wifiMng.setWifiEnabled(true);   //启动wifi
                        break;
                    default:
                        break;
                }
            }
        },0,1000);
    }


    private void initHeartbeatPacket(){
        heartbeatPacket = new HeartbeatPacket(new HeartbeatStop(){
            @Override
            public void callStopFun() {
                sendHeartbeatStop();
            }
        },heartbeatInterval);
    }

    void sendHeartbeatStop(){
        Log.i(TAG,"sendHeartbeat");
        EventManager.send(COM_SYS_HEARTBEAT_STOP,"", EventMode.IN);
    }

    void startServer(){
        try {
            netReceiver = new NetProtocol.UdpUnicastSocket(localIP,NetProtocol.receivePort,NetProtocol.SocketType.RECEIVE);
            Log.i(TAG,"本地IP:"+netReceiver.getAddress());
            Log.i(TAG,"本地port:"+netReceiver.getPort());
            serverTimer = new Timer();
            serverTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    byte[] recvData = netReceiver.receive();
                    try {
                       // Log.i(TAG,DataPackage.getFormatStr(recvData));
                        DataPackage dataPackage = new DataPackage(recvData);
                        parseDataPackage(dataPackage);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },0,500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 解析包数据
     * @param dataPackage
     */
    private void parseDataPackage(DataPackage dataPackage){
        Log.i(TAG,"接收命令:"+dataPackage.getCommand());
        if(COM_SYSTEM_RESPOND == dataPackage.getCommand())//回应
        {
            Respond respond = DataParse.getRespond(dataPackage.data);
            switch (respond.getCommand()){
                case COM_CONNECT_DETACH:
                    heartbeatPacket.stop();
                    break;
                case COM_LIVE_STOP_PROGRAM:
                    break;
                case COM_CONNECT_ATTACH:
                    Log.i(TAG,"连接设备");
                    //开启心跳包
                    heartbeatPacket.start();
                    break;
                default:
                    break;
            }
            postDataPackage(dataPackage, EventMode.IN);
        } else if(COM_SYSTEM_HEARTBEAT_PACKET == dataPackage.getCommand()){//心跳包
            heartbeatPacket.recover();
            sendRespond(dataPackage);
        }else{
            postDataPackage(dataPackage, EventMode.IN);
            sendRespond(dataPackage);
        }
    }

    /**
     * 转发给其他模块
     * @param dataPackage
     */
    private void postDataPackage(DataPackage dataPackage,EventMode eventMode){
            EventManager.send(dataPackage.getCommand(),dataPackage.getData(), eventMode);
    }

    /**
     * 发送回应
     * @param dataPackage
     */
    private void sendRespond(DataPackage dataPackage){

        Respond respond = new Respond(dataPackage.getCommand(),true);
        EventManager.send(COM_SYSTEM_RESPOND, JSON.toJSONString(respond), EventMode.OUT);
    }

    private String getLocalIP( WifiInfo wifiInfo) {
        int ipAddress = wifiInfo.getIpAddress();
        if (0 == ipAddress) {
            return null;
        }
        return  ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

}
