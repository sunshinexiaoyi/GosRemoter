package gos.remoter.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.data.Respond;
import gos.remoter.define.*;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.heartbeat.HeartbeatPacket;
import gos.remoter.heartbeat.HeartbeatStop;

import static gos.remoter.define.CommandType.*;   //导入静态命令集

public class NetService extends Service {
    private  final String TAG = this.getClass().getSimpleName();
    public static NetProtocol.UdpUnicastSocket netSender = null;
    public static NetProtocol.UdpUnicastSocket netReceiver = null;


    private HeartbeatPacket heartbeatPacket = null;
    private int heartbeatInterval = 1000; //设置心跳包超时时间为10s

    private boolean receiveFlag = true;// 接收运行标志

    private BroadcastReceiver wifiChangedReceiver = new BroadcastReceiver(){//wifi改变接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            Log.e("WIFI状态", "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_DISABLED");
                    enableWifi();
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_DISABLING");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:

                    startReceiver();

                    Log.e("WIFI状态", "wifi 已经打开");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_ENABLING");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Log.e("WIFI状态", "wifiState:WIFI_STATE_UNKNOWN");
                    break;
                default:
                    break;
            }

        }
    };


    public NetService() {
    }


    /**
     * 接收内部模块事件
     * udp发送给盒子端
     * @param msg    接收的信息
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onRecviveEvent(EventMsg msg){
        Log.i(TAG,"发送模式:"+msg.getEventMode());
        Log.i(TAG,"发送命令:"+msg.getCommand());
        Log.i(TAG,"发送数据:"+new String(msg.getData()));

        if(EventMode.OUT == msg.getEventMode()){//对外
            DataPackage dataPackage = new DataPackage();
            dataPackage.setCommand( msg.getCommand());
            dataPackage.setData(msg.getData());

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
        }else if(EventMode.IN == msg.getEventMode()){//对内
            switch (msg.getCommand()){
                case COM_SYS_EXIT://退出
                    destroyNetService();
                    break;
                default:
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
        registerWifiReceiver();
        initHeartbeatPacket();
        initNetServer();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"网络服务销毁");
        super.onDestroy();
        EventManager.unregister(this);//取消订阅
        unregisterWifiReceiver();

    }

    void initNetServer(){
        //initWifi();
    }

    private void registerWifiReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiChangedReceiver,filter);
    }

    private void unregisterWifiReceiver(){
        unregisterReceiver(wifiChangedReceiver);
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


    /*网络部分*/
    void startReceiver(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    netReceiver = new NetProtocol.UdpUnicastSocket(getWifiIp(),NetProtocol.receivePort,NetProtocol.SocketType.RECEIVE);
                    Log.i(TAG,"本地IP:"+netReceiver.getAddress());
                    Log.i(TAG,"本地port:"+netReceiver.getPort());
                    sendNetEnable();

                    while (receiveFlag){
                        DataPackage dataPackage = netReceiver.receivePackage();
                        if(null != dataPackage){
                            parseDataPackage(dataPackage);
                        }else {
                            Log.e(TAG,"null != dataPackage");
                        }
                    }

                }catch (UnknownHostException e){
                    e.printStackTrace();
                }catch (SocketException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                    stopReceiver();
                }finally {
                    netReceiver.close();
                    Log.e(TAG,"接收线程finally");
                }
            }
        }).start();

    }

    private void enableWifi(){
        WifiManager wifiMng = (WifiManager)(getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        wifiMng.setWifiEnabled(true);   //启动wifi
        Log.i("wifi","启动wifi");
    }

    /**
     * 获取wifi ip
     * @return
     */
    private String getWifiIp(){
        WifiManager wifiMng = (WifiManager) (getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        WifiInfo wifiInfo = wifiMng.getConnectionInfo();
        return getLocalIP(wifiInfo);
    }


    private String getLocalIP( WifiInfo wifiInfo) {
        int ipAddress = wifiInfo.getIpAddress();
        if (0 == ipAddress) {
            return null;
        }
        return  ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

    private void stopReceiver(){
        receiveFlag = false;
        Log.e(TAG,"停止网络接收线程");
    }

    private void destroyNetService(){
        Log.i(TAG,"destroyNetService");
        stopReceiver();

        stopSelf();
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
                    heartbeatPacket.start();//开启心跳包
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

    private void sendNetEnable(){
        EventManager.send(COM_NET_ENABLE,"", EventMode.IN);
    }
    private void sendNetDisable(){
        EventManager.send(COM_NET_DISABLE,"", EventMode.IN);
    }
}

