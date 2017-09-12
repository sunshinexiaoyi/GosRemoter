package gos.remoter.tool;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

/**沉浸式布局
 * Created by wuxy on 2017/9/7.
 */

public class ImmersionLayout {
    private Activity activity;

    public ImmersionLayout(Activity activity) {
        this.activity = activity;
    }

    public void  setImmersion(){
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }
}
