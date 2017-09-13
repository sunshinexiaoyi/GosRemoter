package gos.remoter.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import gos.remoter.R;

/**
 * Created by lp on 2017/9/8.
 */

public class RemoterSetting extends View {

        private static final String TAG = "View";
        int width = 120;
        int height = 120;
        Paint paintW;
        Paint paintB;
        Paint paintText;
        Paint paintF;
        RectF rectf;
        String ok = "OK";
        int blue = Color.parseColor("#2a95e5");
        int bg_color = Color.parseColor("#f6f7fb");

        //当前点击位置 0中间，3左，4右，1上，2下
        int clickP = -1;

        private onClickItemListener onClickItemListener;

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
            paintF = new Paint();

            paintW.setColor(bg_color);
            paintB.setColor(blue);
            paintText.setColor(blue);
            paintText.setTextSize(100f);
            // DEFAULT_BOLD :黑体字体类型,SANS_SERIF:sans serif字体类型,Typeface.BOLD:粗体
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            paintText.setTypeface(font);
            paintB.setAntiAlias(true);
            paintW.setAntiAlias(true);
            paintText.setAntiAlias(true);
            rectf = new RectF();

            //阴影,加一个圆环，
            paintF.setAlpha(1);
            paintF.setColor(getResources().getColor(R.color.remoter_hint));
            paintF.setStrokeWidth(7);
            paintF.setStyle(Paint.Style.STROKE);

        }

       // View在屏幕上显示出来要先经过measure（计算）和layout（布局）.
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthMode == MeasureSpec.EXACTLY) {
                width = widthSize;
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize;
            }
            rectf.set(5, 5, width - 5, height - 5);
            setMeasuredDimension(width, height);// 传递View的高度和宽度，高速父布局其大小
        }

        @Override
        protected void onDraw(Canvas canvas) {
            paintB.setStrokeWidth(10);
            switch (clickP) {
                case 0:
                    /**由于没有现成的箭头图片，一个笨办法，画8个线段吧！*/
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW); //弧度
                    canvas.drawLine(width - 50, height / 2 + 3, width - 100, height / 2 - 50, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 100, height / 2 + 50, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 50, 100, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 50, 100, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 100, height / 2 - 50, paintB);
                    canvas.drawLine(50, height / 2 - 3, 100, height / 2 + 50, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 50, height - 100, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 50, height - 100, paintB);
                    //中间圆和字
                    paintB.setStyle(Paint.Style.FILL);
                    paintText.setColor(Color.WHITE);
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintB);
                    canvas.drawText(ok, (width / 2) - 75, (height / 2) + 30, paintText);
                    paintText.setColor(blue);
                    break;
                case 1:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 100, height / 2 - 50, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 100, height / 2 + 50, paintB);
                    //上箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 50, 100, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 50, 100, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 100, height / 2 - 50, paintB);
                    canvas.drawLine(50, height / 2 - 3, 100, height / 2 + 50, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 50, height - 100, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 50, height - 100, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2) - 75, (height / 2) + 30, paintText);

                    break;
                case 2:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 100, height / 2 - 50, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 100, height / 2 + 50, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 50, 100, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 50, 100, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 100, height / 2 - 50, paintB);
                    canvas.drawLine(50, height / 2 - 3, 100, height / 2 + 50, paintB);
                    //下箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 50, height - 100, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 50, height - 100, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2) - 75, (height / 2) + 30, paintText);

                    break;
                case 3:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 100, height / 2 - 50, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 100, height / 2 + 50, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 50, 100, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 50, 100, paintB);
                    //左箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 100, height / 2 - 50, paintB);
                    canvas.drawLine(50, height / 2 - 3, 100, height / 2 + 50, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 50, height - 100, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 50, height - 100, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2) - 75, (height / 2) + 30, paintText);

                    break;
                case 4:
                    //右箭头和扇形
                    paintW.setColor(blue);
                    paintB.setColor(Color.WHITE);
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 100, height / 2 - 50, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 100, height / 2 + 50, paintB);
                    paintW.setColor(bg_color);
                    paintB.setColor(blue);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 50, 100, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 50, 100, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 100, height / 2 - 50, paintB);
                    canvas.drawLine(50, height / 2 - 3, 100, height / 2 + 50, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 50, height - 100, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 50, height - 100, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2) - 75, (height / 2) + 30, paintText);

                    break;
                default:

                    //canvas.drawCircle(width / 2 + 3, height / 2 + 6, width / 2 - 3, paintF);
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    canvas.drawLine(width - 50, height / 2 + 3, width - 100, height / 2 - 50, paintB);
                    canvas.drawLine(width - 50, height / 2 - 3, width - 100, height / 2 + 50, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, 50, width / 2 - 50, 100, paintB);
                    canvas.drawLine(width / 2 - 3, 50, width / 2 + 50, 100, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    canvas.drawLine(50, height / 2 + 3, 100, height / 2 - 50, paintB);
                    canvas.drawLine(50, height / 2 - 3, 100, height / 2 + 50, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 50, height - 100, paintB);
                    canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 50, height - 100, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5, paintW);
                    canvas.drawText(ok, (width / 2) - 75, (height / 2) + 30, paintText);

                    break;
            }


            paintB.setStyle(Paint.Style.STROKE); //空心效果
            paintB.setStrokeWidth(6);  //线宽
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
                    } else if (x < 2 * width / 3 && x > width / 3 && y < height && y > 2 * height / 3) {
                        clickP = 2;
                    } else if (x < width / 3 && x > 0 && y < 2 * height / 3 && y > height / 3) {
                        clickP = 3;
                    } else if (x < width && x > 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 4;
                    } else if (x > width / 3 && x < 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 0;
                    }
                    invalidate();

                    return true;
                case MotionEvent.ACTION_UP:

                    Log.d(TAG, "onTouchEvent: " + clickP);
                    invalidate();
                    //当前点击位置 0中间，3左，4右，1上，2下
                    if (null != onClickItemListener) {
                        switch (clickP) {
                            case 0:
                                onClickItemListener.ok();
                                break;
                            case 1:
                                onClickItemListener.up();
                                break;
                            case 2:
                                onClickItemListener.down();
                                break;
                            case 3:
                                onClickItemListener.left();
                                break;
                            case 4:
                                onClickItemListener.right();
                                break;
                        }
                    }
                    clickP = -1;
                    return true;
            }
            return false;
        }

        interface onClickItemListener {
            void up();
            void down();
            void left();
            void right();
            void ok();
        }

        public void setOnClickItemListener(RemoterSetting.onClickItemListener onClickItemListener) {
            this.onClickItemListener = onClickItemListener;
        }

}
