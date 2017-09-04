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

public class ErrorMaskView extends RelativeLayout implements View.OnClickListener {
    private static final int STATUS_EMPTY = 1;
    private static final int STATUS_ERROR = 3;
    private static final int STATUS_GONE = 0;
    private static final int STATUS_LOADING = 2;
    private Context mContext;
    private ImageView mIconImage;
    private LinearLayout mProgressLayout;
    private TextView mProgressText;
    private OnClickListener mRetryClickListener;
    private TextView mRetryTitleText;
    private int mStatus;
    private TextView mSubTitleText;
    private LinearLayout mTextLayout;
    private TextView mTitleText;
    private TextView stopLoading;

    public ErrorMaskView(Context paramContext) {
        super(paramContext);
        initView(paramContext);
    }

    public ErrorMaskView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initView(paramContext);
    }

    public ErrorMaskView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initView(paramContext);
    }

    private void hide() {
        if (getVisibility() != View.GONE)
            setVisibility(View.GONE);
        this.mStatus = 0;
    }

    private void initView(Context paramContext) {
        this.mContext = paramContext;
        ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R.layout.mask_error_layout, this);
        this.mTextLayout = ((LinearLayout) findViewById(R.id.textLayout));
        this.mIconImage = ((ImageView) findViewById(R.id.icon));
        this.mTitleText = ((TextView) findViewById(R.id.title));
        this.mSubTitleText = ((TextView) findViewById(R.id.subTitle));
        this.mRetryTitleText = ((TextView) findViewById(R.id.retryTitle));
        this.mProgressLayout = ((LinearLayout) findViewById(R.id.progressLayout));
        this.mProgressText = ((TextView) findViewById(R.id.progressTitle));
        this.stopLoading = (TextView)findViewById(R.id.stopLoading);

        hide();
        this.mRetryTitleText.setOnClickListener(this);
        this.stopLoading.setOnClickListener(this);
    }

    public void show() {
        if (getVisibility() == View.VISIBLE)
            return;
        setVisibility(View.VISIBLE);
    }

    public void onClick(View paramView) {
        switch (paramView.getId()){
            case R.id.retryTitle:
                if ((paramView == null) || (this.mStatus != STATUS_ERROR) || (this.mRetryClickListener == null))
                    return;
                //setLoadingStatus();
                this.mRetryClickListener.onClick(paramView);
                break;
            case R.id.stopLoading:
                setStopLoading();
                break;
        }


    }

    public void setEmptyStatus() {
        setEmptyStatus(this.mContext.getString(R.string.empty_list));
    }

    public void setEmptyStatus(int paramInt) {
        setEmptyStatus(this.mContext.getString(paramInt));
    }

    public void setEmptyStatus(int paramInt1, int paramInt2) {
        show();
        String str1 = "";
        if (paramInt1 != 0)
            str1 = this.mContext.getString(paramInt1);
        String str2 = "";
        if (paramInt2 != 0)
            str2 = this.mContext.getString(paramInt2);
        this.mProgressLayout.setVisibility(View.GONE);
        this.mTextLayout.setVisibility(View.VISIBLE);
        this.mSubTitleText.setVisibility(View.GONE);
        this.mIconImage.setImageBitmap(DrawableToBitmap.emptyIcon(mContext));
        if (null != str1) {    //!StringUtils.equalsNull(str1)
            this.mTitleText.setVisibility(View.VISIBLE);
            this.mTitleText.setText(str1);
        }
        this.mStatus = STATUS_EMPTY;
    }

    public void setEmptyStatus(String paramString) {
        show();
        this.mProgressLayout.setVisibility(View.GONE);
        this.mTextLayout.setVisibility(View.VISIBLE);
        this.mSubTitleText.setVisibility(View.GONE);
        this.mIconImage.setImageBitmap(DrawableToBitmap.emptyIcon(mContext));
        if (null != paramString) {   //!StringUtils.equalsNull(paramString)
            this.mTitleText.setVisibility(View.VISIBLE);
            this.mTitleText.setText(paramString);
        }
        this.mRetryTitleText.setVisibility(View.GONE);
        this.mStatus = STATUS_EMPTY;
    }

    public void setErrorStatus(boolean isNoData, int paramInt) {
        setErrorStatus(isNoData, this.mContext.getString(paramInt));
    }

    public void setErrorStatus(boolean isNoData, String paramString) {
        setErrorStatus(isNoData, paramString, null);
    }

    public void setErrorStatus(boolean isNoData, String paramString1, String paramString2) {
        show();
        this.mProgressLayout.setVisibility(View.GONE);
        this.mTextLayout.setVisibility(View.VISIBLE);
        this.mRetryTitleText.setVisibility(View.VISIBLE);
        this.mIconImage.setVisibility(View.VISIBLE);
        if (isNoData) {
            this.mIconImage.setImageBitmap(DrawableToBitmap.emptyIcon(mContext));
        } else {
            this.mIconImage.setImageBitmap(DrawableToBitmap.netIcon(mContext));
        }
        if (null != paramString1) {     //!StringUtils.equalsNull(paramString1)
            this.mTitleText.setVisibility(View.VISIBLE);
            this.mTitleText.setText(paramString1);
        }
        this.mStatus = STATUS_ERROR;
    }

    public void setLoadingStatus() {
        setLoadingStatus(this.mContext.getString(R.string.loading));
    }

    public void setLoadingStatus(int paramInt) {
        setLoadingStatus(this.mContext.getString(paramInt));
    }

    public void setLoadingStatus(String paramString) {
        show();
        this.mTextLayout.setVisibility(View.GONE);
        this.mProgressLayout.setVisibility(View.VISIBLE);
        if (null != paramString) {   //!StringUtils.equalsNull(paramString)
            this.mProgressText.setVisibility(View.VISIBLE);
            this.mProgressText.setText(paramString);
        }
        this.mStatus = STATUS_LOADING;
    }

    public void setStopLoading(){
        hide();
    }

    public void setOnRetryClickListener(View.OnClickListener paramOnClickListener) {
        setOnRetry(-1,paramOnClickListener);
    }

    public void setOnRetry(int paramInt,View.OnClickListener paramOnClickListener) {
        paramInt = paramInt==-1?R.string.clickRefresh:paramInt;
        this.mRetryTitleText.setText(paramInt);
        this.mRetryClickListener = paramOnClickListener;

    }

    public void setVisibleGone() {
        hide();
    }
}
