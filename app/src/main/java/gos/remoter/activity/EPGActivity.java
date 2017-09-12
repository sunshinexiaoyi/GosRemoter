package gos.remoter.activity;

import gos.remoter.adapter.Epg_progItem;
import gos.remoter.adapter.Epg_TVDate;
import gos.remoter.adapter.Epg_TVName;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.ReserveEventSend;
import gos.remoter.define.CS;//静态常量
import gos.remoter.R;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.adapter.Epg_myAdapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.odps.udf.CodecCheck;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import static gos.remoter.define.CommandType.*;   //导入静态命令集

import java.util.ArrayList;

public class EPGActivity extends AppCompatActivity {
    private Epg_myAdapter epg_progItemAdapter = null;//节目内容适配器
    private Epg_myAdapter epg_tvNameAdapter = null;//频道号和日期适配器
    private Epg_myAdapter epg_tvDateAdapter = null;//频道号和日期适配器
    private ListView progListView;//列表view

    private ArrayList<Epg_progItem> itemData;//节目中的数据
    private ArrayList<Epg_TVName> tvNameData;//频道名字
    private ArrayList<Epg_TVDate> tvDateData;//频道日期

    private ArrayList<Program> programInfo;//得到的节目总信息


/*******************重写方法***********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        Toolbar epg_toolbar = (Toolbar)findViewById(R.id.epg_TOOLBAR);
        setSupportActionBar(epg_toolbar);//用Toolbar代替ActionBar
        EventManager.register(this);//注册EventManager

        init_adapter();//完成初始化EpgAdapter

        Log.e(CS.EPGACT_TAG, "向服务器获取节目数据");
        getProgramList();//获取第一个节目的所有信息，装载到列表中

/*****************测试列表**********************/
//        epg_tvNameAdapter.add(new Epg_TVName("CCTV 1"));
//        epg_tvNameAdapter.add(new Epg_TVName("CCTV 2"));
//        epg_tvDateAdapter.add(new Epg_TVDate("1995-12-25"));
//        epg_tvDateAdapter.add(new Epg_TVDate("1995-12-26"));
//        epg_progItemAdapter.add(new Epg_progItem("ABC NEWS", "10:00-11:00", "sdalsdlkgjsdhgosdghnjsdkghsdrjkgnasjdkloiasldkghnwaksjdlhhnsdfklgjvnadslk;ho;dsl",
//                R.drawable.epg_simple_recbtn_once_false, R.drawable.epg_simple_recbtn_cycle_false,
//                R.drawable.epg_simple_watchbtn_once_false, R.drawable.epg_simple_watchbtn_cycle_false,
//                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false,
//                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false));
//        epg_progItemAdapter.add(new Epg_progItem("BBC NEWS", "11:00-12:00", "errorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr",
//                R.drawable.epg_simple_recbtn_once_false, R.drawable.epg_simple_recbtn_cycle_false,
//                R.drawable.epg_simple_watchbtn_once_false, R.drawable.epg_simple_watchbtn_cycle_false,
//                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false,
//                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false));
//                Log.e("来自MainAcitity的消息", "添加属列表属性成功");
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

    //事件接收器
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg) {
        //接收消息为空，不做处理
        if(msg.getEventMode() == EventMode.OUT) {
            return;
        }

        //接收Event
        switch (msg.getCommand()){
            //设置节目列表event
            case COM_LIVE_SET_PROGRAM_LIST: {
                makeProgramList(msg.getData()); //拆分数据成类属性，将列表信息添加到节目号列表中
                break;
            }
            //设置选择的节目epg信息
            case COM_EPG_SET_SELECT_PROGRAM: {
                break;
            }
            //系统回应
            case COM_SYSTEM_RESPOND: {
                break;
            }
            default: {
                break;
            }
        }
    }

    /**节目号处理
     *
     *包括：
     * 1、获取所有节目号
     * 2、将所有节目号装载到节目号下拉列表中
     */
    private void makeProgramList(String data){
        programInfo = DataParse.getProgramList(data);//得到节目总信息
        String[] programList = new String[programInfo.size()];//得到一个合适的长度数组
        //将节目号提取出来
        for (int i = 0; i < programInfo.size(); i++) {
            programList[i] = programInfo.get(i).getName();
        }

        for (String program : programList) {
            epg_tvNameAdapter.add(new Epg_TVName(program));
        }
    }


