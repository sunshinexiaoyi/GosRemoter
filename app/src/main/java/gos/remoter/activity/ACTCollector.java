package gos.remoter.activity;

import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by QXTX-OBOOK on 2017/9/17.
 */

public class ACTCollector {
    private static final String TAG = "来自ACTCollector的消息";
    private static LinkedList<Activity> actList = new LinkedList<Activity>();

    //获得某个ACT
    public static Activity get(int index) {
        return actList.get(index);
    }
    //通过名称获得ACT
    public static int getByName(Activity activity) {
         return actList.indexOf(activity);
    }
    //添加ACT
    public static void add(Activity activity) {
        Log.i(TAG,"添加+"+activity.getClass().getSimpleName());
        actList.add(activity);
    }
    //结束某个ACT
    public static void finish(int index) {
        actList.get(index).finish();
        actList.remove(index);
    }
    //结束全部ACT
    public static void finishAll() {
        Log.e("消息", "一共有" + actList.size() + "个Activity");
        for (int i = 0; i < actList.size(); i++) {
            actList.get(i).finish();
            actList.remove(i);
        }
    }
    //移除某个ACT
    public static void remove(int index) {
        actList.remove(index);
    }


    public static void remove(Activity activity) {
        Log.i(TAG,"移除-"+activity.getClass().getSimpleName());
         int index = getByName(activity);
        if(index != -1){
            actList.remove(index);
        }

        if(actList.size()==0){
            Log.i(TAG," System.gc()");
            System.gc();
        }
    }

    //移除全部
    public static void removeAll() {
        actList.removeAll(actList);
    }
    //查看列表中的ACT
    public static void show() {
        for (int i = 0; i < actList.size(); i++) {
            Log.e(TAG, "ACT" + i + "：" + actList.get(i));
        }
    }

    public static boolean isEmpty(){
        Log.e(TAG,"isEmpty："+(actList.size()==0));
        return (actList.size()==0);
    }
}
