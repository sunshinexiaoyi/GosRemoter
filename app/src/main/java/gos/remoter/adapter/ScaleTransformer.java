package gos.remoter.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by lp on 2017/12/20.
 * viewPager的透明度，放大缩小
 */

public class ScaleTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.60f;
    private static final float MIN_ALPHA = 0.5f;//半透明

    private Context context;
    private float elevation;//阴影效果

    public ScaleTransformer(Context context) {
        this.context = context;
        elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
    }

    /**
     * position取值特点： 假设页面从0～1，则：
     * 第一个页面position变化为[0,-1]
     * 第二个页面position变化为[1,0]
     * @param page
     * @param position
     */
    @Override
    public void transformPage(View page, float position) {
        if (position < -1 || position > 1) {
            page.setAlpha(MIN_ALPHA);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
        } else if (position <= 1) { // [-1,1]
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            if (position < 0) {
                float scaleX = 1 + 0.3f * position;
//                Log.e("ScaleTransformer", "scaleX:" + scaleX);
                page.setScaleX(scaleX);
                page.setScaleY(scaleX);
                ((CardView) page).setCardElevation((1 + position) * elevation);
            } else {
                float scaleX = 1 - 0.3f * position;
                page.setScaleX(scaleX);
                page.setScaleY(scaleX);
                ((CardView) page).setCardElevation((1 - position) * elevation);
            }
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
    }
}
