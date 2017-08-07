package gos.media;

import com.alibaba.fastjson.JSON;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import gos.media.data.Device;
import gos.media.data.IndexClass;
import gos.media.data.Program;
import gos.media.data.ProgramUrl;
import gos.media.data.Respond;
import gos.media.define.DataPackage;
import gos.media.define.NetProtocol;
import gos.media.define.*;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    String[] testUrl = new String[]{
            "http://192.168.1.109:1026/hls_encrypt/out/langyb/program.m3u8",
            "http://192.168.100.17/hls_media_1_33.ts" ,
    "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
    "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8" ,
    "http://playertest.longtailvideo.com/adaptive/oceans_aes/oceans_aes.m3u8" ,
    "http://playertest.longtailvideo.com/adaptive/captions/playlist.m3u8",
    };
    
    @Test
    public void addition_isCorrect() throws Exception {
        startServer();
        //test();
    }


    public void startServer()throws IOException{
        /*
         * 接收客户端发送的数据
         */
        // 1.创建服务器端DatagramSocket，指定端口
        DatagramSocket socket = new DatagramSocket(NetProtocol.sendPort,InetAddress.getByName("192.168.100.17"));
        System.out.println("服务器配置");
        System.out.println("ip:"+socket.getLocalAddress());
        System.out.println("port:"+socket.getLocalPort());

        // 2.创建数据报，用于接收客户端发送的数据
        byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
        DatagramPacket packet = new DatagramPacket(data, data.length);
        // 3.接收客户端发送的数据
        System.out.println("****服务器端已经启动，等待客户端发送数据");
        boolean flag = true;
        while(flag)
        {
            socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞

            byte[] recvData = Arrays.copyOf(data,packet.getLength());
            try{
                byte[] sendData =  parseData(recvData);
                if(null != sendData)
                {
                    /*向客户端响应数据*/
                    InetAddress address = packet.getAddress();
                    System.out.println("port:"+packet.getPort());
                    DatagramPacket packet2 = new DatagramPacket(sendData, sendData.length, address, NetProtocol.receivePort);
                    System.out.println("客户端配置");
                    System.out.println("port:"+packet2.getPort());
                    System.out.println("ip:"+packet2.getAddress());
                    socket.send(packet2);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        // 4.关闭资源
        socket.close();
    }


    private byte[] parseData(byte[] reaciveData)throws Exception{
        DataPackage dataPackage = new DataPackage(reaciveData);

        String sendData = null;
        Respond respond = null;
        System.out.println("命令："+dataPackage.command);
        switch (dataPackage.getCommand()){

             /* 系统命令 */
            case CommandType.COM_SYSTEM_RESPOND :
                respond = DataParse.getRespond(dataPackage.getData());
                System.out.println("回应"+respond.getCommand());
                break;     //回应
            case CommandType.COM_SYSTEM_HEARTBEAT_PACKET :
                System.out.println("心跳包");
                try{
                    Thread.sleep(1000);
                    dataPackage.command = CommandType.COM_SYSTEM_HEARTBEAT_PACKET;
                }catch (Exception e){
                    e.printStackTrace();}
                break;    //

            /* 连接模块命令*/
            case CommandType.COM_CONNECT_GET_DEVICE:
                System.out.println("查找设备");
                dataPackage.command = CommandType.COM_CONNECT_SET_DEVICE;

                Device device = new Device("192.168.100.17","ff:ee:34:63:23","2017");
                sendData = JSON.toJSONString(device);
                break;      //

            case CommandType.COM_CONNECT_ATTACH  :
                System.out.println("连接设备");
                respond = new Respond(dataPackage.getCommand(),true);
                dataPackage.setCommand(CommandType.COM_SYSTEM_RESPOND);
                sendData = JSON.toJSONString(respond);
                break;     //
            case CommandType.COM_CONNECT_DETACH  :
                System.out.println("分离");
                respond = new Respond(dataPackage.getCommand(),true);
                dataPackage.setCommand(CommandType.COM_SYSTEM_RESPOND);
                sendData = JSON.toJSONString(respond);
                break;    //

             /* 直播模块命令 */
            case CommandType.COM_LIVE_GET_PROGRAM_LIST :
                System.out.println("获取节目列表");
                dataPackage.command = CommandType.COM_LIVE_SET_PROGRAM_LIST;

                ArrayList<Program> programList = new ArrayList<>();
                for(int i = 0;i<5;i++)
                {
                    String name = String.format("cctv-%d",i);
                    Program program = new Program(name,i);
                    programList.add(program);
                }
                sendData = JSON.toJSONString(programList);

                break; //
            case CommandType.COM_LIVE_GET_PROGRAM_URL :
                System.out.println("获取节目url");
                //String testUrl ="
                dataPackage.command = CommandType.COM_LIVE_SET_PROGRAM_URL;
                IndexClass indexClass = JSON.parseObject(dataPackage.getData(),IndexClass.class);
                int index = indexClass.getIndex();
                ProgramUrl programUrl = new ProgramUrl(index,testUrl[index]);
                sendData = JSON.toJSONString(programUrl);
                break; //

            case CommandType.COM_LIVE_STOP_PROGRAM :
                System.out.println("停止节目");
                respond = new Respond(dataPackage.getCommand(),true);
                dataPackage.setCommand(CommandType.COM_SYSTEM_RESPOND);
                sendData = JSON.toJSONString(respond);
                break;   //
            default:
                break;
        }
        if(null == sendData)
        {
            return null;
        }
        System.out.println(sendData);
        dataPackage.setData(sendData);
        return dataPackage.toByte();
    }

}