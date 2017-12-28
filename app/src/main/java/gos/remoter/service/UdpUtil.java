package gos.remoter.service;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * UDP 通信
 * Created by wuxy on 2017/12/27.
 */

public class UdpUtil {
    String TAG = getClass().getCanonicalName();
    private boolean receiveFlag = true; //接收循环标志
    private DatagramSocket sendSocket;  //发送套接字 与接收公用

    private InetAddress inetAddress;//发送地址·
    private int port;               //发送端口

    private int receiveLen = 1024*50;   //默认接收长度

    //先开启的接收，再发送
    public void receive(String ip,int port,NetCallback netCallback){
        Log.i(TAG,"开启网络接收 ip"+ip +"port:"+port);
        try (DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName(ip))) {
            sendSocket = socket;    //赋值给发送套接字
            netCallback.prepared();

            Log.i(TAG,"生成套接字");
            while (receiveFlag) {
                try {
                    DatagramPacket request = new DatagramPacket(new byte[receiveLen], receiveLen);
                    socket.receive(request);

                    netCallback.recv(Arrays.copyOf(request.getData(), request.getLength()));
                } catch (IOException e) {
                    e.printStackTrace();
                    socket.close();
                    stopReceive();
                }
            }
            Log.i(TAG,"关闭网络");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止接收
     */
    public void stopReceive(){
        Log.i(TAG,"停止接收循环");
        receiveFlag = false;
    }


    /**
     * 设置发送参数
     * @param ip        ip
     * @param port      端口
     */
    public void setSendPara(String ip,int port){
        try {
            inetAddress = InetAddress.getByName(ip);
            this.port = port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送
     * @param data
     */
    public void send(byte data[]) {
        if(null == inetAddress){
            Log.e(TAG,"发送地址为空");
            return;
        }
        if(null == sendSocket){
            Log.e(TAG,"发送套接字为空");
            return;
        }
        DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
        try {
            sendSocket.send(packet);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送是否准备好
     * @return
     */
    public Boolean sendPrepared(){
        return sendSocket!=null;
    }

}

