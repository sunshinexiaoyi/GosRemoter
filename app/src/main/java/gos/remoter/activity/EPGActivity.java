package gos.remoter.activity;

import gos.remoter.adapter.Epg_progItem;
import gos.remoter.adapter.Epg_TVDate;
import gos.remoter.adapter.Epg_TVName;
import gos.remoter.define.CS;//静态常量
import gos.remoter.R;
import gos.remoter.event.EventManager;
import gos.remoter.adapter.Epg_myAdapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import gos.remoter.event.EventMsg;

import java.util.ArrayList;

public class EPGActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Epg_myAdapter epg_progItemAdapter = null;//节目内容适配器
    private Epg_myAdapter epg_tvNameAdapter = null;//频道号和日期适配器
    private Epg_myAdapter epg_tvDateAdapter = null;//频道号和日期适配器
    private ListView progListView;//列表view


/*******************重写方法***********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        Toolbar epg_toolbar = (Toolbar)findViewById(R.id.epg_TOOLBAR);
        setSupportActionBar(epg_toolbar);//用Toolbar代替ActionBar
        EventManager.register(this);//注册EventManager

        init_adapter();//完成初始化EpgAdapter

/*****************测试列表**********************/
//        epg_tvNameAdapter.add(new Epg_TVName("CCTV 1"));
//        epg_tvNameAdapter.add(new Epg_TVName("CCTV 2"));
//        epg_tvDateAdapter.add(new Epg_TVDate("2017-09-08"));
//        epg_tvDateAdapter.add(new Epg_TVDate("2017-09-09"));
//        epg_progItemAdapter.add(new Epg_progItem("ABC News", "9:00-10:00", "this is ABC News!",
//                R.drawable.epg_simple_recbtn_once_false, R.drawable.epg_simple_recbtn_cycle_false,
//                R.drawable.epg_simple_watchbtn_once_false, R.drawable.epg_simple_watchbtn_cycle_false,
//                R.drawable.epg_full_btn_false, R.drawable.epg_full_btn_false,
//                R.drawable.epg_full_btn_false, R.drawable.epg_full_btn_false));
//
//        epg_progItemAdapter.add(new Epg_progItem("ABC News", "9:00-10:00", "this is ABC News!",
//                R.drawable.epg_simple_recbtn_once_false, R.drawable.epg_simple_recbtn_cycle_false,
//                R.drawable.epg_simple_watchbtn_once_false, R.drawable.epg_simple_watchbtn_cycle_false,
//                R.drawable.epg_full_btn_false, R.drawable.epg_full_btn_false,
//                R.drawable.epg_full_btn_false, R.drawable.epg_full_btn_false));
//        Log.e("来自MainAcitity的消息", "添加属列表属性成功");
/***********************************************/
    }
    @Override
    public void onDestroy() {
        Log.e(CS.EPG_TAG, CS.EPG_ONDESTROY);//流程顺序索引
        super.onDestroy();
        EventManager.unregister(this);//取消注册event接收器
        Log.e(CS.EPG_TAG, CS.EPGACT_UNREGISTER_EVENTMANAGER);//告知取消注册event
        Log.e(CS.EPGACT_TAG, CS.EPGACT_DEATH);//告知EPGACT被杀死
    }

    //选中后的触发动作
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //选中列表
    }
    //没选中时的触发动作
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //没选中时的动作
    }

    //事件接收器
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg) {
        //接收消息
    }


