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
    public static final int ADAPTER_TVITEM = 0;
    public static final int ADAPTER_TVNAME = 1;
    public static final int ADAPTER_TVDATE = 2;
    public static final int ADAPTER_VIEWTYPE = 3;

    //EPGActivity的常量
    public static final String EPGACT_TAG = "来自EPGACT的消息";
    public static final String EPGACT_INITADA_SUCEESS = "初始化适配器成功";
}
