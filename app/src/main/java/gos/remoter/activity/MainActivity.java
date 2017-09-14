package gos.remoter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.adapter.TabMenuAdapter;
import gos.remoter.data.IndexClass;
import gos.remoter.define.*;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.fragment.ConnectFragment;
import gos.remoter.fragment.EpgFragment;
import gos.remoter.fragment.RemoteFragment;
import gos.remoter.fragment.LiveFragment;

import static gos.remoter.define.CommandType.*;   //导入静态命令集

public class MainActivity extends FragmentActivity {
    private  final String TAG = this.getClass().getSimpleName();
    private FragmentTabHost mTabHost;
    private ViewPager vPager = null;
    private TabMenuAdapter tabMenuAdapter = null;

    AlertDialog alert = null;
    AlertDialog.Builder builder = null;

    TabMenuManager tabMenuManager =  new TabMenuManager();
    RadioGroup menuBar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventManager.register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        EventManager.unregister(this);
    }

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_SYS_JUMP_CONNECT:
                    jumpToFragment(2);//跳转到连接界面
                    break;
                case COM_SYS_JUMP_LIVE:
                    jumpToFragment(1);//跳转到直播界面
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 初始化页面
     */
    void initView(){
        initTabMenu();
        initViewpager();
        initExitDialog();
        initState();
    }

    /**
     * 初始化底部菜单组件
     */
    void initTabMenu() {
        tabMenuManager.addTabMenuItem(R.string.tab_menu_remote, R.drawable.tab_menu_remote,RemoteFragment.class);
        tabMenuManager.addTabMenuItem(R.string.tab_menu_live, R.drawable.tab_menu_live,LiveFragment.class);
        tabMenuManager.addTabMenuItem(R.string.tab_menu_connect, R.drawable.tab_menu_connect,ConnectFragment.class);
        tabMenuManager.addTabMenuItem(R.string.tab_menu_epg, R.drawable.tab_menu_epg,EpgFragment.class);

        menuBar = (RadioGroup) findViewById(R.id.tab_menu);
        menuBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(null == vPager) {
                    return;
                }
                int position = -1;
                switch (checkedId){
                    case R.id.tab_menu_home:
                        position = 0;
                        break;
                    case R.id.tab_menu_live:
                        position = 1;
                        break;
                    case R.id.tab_menu_connect:
                        position = 2;
                        break;
                    case R.id.tab_menu_epg:
                        position = 3;
                        break;
                    default:
                        break;
                }
                if(-1 != position)
                {
                    vPager.setCurrentItem(position);
                }
            }
        });
    }

    /**
     * 初始化页面切换组件
     */
    void initViewpager()
    {
        ArrayList<Fragment> list = new ArrayList<>();

        LiveFragment liveFragment = new LiveFragment();
        RemoteFragment remoteFragment = new RemoteFragment();
        ConnectFragment connectFragment = new ConnectFragment();
        EpgFragment epgFragment = new EpgFragment();

        list.add(remoteFragment);
        list.add(liveFragment);
        list.add(connectFragment);
        list.add(epgFragment);
        tabMenuAdapter= new TabMenuAdapter(getSupportFragmentManager());
        tabMenuAdapter.setFragmentList(list);

        vPager = (ViewPager) findViewById(R.id.vpager);
        vPager.setAdapter(tabMenuAdapter);
        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               // Log.i(TAG,"onPageScrolled:"+position);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected:"+position);
                if(null != menuBar){
                    RadioButton curButton =    (RadioButton) menuBar.getChildAt(position);
                    curButton.setChecked(true);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.i(TAG,"state:"+state);
            }
        });
        jumpToFragment(2);//跳转到连接界面
    }


    /**
     * 监听back键
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG,"KEYCODE_BACK");
            openExitDialog();
        }

        return false;
    }

    /**
     * 打开退出对话框
     */
    private void openExitDialog() {
        if(null != alert){
            if(!alert.isShowing()) {
                alert.show();
            }
        }
    }

    /**
     * 退出对话框
     */
    void initExitDialog(){

        alert = null;
        builder = new AlertDialog.Builder(this);
        alert = builder.setIcon(R.drawable.ic_warning)
                .setTitle(R.string.exit_dialog_title)
                .setMessage(R.string.exit_dialog_content)
                .setNegativeButton(R.string.exit_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.exit_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSysExit();

                        //延时300ms结束
                        (new Timer()).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },300);

                    }
                }).create();             //创建AlertDialog对象
    }


    private void jumpToFragment(int index){
        vPager.setCurrentItem(index);//跳转到连接界面
    }

    private void sendSysExit(){
        EventManager.send(COM_SYS_EXIT,"",EventMode.IN);
    }

    public void remoterFuncOnClick(View source) {
        IndexClass indexClass = new IndexClass(source.getId());
        EventManager.send(COM_SYS_REMOTE_ID, JSON.toJSONString(indexClass),EventMode.IN);
    }


    /**
     * 沉浸式状态栏
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
