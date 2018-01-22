package gos.remoter.event;

import org.greenrobot.eventbus.EventBus;

/**
 * 事件管理器
 * 封装了EventBus
 * Created by wuxy on 2017/7/14.
 */

public class  EventManager {

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
     * 事件粘性发送
     * @param command   命令
     * @param data      数据
     * @param eventMode      模式
     */
    public static void sendSticky(byte command, String data, EventMode eventMode){
        EventBus.getDefault().postSticky(new EventMsg(command,data, eventMode));
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

    /**
     *  移除所有的粘性事件
     */
    public static  void removeAllSticky() {
        EventBus.getDefault().removeAllStickyEvents();
    }

    /**
     * 移除指定粘性事件
     * @param subscriber
     */
    public static  void removeSticky(Object subscriber) {
        EventBus.getDefault().removeStickyEvent(subscriber);
    }

}
