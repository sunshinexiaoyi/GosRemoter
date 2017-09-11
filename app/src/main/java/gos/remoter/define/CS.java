package gos.remoter.define;

/**
 * Created by QXTX-GOSPELL on 2017/9/5 0005.
 * 静态常量
 */

public class CS {
    public static final String EPG_TAG = "来自EPGACT的消息";
    public static final String EPG_ONCREATE = "进入EPGACT的onCreate()方法";
    public static final String EPG_ONDESTROY = "进入EPGACT的onDwestroy()方法";

    //Adapter注释调试用常量
    public static final String ADAPTER_TAG = "Adapter的消息";
    public static final String ADAPTER_GETCOUNT = "执行getCount()方法";
    public static final String ADAPTER_GETITEM = "执行getItem()方法";
    public static final String ADAPTER_GETITEMID = "执行getItemId()方法";
    public static final String ADAPTER_GETVIEW = "执行getView()方法";
    public static final String ADAPTER_INIT = "进入构造方法, 传入上下文、一个泛型列表";

    //EPGActivity的常量
    public static final String EPGACT_TAG = "来自EPGACT的消息";
    public static final String EPGACT_INITADA_SUCEESS = "初始化适配器成功";
    public static final String EPGACT_UNREGISTER_EVENTMANAGER = "取消注册EventManager";
    public static final String EPGACT_DEATH = "EPGACT被杀死了";
}
