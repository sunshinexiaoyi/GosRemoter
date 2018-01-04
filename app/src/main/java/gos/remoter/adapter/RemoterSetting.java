package gos.remoter.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lp on 2017/9/8.
 */

public class RemoterSetting extends View {

    private static final String TAG = "View";
    private static final int DEFAULT_HEIGHT = 100;
    private static final int DEFAULT_WIDTH = 100;
    int width = 100;
    int height = 100;
    int num = 0;
    Paint paintW;
    Paint paintB;
    Paint paintText;
    RectF rectf;//整个view
    Rect mBound;//字符串
    String ok = "OK";
    int blue = Color.parseColor("#F75522");//2a95e5：蓝色，F75522：橙色
    int bg_color = Color.parseColor("#f6f7fb");//f6f7fb:白色

    //当前点击位置 0中间，3左，4右，1上，2下
    int clickP = -1;
    int curKeyValue = -1;//保存当前按下时键位值
    boolean isLongClick = false;

    int upKey;
    int downKey;
    int leftKey;
    int rightKey;
    int okKey;

    private onTouchListener onTouchListener;
    public Timer timer = new Timer();//定时器，计时器,判断是否长按
    public TimerTask timerTask = null;//监听长按的任务
        class LongTimerTask extends TimerTask {
            int keyValue;
            public LongTimerTask(int keyValue){
                super();
                this.keyValue = keyValue;
            }

            @Override
            public void run() {
                Log.e("status", "长按位置:" + keyValue);
                onTouchListener.longClick(keyValue);
                isLongClick = true;
            }
        }

        public RemoterSetting(Context context) {
            this(context, null);
        }

        public RemoterSetting(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public RemoterSetting(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            paintB = new Paint();
            paintW = new Paint();
            paintText = new Paint();

            paintW.setColor(bg_color);
            paintB.setColor(blue);
            paintText.setColor(blue);
//            paintText.setTextSize(100f);
            // DEFAULT_BOLD :黑体字体类型,SANS_SERIF:sans serif字体类型,Typeface.BOLD:粗体
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            paintText.setTypeface(font);
            paintB.setAntiAlias(true);
            paintW.setAntiAlias(true);
            paintText.setAntiAlias(true);
            rectf = new RectF();
            mBound = new Rect();

        }

       // View在屏幕上显示出来要先经过measure（计算）和layout（布局）.
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            width = getMeasuredLength(widthMeasureSpec, true);
            height = getMeasuredLength(heightMeasureSpec, false);
            num = Math.abs(width - height);
//            Log.e(TAG, num + " num");

            width = Math.min(height, width);
            rectf.set(5, 5, width - 5, height - 5);
            //rectf.set(num / 2, num / 3, width - num / 2, height - num / 3);
            setMeasuredDimension(width, height);// 传递View的高度和宽度，高速父布局其大小
        }

        private int getMeasuredLength(int length, boolean isWidth) {
            int specMode = MeasureSpec.getMode(length);
            int specSize = MeasureSpec.getSize(length);
            int size;
            int padding = isWidth ? getPaddingLeft() + getPaddingRight()
                    : getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.EXACTLY) {//精确尺寸,固定
                size = specSize;
            } else {
                size = isWidth ? padding + DEFAULT_WIDTH : DEFAULT_HEIGHT + padding;
                if (specMode == MeasureSpec.AT_MOST) {
                    size = Math.max(size, specSize);
                }
            }
            return size;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            paintB.setStrokeWidth(8); //线宽
            paintW.setStyle(Paint.Style.FILL);
            paintText.setTextSize(height/5);
            paintText.getTextBounds(ok, 0, ok.length(), mBound);//得到字符串的高宽
            //Log.e(TAG, "width--" + mBound.width() + "  height--" + mBound.height());
            switch (clickP) {
                case 0:
                    /**由于没有现成的箭头图片，一个笨办法，画8个线段吧！*/
                    //右箭头和扇形
                    //椭圆对象、起始角度、所画角度,为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    //中间圆和字
                    paintB.setStyle(Paint.Style.FILL);
                    paintText.setColor(Color.WHITE);
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintB);
                    canvas.drawText(ok, (width / 2 - mBound.width() / 2), (height / 2 + mBound.height() / 2), paintText);
                    paintText.setColor(blue);
                    break;
                case 1:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                    //上箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2 - mBound.width() / 2), (height / 2 + mBound.height() / 2), paintText);

