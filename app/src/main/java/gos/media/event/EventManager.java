package gos.media.event;

import org.greenrobot.eventbus.EventBus;

/**
 * 事件管理器
 * 封装了EventBus
 * Created by wuxy on 2017/7/14.
 */

public class EventManager {

    /**
     * 事件发送
     * @param command   命令
     * @param data      数据
     * @param eventMode      模式
     */
    public static void send(byte command, String data, EventMode eventMode){
        EventBus.getDefault().post(new EventMsg(command,data, eventMode));
    }

    /**
     * 注册
     * @param subscriber
     */
    public static void register(Object subscriber){
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 解注册
     * @param subscriber
     */
    public static  void unregister(Object subscriber){
        EventBus.getDefault().unregister(subscriber);
    }
}
