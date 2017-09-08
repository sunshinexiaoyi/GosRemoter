package gos.remoter.data;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * 网格
 * Created by wuxy on 2017/9/7.
 */

public class GridActivity {
    private Class activity; //启动的activity
    private int icon;       //图标
    private int name;       //标题

    public GridActivity(Class activity, @DrawableRes int icon,@StringRes int name) {
        this.activity = activity;
        this.icon = icon;
        this.name = name;
    }

    public Class getActivity() {
        return activity;
    }

    public void setActivity(Class activity) {
        this.activity = activity;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public  int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }
}