                    break;
                case 2:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    //下箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2 - mBound.width() / 2), (height / 2 + mBound.height() / 2), paintText);

                    break;
                case 3:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    //左箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2 - mBound.width() / 2), (height / 2 + mBound.height() / 2), paintText);

                    break;
                case 4:
                    //右箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2 - mBound.width() / 2), (height / 2 + mBound.height() / 2), paintText);

                    break;
                default:

                    //canvas.drawCircle(width / 2 + 3, height / 2 + 6, width / 2 - 3, paintF);
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2 - mBound.width() / 2), (height / 2 + mBound.height() / 2), paintText);

                    break;
            }
            paintB.setStyle(Paint.Style.STROKE); //空心效果
            paintB.setStrokeWidth(5);  //线宽
            //canvas.drawColor(bg_color); //画布背景
            canvas.drawCircle(width / 2, height / 2, width / 2 - 5, paintB);
            canvas.drawCircle(width / 2, height / 2, width / 5, paintB);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    return true;
                case MotionEvent.ACTION_DOWN:
                    if (x < 2 * width / 3 && x > width / 3 && y > 0 && y < height / 3) {
                        clickP = 1;
                        curKeyValue = upKey;
                    } else if (x < 2 * width / 3 && x > width / 3 && y < height && y > 2 * height / 3) {
                        clickP = 2;
                        curKeyValue = downKey;
                    } else if (x < width / 3 && x > 0 && y < 2 * height / 3 && y > height / 3) {
                        clickP = 3;
                        curKeyValue = leftKey;
                    } else if (x < width && x > 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 4;
                        curKeyValue = rightKey;
                    } else if (x > width / 3 && x < 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 0;
                    }
                    // onDraw之中调用invalidate()，会再触发onDraw，从而不停刷新显示
                    invalidate();
                    if(0 !=clickP){//ok键无法触发长按
                        timerTask = new LongTimerTask(curKeyValue);
                        timer.schedule(timerTask,800);
                    }
                    return true;
                case MotionEvent.ACTION_UP:

                    Log.d(TAG, "onTouchEvent: " + clickP);
                    invalidate();
                    //当前点击位置 0中间，3左，4右，1上，2下
                    if (null != onTouchListener) {
                        switch (clickP) {
                            case 0:
                                curKeyValue = okKey;
                                break;
                            case 1:
                                curKeyValue = upKey;
                                break;
                            case 2:
                                curKeyValue = downKey;
                                break;
                            case 3:
                                curKeyValue = leftKey;
                                break;
                            case 4:
                                curKeyValue = rightKey;
                                break;
                        }
                        cancelLongTask();
                        if(!isLongClick) {//“是”--取消
                            onTouchListener.click(curKeyValue);
                            Log.e("status", "点击位置:" + curKeyValue);
                        }
                    }
                    clickP = -1;
                    invalidate();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    clickP = -1;
                    cancelLongTask();
                    invalidate();
                    return true;
            }
            return false;
        }

    public void cancelLongTask() {
        if(null != timerTask) {
            if(isLongClick) {//“是”--取消
                Log.e("status","取消长按:"+curKeyValue);
                onTouchListener.cancelLong(curKeyValue);
            }
            timerTask.cancel();
            timerTask = null;
            isLongClick = false;
        }
    }

    public interface onTouchListener {

            void click(int keyValue);
            void longClick(int keyValue);//长按接口
            void cancelLong(int keyValue);
        }

        public void setOnTouchListener(onTouchListener onTouchListener) {
            this.onTouchListener = onTouchListener;
        }

        //设置键值
        public void setKeyValue(int upKey, int downKey, int leftKey, int rightKey, int okKey) {
            this.upKey = upKey;
            this.downKey = downKey;
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.okKey = okKey;
        }

}
