package gos.remoter.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import gos.remoter.R;


public final class DrawableToBitmap {

    private static Bitmap a;
    private static Bitmap b;
    private static Bitmap c;
    private static Bitmap d;
    private static Bitmap e;
    private static Bitmap f;
    private static Bitmap g;
    private static Bitmap h;
    private static Bitmap i;
    private static Bitmap j;
    private static Bitmap k;
    private static Bitmap l;
    private static Bitmap m;
    private static Bitmap n;
    private static Bitmap o;
    private static Bitmap p;
    private static Bitmap q;
    private static Bitmap r;

    public static Bitmap netIcon(Context paramContext) {
        if (a == null)
            a = DrawableToBitmap(paramContext, R.drawable.icon_network);
        return a;
    }

    public static Bitmap emptyIcon(Context paramContext) {
        if (b == null)
            b = DrawableToBitmap(paramContext, R.drawable.icon_empty);
        return b;
    }

    public static Bitmap DrawableToBitmap(Context paramContext, int drawableID) {
        Resources res = paramContext.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, drawableID);
        return bitmap;
    }

}
