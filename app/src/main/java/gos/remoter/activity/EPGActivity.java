package gos.remoter.activity;

import gos.remoter.define.CS;//静态常量
import gos.remoter.R;
import gos.remoter.event.EventManager;
import gos.remoter.adapter.EpgAdapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.support.odps.udf.CodecCheck;

import java.util.ArrayList;

public class EPGActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EpgAdapter epgAdapter = null;//自己消息适配器
    private ArrayList<Object> data = null;//聊天列表属性集合
    private ListView progListView = null;//节目内容列表
    private Spinner spin_tvName;//频道列表
    private Spinner spin_tvDate;//频道日期列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        Toolbar epg_toolbar = (Toolbar)findViewById(R.id.epg_TOOLBAR);
        setSupportActionBar(epg_toolbar);//用Toolbar代替ActionBar
        EventManager.register(this);//注册EventManager

        init_adapter();//完成初始化EpgAdapter
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
        data = new ArrayList<>();
        spin_tvName = (Spinner)findViewById(R.id.epg_mainTVName);
        spin_tvDate = (Spinner)findViewById(R.id.epg_mainTVDate);
        progListView = (ListView)findViewById(R.id.epg_mainProgList);//频道中的全部节目
        //初始化适配器
        epgAdapter = new EpgAdapter(context, data);
        //使用适配器
        progListView.setAdapter(epgAdapter);
        spin_tvName.setAdapter(epgAdapter);
        spin_tvDate.setAdapter(epgAdapter);
        Log.e(CS.EPGACT_TAG, CS.EPGACT_INITADA_SUCEESS);//初始化适配器成功
        spin_tvName.setOnItemSelectedListener(this);
        spin_tvDate.setOnItemSelectedListener(this);
    }

    //选中后触发动作
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            //选中频道名列表
            case R.id.epg_spinner_TVName: {
                break;
            }
            //选中日期列表
            case R.id.epg_spinner_TVDate: {
                break;
            }
            default: {
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //没选中时的动作
    }

    //事件接收器
//    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
//    public void onReceiveEvent(EventMsg msg){
//        if(msg.getEventMode() == EventMode.OUT) {
//            //发送给服务器
//            return ;
//        }
//
//        //接收消息
//        switch (msg.getCommand()){
//            //设置节目列表
//            case COM_LIVE_SET_PROGRAM_LIST: {
//                setProgramList(parseProgramData(msg.getData()));
//                aotuSet = false;
//                if (-1 == curProPosition) {
//                    proItemClick(0);
//                }
//                break;
//            }
//            //设置选择的节目EPG信息
//            case COM_EPG_SET_SELECT_PROGRAM: {
//                setDateList(parseDateData(msg.getData()));
//                if (aotuSet) {
//                    Log.i(TAG, "自动设置date");
//                    dateItemClick(curDatePosition);
//                } else if (-1 == curDatePosition) {
//                    dateItemClick(0);
//                }
//                break;
//            }
//            //系统回应
//            case COM_SYSTEM_RESPOND: {
//                Respond respond = DataParse.getRespond(msg.getData());
//                switch (respond.getCommand()) {
//                    case COM_CONNECT_DETACH:
//                        if (respond.getFlag()) {
//                            detach();
//                        }
//                        break;
//                    case COM_CONNECT_ATTACH:
//                        if (respond.getFlag()) {
//                            attach();
//                        }
//                        break;
//                    case COM_EPG_SET_RESERVE:
//                        if (respond.getFlag()) {
//                            aotuSet = true;
//                            getSelectEpgInfo(curProgram.getIndex());
//                        }
//                    default:
//                        break;
//                }
//                break;
//            }
//            //epg预定事件撞车冲突
//            case COM_EPG_CLASH_RESERVE: {break;}
//            //其他保留情况
//            default: {break;}
//        }
//    }
}
