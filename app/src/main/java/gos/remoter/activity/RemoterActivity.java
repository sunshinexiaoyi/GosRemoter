package gos.remoter.activity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import gos.remoter.R;
import gos.remoter.adapter.RemoterSetting;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

public class RemoterActivity extends Activity implements OnClickListener {


    private ImageView remoteBack;
    private ImageView remoteOnOff;
    private ImageView remoteMute;
    private ImageView remoteNumber;
    private ImageView remoteMenu;
    private ImageView remoteFav;
    private ImageView remotePvr;
    private ImageView remoteExit;
    private TitleBarNew titleBar;
    private RemoterSetting remoterSetting;

    private Button numberOne;
    private Button numberTwo;
    private Button numberThree;
    private Button numberFour;
    private Button numberFive;
    private Button numberSix;
    private Button numberSeven;
    private Button numberEight;
    private Button numberNine;
    private Button numberBack;
    private Button numberZero;
    private Button numberOk;
    private View viewNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remoter);
        initView();

        init();
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setAlpha(255);

        titleBar.setTextTitle(R.string.remoter_title);
        titleBar.setImageLeft(R.drawable.remote_return, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setImageRight(R.drawable.remoter_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                Toast.makeText(RemoterActivity.this, "waiting...", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void init() {
        remoteBack = (ImageView) findViewById(R.id.remoteBack);
        remoteOnOff = (ImageView) findViewById(R.id.remoteOnOff);
        remoteMute = (ImageView) findViewById(R.id.remoteMute);
        remoteNumber = (ImageView) findViewById(R.id.remoteNumber);
        remoteMenu = (ImageView) findViewById(R.id.remoteMenu);
        remoteFav = (ImageView) findViewById(R.id.remoteFav);
        remotePvr = (ImageView) findViewById(R.id.remotePvr);
        remoteExit = (ImageView) findViewById(R.id.remoteExit);
        remoterSetting = (RemoterSetting) findViewById(R.id.remoteSet);

        remoteBack.setOnClickListener(this);
        remoteOnOff.setOnClickListener(this);
        remoteMute.setOnClickListener(this);
        remoteNumber.setOnClickListener(this);
        remoteMenu.setOnClickListener(this);
        remoteFav.setOnClickListener(this);
        remotePvr.setOnClickListener(this);
        remoteExit.setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remoteBack:
                break;
            case R.id.remoteOnOff:
                break;
            case R.id.remoteMute:
                break;
            case R.id.remoteNumber:
                initPopWindows(view);

                break;
            case R.id.remoteMenu:
                break;
            case R.id.remoteFav:
                break;
            case R.id.remotePvr:
                break;
            case R.id.remoteExit:
                break;

            case R.id.numberOne:
                Toast.makeText(this, "反应", Toast.LENGTH_SHORT).show();
                break;
            case R.id.numberTwo:
                break;
            case R.id.numberThree:
                break;
            case R.id.numberFour:
                break;
            case R.id.numberFive:
                break;
            case R.id.numberSix:
                break;
            case R.id.numberSeven:
                break;
            case R.id.numberEight:
                break;
            case R.id.numberNine:
                break;
            case R.id.numberZero:
                break;
            case R.id.numberBack:
                break;
            case R.id.numberOk:

                break;
        }

    }

    // PopupWindow(悬浮框)
    private void initPopWindows(View view) {
        viewNumber = LayoutInflater.from(this).inflate(R.layout.remoter_number_dialog, null, false);
        initNumber();

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(viewNumber,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //为了点击非PopupWindow区域，PopupWindow会消失
        //避免无论按多少次后退键，PopupWindow都不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return false;// 返回true，touch事件将被拦截，拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x6f000000));
        //设置popupWindow显示的位置，参数依次是参照View，相对于父控件的位置,x轴的偏移量，y轴的偏移量
        popWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
       // popWindow.dismiss();

    }

    private void initNumber() {
        numberOne = (Button) viewNumber.findViewById(R.id.numberOne);
        numberTwo = (Button) viewNumber.findViewById(R.id.numberTwo);
        numberThree = (Button) viewNumber.findViewById(R.id.numberThree);
        numberFour = (Button) viewNumber.findViewById(R.id.numberFour);
        numberFive = (Button) viewNumber.findViewById(R.id.numberFive);
        numberSix = (Button) viewNumber.findViewById(R.id.numberSix);
        numberSeven = (Button) viewNumber.findViewById(R.id.numberSeven);
        numberEight = (Button) viewNumber.findViewById(R.id.numberEight);
        numberNine = (Button) viewNumber.findViewById(R.id.numberNine);
        numberZero = (Button) viewNumber.findViewById(R.id.numberZero);
        numberBack = (Button) viewNumber.findViewById(R.id.numberBack);
        numberOk = (Button) viewNumber.findViewById(R.id.numberOk);

        //设置popupWindow里的按钮的事件
        numberOne.setOnClickListener(this);
        numberTwo.setOnClickListener(this);
        numberThree.setOnClickListener(this);
        numberFour.setOnClickListener(this);
        numberFive.setOnClickListener(this);
        numberSix.setOnClickListener(this);
        numberSeven.setOnClickListener(this);
        numberEight.setOnClickListener(this);
        numberNine.setOnClickListener(this);
        numberZero.setOnClickListener(this);
        numberBack.setOnClickListener(this);
        numberOk.setOnClickListener(this);
    }

}
