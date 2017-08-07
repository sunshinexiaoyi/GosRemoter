package gos.media.define;

/**
 * Created by wuxy on 2017/7/7.
 * 通信的命令类型
 */

public class CommandType implements CommandOut,CommandIn{

}

/**
 * 外部命令
 * 用于与服务器间通信
 */
interface CommandOut{
    /* 系统命令 */
    byte COM_SYSTEM_RESPOND = 1;     //回应
    byte COM_SYSTEM_HEARTBEAT_PACKET = 2;     //心跳包

    /* 连接模块命令*/
    byte COM_CONNECT_GET_DEVICE = 10;       //查找设备
    byte COM_CONNECT_SET_DEVICE = 11;       //添加设备

    byte COM_CONNECT_ATTACH = 12;     //连接
    byte COM_CONNECT_DETACH = 13;     //分离

    /* 直播模块命令 */
    byte COM_LIVE_GET_PROGRAM_LIST = 20; //获取节目列表
    byte COM_LIVE_SET_PROGRAM_LIST = 21; //设置节目列表

    byte COM_LIVE_GET_PROGRAM_URL = 22;  //获取节目url
    byte COM_LIVE_SET_PROGRAM_URL = 23;  //设置节目url

    byte COM_LIVE_STOP_PROGRAM = 24;     //停止节目
    byte COM_LIVE_UPDATE_PROGRAM_LIST = 25; //更新节目列表

    /*遥控器模块命令*/
    byte COM_REMOTE_SET_KEY = 30; //发送遥控器键值

}

/**
 * 内部命令
 * 用于内部各个模块间通信
 */
interface CommandIn{
     /* 系统命令 */
    byte COM_SYS_EXIT = 101;     //退出系统

    byte COM_SYS_JUMP_CONNECT = 102;     //跳转到连接界面
    byte COM_SYS_JUMP_LIVE = 103;     //跳转到直播界面

    byte COM_SYS_HEARTBEAT_STOP = 104;     //心跳停止

    byte COM_SYS_FINISH_LIVE = 105;     //接收直播

    byte COM_SYS_REMOTE_ID = 106;     //遥控器id


}





