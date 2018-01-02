package gos.remoter.service;

import android.app.Service;
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

import gos.remoter.data.Device;
import gos.remoter.data.Respond;
import gos.remoter.define.CommandType;
import gos.remoter.define.DataPackage;
import gos.remoter.define.DataParse;
import gos.remoter.define.NetProtocol;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.exception.DataPackageException;
import gos.remoter.heartbeat.HeartbeatPacket;
import gos.remoter.heartbeat.HeartbeatStop;

import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_CONNECT_GET_DEVICE;
import static gos.remoter.define.CommandType.COM_LIVE_STOP_PROGRAM;
import static gos.remoter.define.CommandType.COM_NET_DISABLE;
import static gos.remoter.define.CommandType.COM_NET_ENABLE;
import static gos.remoter.define.CommandType.COM_SYSTEM_HEARTBEAT_PACKET;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;
import static gos.remoter.define.CommandType.COM_SYS_EXIT;
import static gos.remoter.define.CommandType.COM_SYS_HEARTBEAT_STOP;

public class NetService extends Service {
    private  final String TAG = this.getClass().getSimpleName();
    //public static NetProtocol.UdpUnicastSocket netSender = null;
    //public static NetProtocol.UdpUnicastSocket netReceiver = null;

    UdpUtil udpUtil = new UdpUtil();

    private HeartbeatPacket heartbeatPacket = null;
    private int heartbeatInterval = 10; //设置心跳包超时时间为10s

    private boolean receiveFlag = true;// 接收运行标志
    private boolean wifiEnableingFlag = false;// wifi开启标志


    /**
     * 接收内部模块事件
     * udp发送给盒子端
     * @param msg    接收的信息
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND,sticky = true)
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

                    udpUtil.setSendPara("255.255.255.255", NetProtocol.sendPort);
                    udpUtil.send(dataPackage.toByte());
                    break;
                case COM_CONNECT_ATTACH:
                    Log.i(TAG,"设备连接设置");
                    Device device = DataParse.getDevice(dataPackage.getData());
                    udpUtil.setSendPara(device.getIp(), NetProtocol.sendPort);
                default:
                    udpUtil.send(dataPackage.toByte());
                    break;
            }
        }else if(EventMode.IN == msg.getEventMode()){//对内
            switch (msg.getCommand()){
                case COM_SYS_EXIT://退出
                    Log.e(TAG,"接收到系统退出命令");
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

        initNet();
        EventManager.register(this);//订阅
        registerWifiReceiver();
        initHeartbeatPacket();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"网络服务销毁");
        super.onDestroy();
        EventManager.unregister(this);//取消订阅
        unregisterWifiReceiver();

    }

    /**
     * 动态注册广播监听wifi
     */
    private void registerWifiReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //registerReceiver(wifiChangedReceiver,filter);
    }

    /**
     * 注销广播
     */
    private void unregisterWifiReceiver(){
        //unregisterReceiver(wifiChangedReceiver);
    }


    /**
     * 初始化，等到触发时执行，停止心跳包
     */
    private void initHeartbeatPacket(){
        heartbeatPacket = new HeartbeatPacket(new HeartbeatStop(){
            @Override
            public void callStopFun() {
                sendHeartbeatStop();
            }
        },heartbeatInterval);
    }

    /**
     * 心跳包停止
     * 转发给内部组件，进行通知
     */
    void sendHeartbeatStop(){
        Log.i(TAG,"sendHeartbeat");
        EventManager.send(COM_SYS_HEARTBEAT_STOP,"", EventMode.IN);
    }

    /**
     * 网络部分
     * 接收服务器的响应，并对其解析
     */
    private void initNet(){
        Log.i(TAG,"初始化网络");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = getWifiIp();
                if(null == ip){
                    Log.e(TAG,"本地ip为空");
                    return;
                }
                udpUtil.receive(ip, NetProtocol.receivePort, new NetCallback() {
                    @Override
                    public void recv(byte[] data) {//回调
                        try {
                            DataPackage dataPackage = new DataPackage(data);
                            parseDataPackage(dataPackage);

                        } catch (DataPackageException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void prepared() {
                        sendSocketPrepared();
                    }
                });

            }
        }).start();
    }

    /**
     * 发送网络准备好，sendSocket创建成功
     */
    private void sendSocketPrepared() {

        EventManager.send(CommandType.COM_NET_SOCKET_PREPARED, "", EventMode.IN);
    }

    private void enableWifi(){
        Log.i(TAG,"请求开启wifi");
        WifiManager wifiMng = (WifiManager)(getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        wifiMng.setWifiEnabled(true);   //启动wifi
    }

    /**
     * wifi  获取路由器分配给设备的ipz
     * @return
     */
    private String getWifiIp(){
        WifiManager wifiMng = (WifiManager) (getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        WifiInfo wifiInfo = wifiMng.getConnectionInfo();
        return getLocalIP(wifiInfo);
    }

    /**
     * ip判断
     * @param wifiInfo
     * @return
     */
    private String getLocalIP( WifiInfo wifiInfo) {
        int ipAddress = wifiInfo.getIpAddress();
        if (0 == ipAddress) {
            Log.e(TAG,"0 == ipAddress");
            return null;
        }
        return  ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

    private void stopReceiver(){
        udpUtil.stopReceive();
    }

    private void destroyNetService(){
        Log.e(TAG,"销毁网络服务");
        stopReceiver();
        stopSelf();//Stop the service
    }


    /**
     * 解析包数据
     * 服务器响应命令类型：
     *    1.应答，不携带data，回应手机已收到请求
     *    2.“心跳包”，确保手机与服务器保持连接状态
     *    3.包含data的应答，如发送“查找设备”的应答，其返回设备信息，如“获取节目”的应答
     *   1,3 类型命令下的dataPackage都需要转发给组件——IN；
     *   2，3 需要给服务器应答,已收到dataPackage
     *
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
            heartbeatPacket.recover();//保持连接状态
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

    /**
     * 转发wifi状态
     */
    private void sendNetEnable(){
        EventManager.send(COM_NET_ENABLE,"", EventMode.IN);
    }

    private void sendNetDisable(){
        EventManager.send(COM_NET_DISABLE,"", EventMode.IN);
    }


}