/*****************自定义方法***********************/
    //初始化适配器
    public void init_adapter() {
        Context context = EPGActivity.this;
        //初始化数据
        ArrayList<Epg_progItem> itemData = new ArrayList<Epg_progItem>();
        ArrayList<Epg_TVName> tvNameData = new ArrayList<Epg_TVName>();
        ArrayList<Epg_TVDate> tvDateData = new ArrayList<Epg_TVDate>();
        //找到各自列表的Id
        progListView = (ListView) findViewById(R.id.epg_mainProgList);
        Spinner spin_tvName = (Spinner) findViewById(R.id.epg_mainTVName);
        Spinner spin_tvDate = (Spinner) findViewById(R.id.epg_mainTVDate);
        //初始化适配器
        epg_progItemAdapter = new Epg_myAdapter<Epg_progItem>(context, itemData, R.layout.epg_progitem) {
            @Override
            public void bindView(Holder holder, Epg_progItem obj) {
                holder.setBackgroundResource(R.id.epg_simple_recBtnOnce,obj.getSimpleRecBtnOnce());
                holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, obj.getSimpleRecBtnCycle());
                holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, obj.getSimpleWatchBtnOnce());
                holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, obj.getSimpleWatchBtnCycle());
                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, obj.getRecBtnOnce());
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, obj.getRecBtnCycle());
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, obj.getWatchBtnOnce());
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, obj.getWatchBtnCycle());

                //item的监听
                holder.setItemOnClickLIstener(progListView, new ItemClick());

                //item中的按钮监听
                ButtonClick buttonClick = new ButtonClick(holder);
                holder.setOnClickListener(R.id.epg_simple_recBtnOnce, buttonClick);
                holder.setOnClickListener(R.id.epg_simple_recBtnCycle, buttonClick);
                holder.setOnClickListener(R.id.epg_simple_watchBtnOnce, buttonClick);
                holder.setOnClickListener(R.id.epg_simple_watchBtnCycle, buttonClick);
                holder.setOnClickListener(R.id.epg_full_recBtnOnce, buttonClick);
                holder.setOnClickListener(R.id.epg_full_recBtnCycle, buttonClick);
                holder.setOnClickListener(R.id.epg_full_watchBtnOnce, buttonClick);
                holder.setOnClickListener(R.id.epg_full_watchBtnCycle, buttonClick);
            }
        };
        epg_tvNameAdapter = new Epg_myAdapter<Epg_TVName>(context, tvNameData, R.layout.epg_spinner_tvname) {
            @Override
            public void bindView(Holder holder, Epg_TVName obj) {
                holder.setText(R.id.epg_TVName, obj.getTVName());
            }
        };
        epg_tvDateAdapter = new Epg_myAdapter<Epg_TVDate>(context, tvDateData, R.layout.epg_spinner_tvdate) {
            @Override
            public void bindView(Holder holder, Epg_TVDate obj) {
                holder.setText(R.id.epg_TVDate, obj.getTVDate());
            }
        };
        //列表使用适配器
        progListView.setAdapter(epg_progItemAdapter);
        spin_tvName.setAdapter(epg_tvNameAdapter);
        spin_tvDate.setAdapter(epg_tvDateAdapter);
        Log.e(CS.EPGACT_TAG, CS.EPGACT_INITADA_SUCEESS);//初始化适配器成功
    }

    //Item的点击事件
    private class ItemClick implements AdapterView.OnItemClickListener {
        private boolean[] isTouch;
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            if (isTouch == null) {
                l = adapterView.getCount();
                Log.e("消息", "item行数：" + l);
                isTouch = new boolean[(int)l];
            }
            View settingView = view.findViewById(R.id.epg_full_ProgSetting);

            if (!isTouch[pos]) {
                Log.e("消息", "选中了第" + pos + "行， 隐藏详细设置");
                settingView.setVisibility(View.GONE);
            } else {
                Log.e("消息", "选中了第" + pos + "行， 显示详细设置");
                settingView.setVisibility(View.VISIBLE);
            }
            isTouch[pos] = !isTouch[pos];
        }
    }

    //Item中的按钮被点击事件
    private class ButtonClick implements View.OnClickListener {
        //记录按钮状态
        private boolean isRecOnce = false;
        private boolean isRecCycle = false;
        private boolean isWatchOnce = false;
        private boolean isWatchCycle = false;

        private Epg_myAdapter.Holder holder;

        private ButtonClick(Epg_myAdapter.Holder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.epg_simple_recBtnOnce:
                case R.id.epg_full_recBtnOnce: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定录制一次按钮");
                    //更改按钮样式
                    if (!isRecOnce) {
                        holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_true);
                        holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);

                        holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_btn_true);
                        holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_btn_false);

                        isRecCycle = false;
                        isWatchOnce = false;
                        isWatchCycle = false;
                    } else {
                        holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_btn_false);
                    }
                    isRecOnce = !isRecOnce;
                    break;
                }
                case R.id.epg_simple_recBtnCycle:
                case R.id.epg_full_recBtnCycle: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定循环录制按钮");
                    //更改按钮样式
                    if (!isRecCycle) {
                        holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_true);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);

                        holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_btn_true);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_btn_false);

                        isRecOnce = false;
                        isWatchOnce = false;
                        isWatchCycle = false;
                    } else {
                        holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                        holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_btn_false);
                    }
                    isRecCycle = !isRecCycle;
                    break;
                }
                case R.id.epg_simple_watchBtnOnce:
                case R.id.epg_full_watchBtnOnce: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定观看一天按钮");
                    //更改按钮样式
                    if (!isWatchOnce) {
                        holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_true);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);

                        holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_btn_true);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_btn_false);

                        isRecOnce = false;
                        isRecCycle = false;
                        isWatchCycle = false;
                    } else {
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_btn_false);
                    }
                    isWatchOnce = !isWatchOnce;
                    break;
                }
                case R.id.epg_simple_watchBtnCycle:
                case R.id.epg_full_watchBtnCycle: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定循环观看按钮");
                    //更改按钮样式
                    if (!isWatchCycle) {
                        holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_true);

                        holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_btn_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_btn_true);

                        isRecOnce = false;
                        isRecCycle = false;
                        isWatchOnce = false;
                    } else {
                        holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);
                        holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_btn_false);
                    }
                    isWatchCycle = !isWatchCycle;
                    break;
                }

                default: {
                    Log.e(CS.ADAPTER_TAG, "点中了其他按钮");
                    break;
                }
            }
        }
    }
}
