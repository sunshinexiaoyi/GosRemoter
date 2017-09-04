package gos.remoter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author daizhongbin
 * @category 用surfaceView来实现字幕的滚动
 * @since 2013-1-28
 */
public class ExtSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // public static final int TEXT_ALIGN_CENTER = 0x00000000;
    public static final int TEXT_ALIGN_LEFT = 0x00000001;
    public static final int TEXT_ALIGN_RIGHT = 0x00000010;
    public static final int TEXT_ALIGN_CENTER_VERTICAL = 0x00000100;
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;
    public static final int TEXT_ALIGN_TOP = 0x00010000;
    public static final int TEXT_ALIGN_BOTTOM = 0x00100000;
    private int width = 0; // surface的宽度
    private int hMoveSpeed = 3; // 水平移动速度
    private int showTimes = 0; // 字符串在该surfaceView上从左到右需要显示几遍
    private float textPosX = 0;
    private float textPosY = 0;
    private float textLen = 0;
    private boolean run = false;
    private boolean canDraw = false;
    private String text = "";
    /**
     * 文本中轴线X坐标
     */
    private float textCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float textBaselineY;
    /**
     * 控件的宽度
     */
    private int viewWidth;
    /**
     * 控件的高度
     */
    private int viewHeight;
    /**
     * 控件画笔
     */
    private Paint paint;
    private FontMetrics fm;
    private int BackGroundcolor = 0xff0b85ff; // 背景颜色 默认是
    private DrawThread drawThread = null;
    private SurfaceHolder holder;
    /**
     * 文字的颜色
     */
    private int textColor;
    /**
     * 文字的大小
     */
    private int textSize;
    /**
     * 文字的方位
     */
    private int textAlign;

    public ExtSurfaceView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        init();
    }

    public ExtSurfaceView(Context context, AttributeSet attr) {
        super(context, attr);
        holder = getHolder();
        holder.addCallback(this);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setTextAlign(Align.CENTER);
        paint.setAntiAlias(true);
        textColor = Color.WHITE;
        textSize = 24;
        // 默认情况下文字居中显示
        textAlign = TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this.holder) {
            // setZOrderOnTop(true);
            // getHolder().setFormat(PixelFormat.TRANSPARENT);
            // setPadding(0, 500, 1280, 60);
            drawThread = new DrawThread();
            drawThread.start();
            run = true;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this.holder) {
            run = false;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        textLen = getTextLength(text);
    }

    public void setTextColor(int color) {
        paint.setColor(color);
    }

    public void setTextSize(float size) {
        paint.setTextSize(size);
    }

    public void setTextAlign(Align align) {
        paint.setTextAlign(align);
    }

    public void setTextPosition(float posX, float posY) {
        textPosX = posX;
        textPosY = posY;
    }

    public Point getTextPosition() {
        Point point = new Point();
        point.x = (int) textPosX;
        point.y = (int) textPosY;
        return point;
    }

    public void setMoveSpeed(int speed) {
        hMoveSpeed = speed;
    }

    public int getShowTimes() {
        return showTimes;
    }

    public void setShowTimes(int showTimes) {
        this.showTimes = showTimes;
    }

    public void canDraw(Boolean flag) {
        canDraw = flag;
    }

    public int getBackGroundColor() {
        return BackGroundcolor;
    }

    public void setBackGroundColor(int color) {
        BackGroundcolor = color;
    }

    /**
     * brief:获取该控件字符串的长度
     */
    public float getTextLength(String text) {
        if (null == text) {
            return 0;
        }
        return paint.measureText(text);
    }

    /**
     * 定位文本绘制的位置
     */
    private void setTextLocation() {
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        fm = paint.getFontMetrics();
        // 文本的宽度
        float textWidth = paint.measureText(text);
        float textCenterVerticalBaselineY = viewHeight / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
        switch (textAlign) {
            case TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = (float) viewWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_LEFT | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = textWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_RIGHT | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = viewWidth - textWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_CENTER_HORIZONTAL:
                textCenterX = viewWidth / 2;
                textBaselineY = viewHeight - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL:
                textCenterX = viewWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_LEFT:
                textCenterX = textWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_LEFT:
                textCenterX = textWidth / 2;
                textBaselineY = viewHeight - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_RIGHT:
                textCenterX = viewWidth - textWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_RIGHT:
                textCenterX = viewWidth - textWidth / 2;
                textBaselineY = viewHeight - fm.bottom;
                break;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        viewWidth = getWidth();
        viewHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            setTextLocation();
            canvas.drawColor(BackGroundcolor, PorterDuff.Mode.SRC);
            canvas.drawText(text, textPosX, textBaselineY, paint);
            // System.out.println("-------------------------------1 force osd
            // 信息的长度 为： "+textPosX);
            // canvas.drawRect(0, 500, 1280, 60,paint);
            textPosX -= hMoveSpeed;
            if (textPosX <= -textLen) {
                textPosX = width;
                showTimes--;
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /*
     * brief : 绘制渲染线程
     */
    class DrawThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (holder) {
                    if (run) {
                        if (canDraw) {
                            draw();
                        } else {
                            try {
                                sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        break;
                    }
                }
                try {
                    sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            drawThread = null;
        }
    }
}
