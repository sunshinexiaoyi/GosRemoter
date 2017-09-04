package gos.remoter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gos.remoter.R;


public class TitleBar extends RelativeLayout {

    private Context mContext;
    private ImageView rightImageView;
    private ImageView leftImageView;
    private TextView leftTextView;
    private LinearLayout rightLayout;
    private RelativeLayout wrapLayout;

    public TitleBar(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public TitleBar(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public TitleBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private void init(Context paramContext) {
        this.mContext = paramContext;
    }

    private void setButton(ImageView paramImageView, int paramInt, View.OnClickListener paramOnClickListener) {
        if (paramInt == 0)
            return;
        paramImageView.setImageResource(paramInt);
        paramImageView.setVisibility(View.VISIBLE);
        if (paramOnClickListener == null)
            return;
        rightLayout.setOnClickListener(paramOnClickListener);
    }

    public void setTitleInfoWithLogo(int paramInt1, int paramInt2, View.OnClickListener paramOnClickListener) {
        LayoutInflater.from(this.mContext).inflate(R.layout.title, this, true);
        leftTextView = (TextView) findViewById(R.id.tv_title_name);
        leftImageView = (ImageView) findViewById(R.id.img_title_logo);
        rightImageView = ((ImageView) findViewById(R.id.img_title_set));
        rightLayout = (LinearLayout) findViewById(R.id.ll_title_set);
        leftTextView.setVisibility(View.GONE);
        setImageView(leftImageView, paramInt1);
        setButton(this.rightImageView, paramInt2, paramOnClickListener);
    }

    public void setBacground(int wrCo, int rightCo) {
        wrapLayout.setBackgroundColor(wrCo);
        rightImageView.setBackgroundColor(rightCo);
    }

    public void hideRightImageView() {
        rightImageView.setVisibility(View.GONE);
    }

    public void showRightImageView() {
        rightImageView.setVisibility(View.VISIBLE);
    }

    private void setImageView(ImageView paramImageView, int paramInt) {
        if (paramInt == 0)
            return;
        paramImageView.setImageResource(paramInt);
        paramImageView.setVisibility(View.VISIBLE);
    }


    public void setTitleInfoWithText(int paramInt1) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.title, this, true);
        wrapLayout = (RelativeLayout) findViewById(R.id.RE_Wrap);
        leftTextView = (TextView) view.findViewById(R.id.tv_title_name);
        setTextView(leftTextView, paramInt1);
    }
    public void setTitleInfoWithText(int paramInt1, int paramInt2, View.OnClickListener paramOnClickListener) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.title, this, true);
        wrapLayout = (RelativeLayout) findViewById(R.id.RE_Wrap);
        leftTextView = (TextView) view.findViewById(R.id.tv_title_name);
        leftImageView = (ImageView) view.findViewById(R.id.img_title_logo);
        rightImageView = ((ImageView) view.findViewById(R.id.img_title_set));
        rightLayout = (LinearLayout) view.findViewById(R.id.ll_title_set);
        leftImageView.setVisibility(View.GONE);
        setTextView(leftTextView, paramInt1);
        setButton(rightImageView, paramInt2, paramOnClickListener);
    }

    private void setTextView(TextView paramTextView, int paramInt) {
        if (paramInt == 0)
            return;
        paramTextView.setText(paramInt);
        paramTextView.setVisibility(View.VISIBLE);
    }

}
