package gos.remoter.activity;

import gos.remoter.adapter.Epg_progItem;
import gos.remoter.adapter.Epg_TVDate;
import gos.remoter.adapter.Epg_TVName;
import gos.remoter.define.CS;//静态常量
import gos.remoter.R;
import gos.remoter.event.EventManager;
import gos.remoter.adapter.Epg_progItemAdapter;
import gos.remoter.adapter.Epg_TVDateAdapter;
import gos.remoter.adapter.Epg_TVNameAdapter;

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
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;

import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import java.util.ArrayList;

public class EPGActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Epg_progItemAdapter epg_progItemAdapter = null;//节目内容适配器
    private Epg_TVNameAdapter epg_tvNameAdapter = null;//频道号和日期适配器
    private Epg_TVDateAdapter epg_tvDateAdapter = null;//频道号和日期适配器
    private ArrayList<Epg_progItem> ItemData = null;//节目内容列表属性集合
    private ArrayList<Epg_TVName> tvNameData = null;//频道号下拉列表属性集合
    private ArrayList<Epg_TVDate> tvDateData = null;//频道号下拉列表属性集合

    private ListView progListView = null;//节目内容列表
    private Spinner spin_tvName;//频道号下拉列表
    private Spinner spin_tvDate;//频道日期下拉列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        Toolbar epg_toolbar = (Toolbar)findViewById(R.id.epg_TOOLBAR);
        setSupportActionBar(epg_toolbar);//用Toolbar代替ActionBar
        EventManager.register(this);//注册EventManager

        init_adapter();//完成初始化EpgAdapter
        epg_tvNameAdapter.add(new Epg_TVName("CCTV 2"));
        epg_tvDateAdapter.add(new Epg_TVDate("2017-09-08"));
        epg_progItemAdapter.add(new Epg_progItem("BBC News", "9:00-10:00", "this is BBC News!",
                R.drawable.epg_recordbtn_once, R.drawable.epg_recordbtn_cycle,
                R.drawable.epg_watchbtn_once, R.drawable.epg_watchbtn_cycle,
                R.drawable.epg_recbtn_once_pressed, R.drawable.epg_recbtn_cycle_pressed,
                R.drawable.epg_watchbtn_once_pressed, R.drawable.epg_watchbtn_cycle_pressed));
        Log.e("来自MainAcitity的消息", "添加属列表属性成功");
    }
    @Override
    public void onDestroy() {
        Log.e(CS.EPG_TAG, CS.EPG_ONDESTROY);//流程顺序索引
        super.onDestroy();
        EventManager.unregister(this);//取消注册event接收器
    }
    //初始化EpgAdapter
    public void init_adapter() {
        Context context = EPGActivity.this;
        ItemData = new ArrayList<Epg_progItem>();
        tvNameData = new ArrayList<Epg_TVName>();
        tvDateData = new ArrayList<Epg_TVDate>();

        spin_tvName = (Spinner)findViewById(R.id.epg_mainTVName);
        spin_tvDate = (Spinner)findViewById(R.id.epg_mainTVDate);
        progListView = (ListView)findViewById(R.id.epg_mainProgList);//频道中的全部节目
        //初始化适配器
        epg_progItemAdapter = new Epg_progItemAdapter(context, ItemData);
        epg_tvNameAdapter = new Epg_TVNameAdapter(context, tvNameData);
        epg_tvDateAdapter = new Epg_TVDateAdapter(context, tvDateData);

        //使用适配器
        progListView.setAdapter(epg_progItemAdapter);
        spin_tvName.setAdapter(epg_tvNameAdapter);
        spin_tvDate.setAdapter(epg_tvDateAdapter);
        Log.e(CS.EPGACT_TAG, CS.EPGACT_INITADA_SUCEESS);//初始化适配器成功
        spin_tvName.setOnItemSelectedListener(this);
        spin_tvDate.setOnItemSelectedListener(this);
    }

    //选中后触发动作
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //选中列表
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //没选中时的动作
    }

    //事件接收器
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg) {
        //接收消息
    }
}
