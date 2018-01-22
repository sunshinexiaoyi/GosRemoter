package gos.remoter.define;

/**
 * 命令集
 * Created by wuxy on 2017/7/7.
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
    byte CMD_SYSTEM_CLIENT_NOT_FOUND = 3;//

    /* 连接模块命令 */
    byte COM_CONNECT_GET_DEVICE = 10;       //查找设备
    byte COM_CONNECT_SET_DEVICE = 11;       //添加设备

    byte COM_CONNECT_ATTACH = 12;     //连接
    byte COM_CONNECT_DETACH = 13;     //分离

    /* 直播模块命令 */
    byte COM_LIVE_GET_PROGRAM_LIST = 20; //获取直播节目列表
    byte COM_LIVE_SET_PROGRAM_LIST = 21; //设置直播节目列表

    byte COM_LIVE_GET_PROGRAM_URL = 22;  //获取直播节目url
    byte COM_LIVE_SET_PROGRAM_URL = 23;  //设置直播节目url

    byte COM_LIVE_STOP_PROGRAM = 24;     //停止直播节目
    byte COM_LIVE_UPDATE_PROGRAM_LIST = 25; //更新直播节目列表

    /* 遥控器模块命令 */
    byte COM_REMOTE_SET_KEY = 30; //发送遥控器键值
	byte COM_REMOTE_SET_LONG_KEY = 31; //发送遥控器长按键值
    byte COM_REMOTE_SET_KEY_UP = 32; //发送遥控器长按松开的键值

    /* EPG模块命令 */
    byte COM_EPG_GET_INFORM_LIST = 40;//获取节目epg信息列表
    byte COM_EPG_SET_INFORM_LIST = 41;//设置节目epg信息列表

    byte COM_EPG_SET_RESERVE = 42;   //设置epg预定事件
    byte COM_EPG_CLASH_RESERVE = 43; //epg预定事件冲突

    /* 定时开关机模块命令 */
    byte COM_TIME_SET_OFF = 50; //设置定时关机
    byte COM_TIME_SET_ON = 51; //设置定时开机

    /* 邮件模块命令 */
    byte COM_EMAIL_GET_INFORM = 60;//获取邮件信息
    byte COM_EMAIL_SET_INFORM = 61;//设置邮件信息

    /* 节目列表模块命令 */
    byte COM_PROGRAM_GET_All_LIST = 70; //获取所有节目列表 
    byte COM_PROGRAM_SET_All_LIST = 71; //设置所有节目列表
    byte COM_PROGRAM_STB_SWITCH = 72;   //切换播放节目
    byte COM_PROGRAM_UPDATE_ALL_LIST = 73;//更新所有节目列表
    byte COM_PROGRAM_SET_UPDATE_ALL_LIST = 74;//触发服务器的更新命令，模拟

    /* 广告模块命令 */
    byte COM_GET_AD_INFO = 80; //获取广告
    byte COM_SET_AD_IMFO = 81; //设置广告

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

    byte COM_SYS_FINISH_LIVE = 105;     //结束直播

    byte COM_SYS_REMOTE_ID = 106;     //遥控器id

    byte COM_NET_ENABLE = 107;         //无线网络可用，已打开
    byte COM_NET_DISABLE = 108;        //无线网络不可用

    byte COM_NET_SOCKET_PREPARED = 109;        //网络准备好,socket

    byte COM_SET_SYSTEM_LANGUAGE = 110;        //系统语言


}





