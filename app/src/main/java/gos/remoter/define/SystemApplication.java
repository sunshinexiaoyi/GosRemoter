package gos.remoter.define;

import android.app.Application;
import android.content.Context;

import gos.remoter.data.Device;
import gos.remoter.enumkey.SystemState;
import gos.remoter.tool.SharedPreferencesUtils;

/**
 * 系统信息
 * Created by wuxy on 2017/7/14.
 */

public class SystemApplication extends Application {
    private Device service = null;
    private SystemState state = SystemState.DETACH;
    private static SystemApplication instance = null;
    public SystemApplication(){
        super();
    }
    private Context context;

    /**
     * 获取SystemInfo实例
     * @return SystemInfo对象
     */
    public static SystemApplication getInstance(){
        return instance;
    }

    public SystemState getState(){
        return state;
    }

    /**
     * 设置系统状态
     * @param state   ATTACH 连接,DETACH 断开连接
     */
    public void setState(SystemState state){
        this.state = state;
    }

    public Device getService(){
        return service;
    }

    /**
     * 设置服务器设备信息
     * @param service 服务器信息
     */
    public void setService(Device service){
        this.service = service;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        SharedPreferencesUtils.init(context);//初始化
    }
}
