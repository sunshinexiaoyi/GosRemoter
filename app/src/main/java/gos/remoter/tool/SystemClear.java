package gos.remoter.tool;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;

/**
 * 系统清理
 * Created by wuxy on 2017/9/19.
 */

public class SystemClear {
    public static void clearBackground(View view){
        BitmapDrawable bd = (BitmapDrawable)view.getBackground();
        view.setBackgroundResource(0);
        bd.setCallback(null);
        bd.getBitmap().recycle();
        bd = null;
    }
}
