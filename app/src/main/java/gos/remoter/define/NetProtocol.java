/**
 * NetWork.java class:SocketType,UdpUnicastSocket
 * @author wildwolf group
 */

package gos.remoter.define;

import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;


public  class NetProtocol{
    public static final int sendPort = 4321;
    public static final int receivePort = 1234;

    public enum SocketType {
        SEND,
        RECEIVE
    }

    /**
     * UdpUnicastSocket class:UDP 单播类
     * getType:获取套接字类型。
     * getAddress:获取网络地址
     * getPort:获取端口号
     * send:发送网络数据
     * receive:接收网络数据
     */
    public static class UdpUnicastSocket {
        public InetAddress ipAddress;
        public int port;
        private DatagramSocket socket;
        private SocketType type;


        public UdpUnicastSocket(String IP, int port, SocketType type) throws Exception{
            try {
                this.ipAddress = InetAddress.getByName(IP);
            }catch (UnknownHostException e) {
                e.printStackTrace();
                throw new Exception("InetAddress.getByName(IP) failed");
            }
            this.port = port;
            this.type = type;

            if (type == SocketType.SEND) {
                this.socket = new DatagramSocket();

            } else if (type == SocketType.RECEIVE) {
                this.socket = new DatagramSocket(this.port, this.ipAddress);

            }
        }

        public SocketType getType() {
            return type;
        }

        public String getAddress() {
            return ipAddress.getHostAddress();
        }

        public int getPort() {
            return port;
        }

        public void send(byte data[]) {
            //Log.i("test__send",String.format("ip:%s,port:%d",getAddress(),port));
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
            try {
                socket.send(packet);
                SystemClock.sleep(100);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public byte [] receive() {
            byte receiveData[] = new byte[1024 *100];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length, ipAddress, port);
            try {
                socket.receive(packet);
                return Arrays.copyOf(packet.getData(), packet.getLength());
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void close()
        {
            this.socket.close();
        }


    }

    /**
     * UdpMulticastSocket class:UDP组播类
     * getAddress:获取组播地址
     * getPort:获取组播端口
     * send:发送组播数据
     * receive:收取组播数据
     */
    public static class UdpMulticastSocket {
        private InetAddress ipAddress;
        private int port;
        MulticastSocket socket;

        public UdpMulticastSocket(String IP, int port) {
            this.port = port;
            try {
                this.ipAddress = InetAddress.getByName(IP);
                socket = new MulticastSocket(port);
                socket.setTimeToLive(1); //local network
                socket.joinGroup(ipAddress);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getAddress() {
            return ipAddress.getHostAddress();
        }

        public int getPort() {
            return port;
        }

        public void send(byte data[]) {
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
            try {
                socket.send(packet);
                SystemClock.sleep(100);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public byte [] receive() {
            byte receiveData[] = new byte[1024 *100];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length, ipAddress, port);
            try {
                socket.receive(packet);
                return Arrays.copyOf(packet.getData(), packet.getLength());
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * TcpSocket class:TCP网络类
     * close:关闭网络连接
     * getAddress:获取网络地址
     * getPort:获取网络端口
     * send:发送网络数据
     * receive:接收网络数据
     */
    public static class TcpSocket {
        private InetAddress ipAddress;
        private int port;
        private Socket socket;
        private OutputStream oStream;
        private BufferedReader iStream;

        public TcpSocket(String IP, int port, boolean isServer) {
            try {
                this.port = port;
                if (isServer) {
                    ServerSocket ss = new ServerSocket(port);
                    socket = ss.accept();
                    this.ipAddress = socket.getInetAddress();
                } else {
                    this.ipAddress = InetAddress.getByName(IP);
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ipAddress, port), 10000);
                }
                iStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                oStream = socket.getOutputStream();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void close(){
            try {
                socket.close();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        public String getAddress() {
            return socket.getInetAddress().getHostAddress();
        }

        public int getPort() {
            return socket.getPort();
        }

        public void send(byte data[]) {
            try {
                oStream.write(data);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void send(String data) {
            try {
                oStream.write(data.getBytes("utf-8"));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public byte [] receive() {
            try {
                return iStream.readLine().getBytes("utf-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class NetTest
    {
        public void sendUdp()
        {
            Log.i("test__send","sendUdp");
            DatagramSocket ds = null;  //建立套间字udpsocket服务

            try {
                ds = new DatagramSocket(8999);  //实例化套间字，指定自己的port
            } catch (SocketException e) {
                Log.e("test__send","Cannot open port!");
                System.exit(1);
            }

            byte[] buf= "Hello, I am sender!".getBytes();  //数据
            InetAddress destination = null ;
            try {
                destination = InetAddress.getByName("192.168.100.100");  //需要发送的地址
            } catch (UnknownHostException e) {
                Log.e("test__send","Cannot open findhost!");
                System.exit(1);
            }
            DatagramPacket dp =
                    new DatagramPacket(buf, buf.length, destination , 4321);
            //打包到DatagramPacket类型中（DatagramSocket的send()方法接受此类，注意10000是接受地址的端口，不同于自己的端口！）

            try {
                ds.send(dp);  //发送数据
                Log.i("test__send","Success send!");
            } catch (IOException e) {
                Log.e("test__send","Cannot send!");
            }
            ds.close();
        }
    }
}
