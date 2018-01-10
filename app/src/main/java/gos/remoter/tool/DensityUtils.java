package gos.remoter.tool;

import android.content.Context;


/**
 * 作者：laiyx
 * 日期：2017年12月19日
 * 描述：像素单位类功能集合
 */
public class DensityUtils {
    /**
     * 获得屏幕像素密度( px/(in * in) )
     */
    public static int getScreenDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 获得屏幕宽高像素(px & px)
     */
    public static int[] getScreenPx(Context context) {
        int[] px = new int[2];
        px[0] = context.getResources().getDisplayMetrics().widthPixels;
        px[1] = context.getResources().getDisplayMetrics().heightPixels;
        return px;
    }

    /**
     * 获得屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获得屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     *  dp 转 px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * dp即dip
     *  px = dip * density / 160,则当屏幕密度为160时，px = dip
     */
    public static int px2dp(Context context, float pxValue) {
        int autoDpi = 160;
        float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (pxValue / scale * autoDpi);
//        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp 转 px
     * 描述：认为sp与dp相等
     */
    public static int sp2px(Context context, float spValue) {
        int autoDpi = 160;
        float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int)(spValue / scale * autoDpi);
    }

    /**
     * px 转 sp
     * 描述：认为sp与dp相等
     */
    public static float px2sp(Context context, float pxValue) {
        int autoDpi = 160;
        float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int)(pxValue / autoDpi * scale);
    }

    /**
     * dp 转 sp
     * 描述：认为sp与dp相等
     */
    public static float dp2sp(Context context, float dpValue) {
        return (int)dpValue;
    }

    /**
     * sp 转 dp
     * 描述：认为sp与dp相等
     */
    public static float sp2dp(Context context, float spValue) {
        return (int)spValue;
    }
}