package gos.remoter.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.adapter.HomePagerAdapter;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.adapter.ScaleTransformer;
import gos.remoter.data.Advertisement;
import gos.remoter.data.GridActivity;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_GET_AD;
import static gos.remoter.define.CommandType.COM_SET_AD;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;
import static gos.remoter.define.CommandType.COM_SYS_EXIT;
import static gos.remoter.define.CommandType.COM_SYS_HEARTBEAT_STOP;


public class HomeActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;
    private GridView gridView;

    AlertDialog logoutAlert;
    long firstTime;//保存第一次按退出键的时间

    // 判断是否自动滚动viewPager
    private boolean isRunning = true;
    private int curPosition = 1;
    private ArrayList<Advertisement> adList = null;//广告图片地址
    private List<Integer> imageList = null;//本地图片
    private ArrayList<Bitmap> adBitmap = null;//下载后的图片
    private String[] ads = null;

    private Handler pagerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 执行滑动到下一个页面
                    if (isRunning) {
                        if(ads == null && viewPager.getCurrentItem() == imageList.size() -1) {
                            //Log.e(TAG, viewPager.getCurrentItem() + "--imageList---CurrentItem--000000");
                            viewPager.setCurrentItem(0, true);
                        } else if(ads != null && viewPager.getCurrentItem() == ads.length -1) {
                            viewPager.setCurrentItem(0, false);
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        }
                        // 再发一个handler延时
                        pagerHandler.sendEmptyMessageDelayed(0, 2000);
                    }
                    break;
            }
        }
    };

    ReuseAdapter<GridActivity> gridAdapter = new ReuseAdapter<GridActivity>(R.layout.item_home_grid) {
        @Override
        public void bindView(ViewHolder holder, GridActivity obj, int position) {

            holder.setText(R.id.txt_icon,getResources().getString(obj.getName()) );
            holder.setImageResource(R.id.img_icon,obj.getIcon());
        }
    };

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_SYS_HEARTBEAT_STOP:
                    detach();
                    break;
                case COM_SET_AD: //广告
                    parseAdDate(msg.getData());
                    initImageData();
                    break;
                case COM_SYSTEM_RESPOND:{    //回应
                    Respond respond = DataParse.getRespond(msg.getData());
                    switch (respond.getCommand()) {
                        case COM_CONNECT_DETACH:
                            if (respond.getFlag()) {
                                detach();
                            } else {
                                Log.i(TAG, "断开连接失败");
                            }
                            break;
                        case COM_CONNECT_ATTACH:
                            if (respond.getFlag()) {
                                //attach();
                                getAD();
                            } else {
                                Log.i(TAG, "连接设备失败");
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case COM_SYS_EXIT:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.gc();
        ACTCollector.add(this);//添加到收集器
        EventManager.register(this);

        initView();

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"销毁");
        sendDetachDevice();

        isRunning = false;
        //ACTCollector.remove(this);//从收集器移除
        if(ACTCollector.isEmpty()){
            sendExitSystem();
        }
        EventManager.unregister(this);
        super.onDestroy();
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if(System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, R.string.exit2, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                exitSystem();
            }
            return true; //不返回，一次就立马退出，
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            logoutAlert();
        }
        return super.onKeyDown(keyCode, event);
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();//影藏状态栏

        //标题栏
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setBackgroundColor(Color.parseColor("#e93700"));

        titleBar.setTextTitle(R.string.homeTitle);
        /*titleBar.setImageRight(R.drawable.home_logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                logoutAlert();
            }
        });*/

        getAD();
        initViewPager();
        initGridView();

    }

    private void initGridView() {
        gridAdapter.add(new GridActivity(LiveListActivity.class,R.drawable.new_home_live,R.string.home_live));
        gridAdapter.add(new GridActivity(RemoterActivity.class,R.drawable.new_home_remote,R.string.home_remoter));
        gridAdapter.add(new GridActivity(ProgramActivity.class,R.drawable.new_home_list,R.string.home_program_list));
        gridAdapter.add(new GridActivity(EpgActivity.class,R.drawable.new_home_epg,R.string.home_epg));
        gridAdapter.add(new GridActivity(null,R.drawable.new_home_edit,R.string.home_edit_program));
        gridAdapter.add(new GridActivity(TimeSwitchActivity.class,R.drawable.new_home_switch,R.string.home_timing));
        gridAdapter.add(new GridActivity(null,R.drawable.new_home_mail,R.string.home_email));
        gridAdapter.add(new GridActivity(null,R.drawable.new_home_setting,R.string.home_setting));
        gridAdapter.add(new GridActivity(HelpActivity.class,R.drawable.new_home_help,R.string.home_help));

        gridView = (GridView)findViewById(R.id.gridActivity);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridActivity selectActivity = gridAdapter.getItem(position);
                if(selectActivity.getActivity()==null){
                    String info =  getResources().getString(R.string.home_info);
                    Toast.makeText(HomeActivity.this, info, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(HomeActivity.this,selectActivity.getActivity());
                startActivity(intent);
            }
        } );
    }

    /**
     *  ViewPager轮播
     */
    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setPageMargin(5);
        viewPager.setOffscreenPageLimit(3);//默认为1时，左右各预加载一页
        viewPager.setPageTransformer(false, new ScaleTransformer(this));

        initImageData();
        viewPager.setCurrentItem(curPosition);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /**
             * 页面切换后调用， position是新的页面位置
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                // 实现无限制循环播放
                if(ads != null && ads.length > 1) {
                    //Log.e(TAG, position + "ads--position--000000");
                    position %= ads.length;
                    curPosition = position;
                } else if(imageList.size() > 1) {
                    position %= imageList.size();
                    curPosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 滑动状态改变的方法 state :draaging 拖拽 idle 静止 settling 惯性过程
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    viewPager.setCurrentItem(curPosition, false);// 设置当前页,smoothScroll 平稳滑动
                }
            }
        });

        /**
         * 自动循环： 1.定时器：Timer 2.开子线程：while true循环
         * 3.ClockManger ; 4.用Handler发送延时信息，实现循环，最简单最方便
         */
        pagerHandler.sendEmptyMessageDelayed(0, 2000);

    }

    private void initImageData() {
        if(null == ads) {
//            Log.e(TAG, "adList == null" + "-----adList-adList-adList");
            imageList = new ArrayList<>();
            imageList.add(R.drawable.home_pager01);
            imageList.add(R.drawable.home_pager02);
            imageList.add(R.drawable.details_bg_window);
            imageList.add(R.drawable.home_pager03);
            imageList.add(R.drawable.home_pager04);

            pagerAdapter = new HomePagerAdapter(this, imageList);
        } else {
            Log.e(TAG, "adList != null--" + ads.length);
            pagerAdapter = new HomePagerAdapter(this, ads);
        }
        viewPager.setAdapter(pagerAdapter);

    }

    private void detach(){
        Log.i(TAG,"断开连接成功");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemInfo.getInstance().setState(SystemState.DETACH);
    }

    /**
     * 解析广告数据，新建一个字符串数组存储广告信息
     * @param data
     * @return
     */
    private String[] parseAdDate(String data) {
        adList = new ArrayList<>();
        adList = DataParse.getAdList(data);
//        Log.e(TAG,"adList:\n"+ JSON.toJSONString(adList));
        int i = 0;
        ads = new String[adList.size()];
        for(Advertisement ad : adList) {
            ads[i++] = ad.getAdUrl();
        }
        return ads;
    }

    /**
     * 延时500 毫秒关闭
     */
    void delayFinish(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        },500);
    }

    void initLogoutAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        logoutAlert = builder.setIcon(R.drawable.home_logout_black)
                .setTitle(R.string.homeTitle)
                .setMessage(R.string.home_alert_msg)
                .setNegativeButton(R.string.home_alert_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutAlert.dismiss();
                    }
                })
                .setPositiveButton(R.string.home_alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDetachDevice();
                        //restartConnect();
                        exitSystem();
                        ACTCollector.removeAll();
                    }
                }).create();
    }

    void logoutAlert(){
        if(null == logoutAlert){
            initLogoutAlert();
        }
        logoutAlert.show();
    }

    /**
     * 获取广告信息
     */
    private void getAD() {
        Log.i(TAG,"获取广告信息:");
        EventManager.send(COM_GET_AD, "", EventMode.OUT);
    }

    //断开与服务器的连接
    private void sendDetachDevice(){
        if(SystemState.ATTACH == SystemInfo.getInstance().getState()) {
            Log.e(TAG,"发送断开与服务器的连接");
            SystemInfo.getInstance().setState(SystemState.DETACH);

            EventManager.send(COM_CONNECT_DETACH, "", EventMode.OUT);
        }
    }

    void exitSystem(){
        if(SystemState.ATTACH == SystemInfo.getInstance().getState()) {
            sendDetachDevice();
        }
        sendExitSystem();
    }

    /**
     * 退出系统
     */
    void sendExitSystem(){
        if(SystemState.EXIT != SystemInfo.getInstance().getState()) {
            Log.e(TAG,"发送退出系统");
            SystemInfo.getInstance().setState(SystemState.EXIT);
            EventManager.send(COM_SYS_EXIT,"",EventMode.IN);
        }
    }

}