/*****************初始化列表相关部分***********************/
    //初始化适配器
    public void init_adapter() {
        Context context = EPGActivity.this;
        //初始化数据
        itemData = new ArrayList<Epg_progItem>();
        tvNameData = new ArrayList<Epg_TVName>();
        tvDateData = new ArrayList<Epg_TVDate>();
        //找到各自列表的Id
        progListView = (ListView) findViewById(R.id.epg_mainProgList);
        Spinner spin_tvName = (Spinner) findViewById(R.id.epg_mainTVName);
        Spinner spin_tvDate = (Spinner) findViewById(R.id.epg_mainTVDate);
        //初始化适配器
        epg_progItemAdapter = new Epg_myAdapter<Epg_progItem>(context, itemData, R.layout.epg_progitem) {
            @Override
            public void bindView(Holder holder, Epg_progItem obj) {
                holder.setText(R.id.epg_progName, obj.getProgName());
                holder.setText(R.id.epg_progTime, obj.getPorgTime());

                holder.setTextMarquee(R.id.epg_simpleProgInfo);//长文本缩略处理
                View fullSetting = holder.getItem().findViewById(R.id.epg_full_ProgSetting);
                fullSetting.setVisibility(View.GONE);//隐藏详细设置

                holder.setText(R.id.epg_simpleProgInfo, obj.getProgInfo());
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

/*******************自定义点击事件***************************/
    //Item的点击事件
    private class ItemClick implements AdapterView.OnItemClickListener {
        private boolean[] isSpread;
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            if (isSpread == null) {
                int len = adapterView.getCount();
                Log.e("消息", "item行数：" + l);
                isSpread = new boolean[len];
            }
            View settingView = view.findViewById(R.id.epg_full_ProgSetting);
            Epg_myAdapter.Holder holder = (Epg_myAdapter.Holder)view.getTag();

            if (!isSpread[pos]) {
                Log.e("消息", "选中了第" + pos + "行， 显示详细设置");
                settingView.setVisibility(View.VISIBLE);//显示详细设置
                holder.setGONE(R.id.epg_simpleBtn);//隐藏按钮
                holder.setTextNormal(R.id.epg_simpleProgInfo);//去除文本框特殊属性
                holder.setTextHeight(R.id.epg_simpleProgInfo, ViewGroup.LayoutParams.WRAP_CONTENT);//改变高度
            } else {
                Log.e("消息", "选中了第" + pos + "行， 隐藏详细设置");
                settingView.setVisibility(View.GONE);//显示详细设置
                holder.setVISIBLE(R.id.epg_simpleBtn);//显示按钮
                holder.setTextMarquee(R.id.epg_simpleProgInfo);//加入文本框属性
            }
            isSpread[pos] = !isSpread[pos];
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
                    clickRecordOnce();
                    break;
                }
                case R.id.epg_simple_recBtnCycle:
                case R.id.epg_full_recBtnCycle: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定循环录制按钮");
                    clickRecordCycle();
                    break;
                }
                case R.id.epg_simple_watchBtnOnce:
                case R.id.epg_full_watchBtnOnce: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定观看一天按钮");
                    clickWatchOnce();
                    break;
                }
                case R.id.epg_simple_watchBtnCycle:
                case R.id.epg_full_watchBtnCycle: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定循环观看按钮");
                    clickWatchCycle();
                    break;
                }

                default: {
                    Log.e(CS.ADAPTER_TAG, "点中了其他按钮");
                    break;
                }
            }
        }

        //点击录制一次按钮
        public void clickRecordOnce() {
            //更改按钮样式
            if (!isRecOnce) {
                holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_true);
                holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_true);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);

                isRecCycle = false;
                isWatchOnce = false;
                isWatchCycle = false;
            } else {
                holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
            }
            isRecOnce = !isRecOnce;
        }
        //点击循环录制按钮
        public void clickRecordCycle() {
            //更改按钮样式
            if (!isRecCycle) {
                holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_true);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_true);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);

                isRecOnce = false;
                isWatchOnce = false;
                isWatchCycle = false;
            } else {
                holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
            }
            isRecCycle = !isRecCycle;
        }
        //点击观看一次按钮
        public void clickWatchOnce() {
            //更改按钮样式
            if (!isWatchOnce) {
                holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_true);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_true);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);

                isRecOnce = false;
                isRecCycle = false;
                isWatchCycle = false;
            } else {
                holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
            }
            isWatchOnce = !isWatchOnce;
        }
        //点击循环观看按钮
        public void clickWatchCycle() {
            //更改按钮样式
            if (!isWatchCycle) {
                holder.setBackgroundResource(R.id.epg_simple_recBtnOnce, R.drawable.epg_simple_recbtn_once_false);
                holder.setBackgroundResource(R.id.epg_simple_recBtnCycle, R.drawable.epg_simple_recbtn_cycle_false);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnOnce, R.drawable.epg_simple_watchbtn_once_false);
                holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_true);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_true);

                isRecOnce = false;
                isRecCycle = false;
                isWatchOnce = false;
            } else {
                holder.setBackgroundResource(R.id.epg_simple_watchBtnCycle, R.drawable.epg_simple_watchbtn_cycle_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);
            }
            isWatchCycle = !isWatchCycle;
        }
    }


/*****************向服务器要求获取数据***********************/
    //获取节目列表
    private void getProgramList() {
        Log.i(CS.EPGACT_TAG, "获取节目列表:");
        EventManager.send(COM_LIVE_GET_PROGRAM_LIST,"", EventMode.OUT);
    }

    //获取选中的节目epg信息
    private void getSelectEpgInfo(int index){
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_EPG_GET_SELECT_PROGRAM, JSON.toJSONString(indexClass), EventMode.OUT);
    }

    //发送预定事件设置
    private void sendReserveSet(ReserveEventSend reserveSet){
        EventManager.send(COM_EPG_SET_RESERVE,JSON.toJSONString(reserveSet), EventMode.OUT);

    }
}
