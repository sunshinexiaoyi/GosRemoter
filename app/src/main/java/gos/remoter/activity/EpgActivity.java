package gos.remoter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.Date;
import gos.remoter.data.EpgProgram;
import gos.remoter.data.ExpandableTime;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.Respond;
import gos.remoter.data.Time;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class EpgActivity extends Activity {
    private  final String TAG = this.getClass().getSimpleName();

    Spinner programSpinner;
    Spinner dateSpinner;

    ArrayList<Program> programList ;
    ArrayList<Date> dateList;

    private EpgProgram epgProgram = null;
    ReuseAdapter<Program> programAdapter = new ReuseAdapter<Program>(R.layout.item_epg_spinner) {
        @Override
        public void bindView(ViewHolder holder, Program obj) {

            holder.setText(R.id.text,obj.getName());
        }
    };

    ReuseAdapter<Date> dateAdapter = new ReuseAdapter<Date>(R.layout.item_epg_spinner) {
        @Override
        public void bindView(ViewHolder holder, Date obj) {

            holder.setText(R.id.text,obj.getDate());
        }
    };


    ReuseAdapter<ExpandableTime> epgAdapter = new ReuseAdapter<ExpandableTime>(R.layout.item_epg_info) {
        //event type（0：delete， 1：View， 2：View Series， 3： Record，4： Record Series）
        //缩略显示的设置图标
        public int[] showIcon = new int[]{
                R.drawable.btn_more, //无 暂时用白色隐藏
                R.drawable.epg_watch_once,
                R.drawable.epg_watch_cycle,
                R.drawable.epg_record_once,
                R.drawable.epg_record_cycle};

        //设置按钮ID
        int[] setBtn = new int[]{
                -1,
                R.id.watchBtnOnce,
                R.id.watchBtnCycle,
                R.id.recordBtnOnce,
                R.id.recordBtnCycle,
        };

        int curTextWidth = -1 ;

        @Override
        public void bindView(ViewHolder holder, ExpandableTime obj) {
            int position = holder.getItemPosition();
            Time time = obj.getTime();

            if(obj.isExpand()){//张开
                holder.getView(R.id.detialInfo).setVisibility(View.VISIBLE);
                holder.getView(R.id.epgShowIcon).setVisibility(View.GONE);
                // TextView shortDes = (TextView)holder.getView(R.id.shortDes);
                // shortDes.setSingleLine(true);

                setTextFull((TextView) holder.getView(R.id.shortDes));
            }else {
                holder.getView(R.id.detialInfo).setVisibility(View.GONE);
                holder.getView(R.id.epgShowIcon).setVisibility(View.VISIBLE);

                setTextMarquee((TextView) holder.getView(R.id.shortDes));
            }

            holder.setText(R.id.event,time.getEvent());
            holder.setText(R.id.time,time.getStartTime()+"->"+time.getEndTime());
            holder.setText(R.id.shortDes,time.getShortDes()+getResources().getString(R.string.epg_test));
            holder.setImageResource(R.id.epgShowIcon,showIcon[Integer.parseInt(time.getEventType())]);



            holder.setTag(R.id.recordBtnOnce,position);
            holder.setTag(R.id.recordBtnCycle,position);
            holder.setTag(R.id.watchBtnOnce,position);
            holder.setTag(R.id.watchBtnCycle,position);

            int eventType = Integer.parseInt(time.getEventType());
            int setBtnId = setBtn[eventType];
            selectSingleBtn(holder,setBtnId);

            holder.setOnClickListener(R.id.watchBtnOnce, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setEvent((Integer) v.getTag(),"1");

                }
            });
            holder.setOnClickListener(R.id.watchBtnCycle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setEvent((Integer) v.getTag(),"2");

                }
            });

            holder.setOnClickListener(R.id.recordBtnOnce, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setEvent((Integer) v.getTag(),"3");

                }
            });

            holder.setOnClickListener(R.id.recordBtnCycle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setEvent((Integer) v.getTag(),"4");

                }
            });

        }


        void setEvent(int position,String  setEventType){
            Log.i("tag","tag :"+position);
            Time time = epgAdapter.getItem(position).getTime();
            String curEvent = time.getEventType();
            Log.i("tag","curEvent :"+curEvent+"setEventType:"+setEventType);

            if(curEvent.equals(setEventType))
            {
                setEventType = "0";//清空事件
            }
            time.setEventType(setEventType);

            //发送修改事件


            notifyDataSetChanged();
        }

        /**
         * 单选 设置按钮
         * @param holder
         * @param id
         */
        void selectSingleBtn(ViewHolder holder,int  id){
            if(-1 == id){   // 如果为-1，全部置为不选择
                for (int i :
                        setBtn) {
                    if(i != -1)
                    holder.setSelect(i, false);
                }
                return;
            }

            for (int i :
                    setBtn) {
                if(i != -1){
                    if(id == i) {
                        holder.setSelect(i, true);
                        continue;
                    }

                    holder.setSelect(i, false);
                }

            }
        }


        public  void setTextMarquee(TextView textView) {
            if (textView != null) {
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);

                ViewGroup.LayoutParams lp = textView.getLayoutParams();

                if(-1 == curTextWidth){
                    curTextWidth = lp.width;
                }

                lp.width = curTextWidth;
                textView.setLayoutParams(lp);

            }
        }

        public  void setTextFull(TextView textView) {
            if (textView != null) {

                textView.setSingleLine(false);
                ViewGroup.LayoutParams lp = textView.getLayoutParams();

                if(-1 == curTextWidth){
                    curTextWidth = lp.width;
                }

                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                textView.setLayoutParams(lp);
            }
        }

    };



    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg){
        if(msg.getEventMode() == EventMode.OUT)
            return;

        switch (msg.getCommand()){
            case COM_LIVE_SET_PROGRAM_LIST:
                programList = DataParse.getProgramList(msg.getData());
                programAdapter.reset(programList);

                break;
            case COM_EPG_SET_SELECT_PROGRAM:
                epgProgram = DataParse.getEpgProgram(msg.getData());
                dateList = epgProgram.getDateArray();
                dateAdapter.reset(dateList);

                break;
            case COM_EPG_CLASH_RESERVE:
                break;
            case COM_SYSTEM_RESPOND:    //回应
                Respond respond = DataParse.getRespond(msg.getData());
                switch (respond.getCommand()){
                    case COM_CONNECT_DETACH:
                        if(respond.getFlag()){
                           // detach();
                        }
                        break;
                    case COM_CONNECT_ATTACH:
                        if(respond.getFlag()){
                           // attach();
                        }
                        break;
                    case COM_EPG_SET_RESERVE:
                        if(respond.getFlag()){
                           // aotuSet = true;
                           // getSelectEpgInfo(curProgram.getIndex());
                        }
                    default:
                        break;
                }
                break;
            default:
                break;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg);
        EventManager.register(this);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.epg_title);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView epgListView = (ListView)findViewById(R.id.epgListView);
        epgListView.setAdapter(epgAdapter);
        epgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemClick:"+position);
                ExpandableTime expandableTime = epgAdapter.getItem(position);
                boolean flag = expandableTime.isExpand()?false:true;
                Log.i(TAG,"onItemClick:"+flag);

                expandableTime.setExpand(flag);
                epgAdapter.notifyDataSetChanged();
            }
        });

        programSpinner = (Spinner)findViewById(R.id.programSpinner);
        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Program selectProgram = programAdapter.getItem(position);
                Log.i(TAG,"selectProgram:"+selectProgram.getName());
                getSelectEpgInfo(selectProgram.getIndex());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        programSpinner.setAdapter(programAdapter);

        dateSpinner = (Spinner)findViewById(R.id.dateSpinner);
        dateSpinner.setAdapter(dateAdapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Date date = dateAdapter.getItem(position);

                ArrayList<Time> timeList = date.getTimeArray();
                Log.i("epg","t"+timeList.toString());
                ArrayList<ExpandableTime> expandableTimes =ExpandableTime.toExpandableTime(timeList);
                epgAdapter.reset(expandableTimes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    private void initData() {
        getProgramList();
    }

    /**
     * 获取节目列表
     */
    private void getProgramList(){
        Log.i(TAG,"获取节目列表:");
        //errorMaskView.setLoadingStatus();
        EventManager.send(COM_LIVE_GET_PROGRAM_LIST,"", EventMode.OUT);
    }

    /**
     * 获取选中的节目epg信息
     */
    private void getSelectEpgInfo(int index){
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_EPG_GET_SELECT_PROGRAM, JSON.toJSONString(indexClass), EventMode.OUT);
    }


    private String[] parseDateData(String data){
        epgProgram = DataParse.getEpgProgram(data);
        //dateList.clear();
        //dateList.addAll(epgProgram.getDateArray());
        dateList = epgProgram.getDateArray();

        String[] dates = new String[dateList.size()];
        int i = 0;
        for (Date d :
                dateList) {
            dates[i++] = d.getDate();
        }

        return dates;
    }
}
