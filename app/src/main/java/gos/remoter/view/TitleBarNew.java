package gos.remoter.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import gos.remoter.R;

/**
 * Created by wuxy on 2017/9/7.
 */

public class TitleBarNew  extends android.support.constraint.ConstraintLayout{
    private ImageView imageRight;
    private ImageView imageLeft;
    private TextView textTitle;
    private ConstraintLayout titleLayout;


    public TitleBarNew(Context context) {
        super(context);
        init(context);
    }

    public TitleBarNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public TitleBarNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化布局文件
     * @param context
     */
    private void init(Context context){
        //加载title_bar布局文件
        LayoutInflater.from(context).inflate(R.layout.title_bar,this,true);

        imageLeft = (ImageView)findViewById(R.id.imageLeft);
        imageRight = (ImageView)findViewById(R.id.imageRight);
        textTitle = (TextView) findViewById(R.id.textTitle);
        titleLayout = (ConstraintLayout)findViewById(R.id.titleLayout);
    }

    /**
     * 设置标题
     * @param resId 文本资源id
     */
    public void setTextTitle(@StringRes int resId){
        textTitle.setText(resId);
    }

    /**
     * 设置右部图片
     * @param resId 图片资源id
     */
    public void setImageRight(@DrawableRes int resId){
        imageRight.setImageResource(resId);
    }

    public void setImageRight(@DrawableRes int resId,OnClickListener onClickListener){
        setImageRight(resId);
        imageRight.setOnClickListener(onClickListener);
    }

    public void setImageLeft(@DrawableRes int resId){
        imageLeft.setImageResource(resId);
    }

    public void setImageLeft(@DrawableRes int resId,OnClickListener onClickListener){
        setImageLeft(resId);
        imageLeft.setOnClickListener(onClickListener);
    }

    public void setBackground(@DrawableRes int resId){
        titleLayout.setBackgroundResource(resId);
    }

    public void setNullBackground(){
        titleLayout.setBackground(null);
    }



    public void setAlpha(@IntRange(from=0,to=255) int alpha){

        titleLayout.getBackground().setAlpha(alpha);
    }


}
