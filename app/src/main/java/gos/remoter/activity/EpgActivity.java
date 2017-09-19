package gos.remoter.activity;
import com.alibaba.fastjson.JSON;
import gos.remoter.R;
import gos.remoter.adapter.Epg_myAdapter;
import gos.remoter.data.Date;
import gos.remoter.data.ExpandableTime;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.ReserveEventSend;
import gos.remoter.data.Respond;
import gos.remoter.data.Time;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;
import static gos.remoter.define.CommandType.*;   //导入静态命令集

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;

public class EpgActivity extends Activity {
    private Epg_myAdapter progItemAdapter = null;
    private Epg_myAdapter tvNameAdapter = null;
    private Epg_myAdapter tvDateAdapter = null;

    private ArrayList<Program> progInfo;//频道信息
    private ArrayList<Date> progDate;//节目日期信息
    private ArrayList<Time> progTime;//一个日期的节目Epg信息，包含在ExpandableTime中
    private ArrayList<ExpandableTime> expandableTimes;//包含progTime，还包含是否被折叠

    private SparseIntArray sBtnDraw;//简单按钮的drawable资源
    private SparseIntArray btnSelected;//详细设置中全部按钮的id

    private static boolean isUndo = false;//按钮状态回滚
    private static int datePos = 0;//记录当前日期pos
    private static int tvIndex = 0;//记录当前频道index
    private Handler handler;//回滚按钮状态的handler

/*******************自定义点击事件***************************/
    //Item被点击事件
    private class ItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            Log.e("消息", "目前状态为" + expandableTimes.get(pos).isExpand());
            expandableTimes.get(pos).setExpand(!expandableTimes.get(pos).isExpand());
            progItemAdapter.notifyDataSetChanged();//提交改变
            Log.e("消息", "提交更改后，第" + datePos + "天的第" + pos + "个节目的展开状态现在是：" + expandableTimes.get(pos).isExpand());
        }
    }
    //频道列表被选择事件
    private class TvNameSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            progItemAdapter.clear();//清除上一次的节目信息列表
            tvDateAdapter.clear();//清除上一次节目日期列表
            if (expandableTimes != null) {
                expandableTimes.clear();//清除全部的日期的数据
            }

            Log.e("列表被选择事件的消息", "选择了第" + pos + "个节目，向服务器请求获取第" + pos + "个节目的EPG信息");
            getSelectEpgInfo(progInfo.get(pos).getIndex());//获取第pos个节目的EPG信息
            tvIndex = progInfo.get(pos).getIndex();//保存频道pos

            TextView textName = (TextView) view.findViewById(R.id.epg_TVName);
            textName.setTextColor(Color.parseColor("#2891e2"));//被选中的item设置蓝色字体
        }
        @Override
        public void onNothingSelected (AdapterView<?> adapterView) {Log.e("消息", "适配器中已经没有数据，进入onNothing()方法");}
    }
    //日期列表被选择事件
    private class TvDateSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            progItemAdapter.clear();//清除上一次的节目信息列表
            expandableTimes.clear();//清除上一个日期的列表数据
            datePos = pos;//记录日期位置
            Log.e("日期列表被选择事件的消息", "选择了第" + progInfo.get(pos).getIndex() + "个节目，第" + pos + "个日期");

            TextView textName = (TextView)findViewById(R.id.epg_TVName);
            textName.setTextColor(Color.parseColor("#808080"));//被选中的item设置灰色字体
            TextView textDate = (TextView)view.findViewById(R.id.epg_TVDate);
            textDate.setTextColor(Color.parseColor("#2891e2"));//被选中的item设置蓝色字体

            //获取这个日期的数据
            progTime = progDate.get(pos).getTimeArray();//拿到被选择的日期中的节目列表
            Log.e("消息", "现在expandableTimes中被填充的时第" + pos + "天的数据");
            expandableTimes = ExpandableTime.toExpandableTime(progTime);
            progItemAdapter.addAll(expandableTimes);
        }
        @Override
        public void onNothingSelected (AdapterView<?> adapterView) {Log.e("消息", "适配器中已经没有数据，进入onNothing()方法");}
    }
    //节目中的按钮被点击事件
    private class ItemBtnClick implements View.OnClickListener {
        private int eventType = 0;
        private int temp;
        private Epg_myAdapter.Holder holder;
        ReserveEventSend reserveEventSend;
        public ItemBtnClick(Epg_myAdapter.Holder holder) {
            this.holder = holder;
            reserveEventSend = new ReserveEventSend();
        }
        @Override
        public void onClick(View view) {
            eventType = Integer.parseInt(expandableTimes.get(holder.getItemPosition()).getTime().getEventType());
            temp = eventType;
            if (eventType == 0) {
                Log.e("消息", "这个item无设置状态，此时eventType是" + eventType);//本身为无状态，点亮点击到的按钮，并将简单按钮亮起
                holder.setBtnSelected(view.getId(), true);//点亮被选择的按钮
                holder.setBackgroundResource(R.id.epg_simple_sBtn, sBtnDraw.get(sBtnDraw.indexOfValue(view.getId())));//将简单按钮亮起
                getType(view);//更新ventType
            } else {
                if ((view.getId() == btnSelected.get(eventType)) || (view.getId() == R.id.epg_simple_sBtn)) {
                    Log.e("消息", "点中已经被按下的按钮，此时eventType是" + eventType);//点中的按钮为已选择的按钮或者简单按钮，清零设置状态
                    holder.setBtnSelected(view.getId(), false);//取消按下
                    holder.setBackgroundResource(R.id.epg_simple_sBtn, sBtnDraw.get(0));//隐藏简单按钮
                    eventType = 0;
                    expandableTimes.get(holder.getItemPosition()).getTime().setEventType(Integer.toString(eventType));//改变按钮状态
                    progItemAdapter.notifyDataSetChanged();
                } else {
                    Log.e("消息", "点中灰色按钮，此时eventType是" + eventType);//点中没有按下的按钮，则熄灭已经按下的按钮，点亮被点中的按钮，改变简单按钮
                    holder.setBtnSelected(btnSelected.get(eventType), false);//熄灭被点亮的按钮
                    holder.setBtnSelected(view.getId(), true);//点亮被点中的按钮
                    holder.setBackgroundResource(R.id.epg_simple_sBtn, sBtnDraw.get(eventType));//改变简单按钮
                    getType(view);//更新ventType
                }
            }
            //将改变后的eventType发送给服务器
            reserveEventSend.setEventId(expandableTimes.get(holder.getItemPosition()).getTime().getEventID());//按钮id
            reserveEventSend.setEventType(Integer.toString(eventType));//按钮状态
            reserveEventSend.setIndex(tvIndex);//频道index
            sendReserveSet(reserveEventSend);//向服务器发送按钮状态
            new Thread() {
                boolean toFoldRun;
                public void run() {
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException sleepError) {Log.e("线程中", "睡眠异常");}
                    if (isUndo) {
                        Log.e("线程中消息", "不知怎么的，服务器不允许更改，回滚设置状态");
                        expandableTimes.get(holder.getItemPosition()).getTime().setEventType(Integer.toString(temp));//改变按钮状态
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progItemAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }.start();
        }
        private void getType(View view) {
            for (int i = 1; i <= btnSelected.size(); i++) {
                if (btnSelected.get(i) == view.getId()) {
                    eventType = i;
                    expandableTimes.get(holder.getItemPosition()).getTime().setEventType(Integer.toString(eventType));//改变按钮状态
                    progItemAdapter.notifyDataSetChanged();
                    break;
                }
            }
            Log.e("消息", "eventType为" + eventType);
        }
    }


/*******************事件接收和接收数据处理*******************/
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg) {
        if(msg.getEventMode() == EventMode.OUT) {
            return;//接收消息为空，不做处理
        }
        switch (msg.getCommand()){
            case COM_LIVE_SET_PROGRAM_LIST: {//收到频道列表
                makeProgramList(msg.getData());
                break;
            }
            case COM_EPG_SET_SELECT_PROGRAM: {//收到节目epg信息
                makeProgramData(msg.getData());
                break;
            }
            case COM_SYSTEM_RESPOND: {//系统回应：epg信息设置状态，eventType
                Respond respond = DataParse.getRespond(msg.getData());
                if (respond.getCommand() == COM_EPG_SET_RESERVE) {
                    if (!respond.getFlag()) {
                        isUndo = true;
                        Log.e("接收服务器回应部分的消息", "不知怎么的，服务器拒绝了按钮状态的更改请求");
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }
    //频道列表处理
    private void makeProgramList(String data){
        progInfo = DataParse.getProgramList(data);//得到节目总信息
        Log.e("处理频道列表数据部分的消息", "节目数目是：" + progInfo.size());
        tvNameAdapter.addAll(progInfo);//将所有频道名添加到频道列表
        getSelectEpgInfo(progInfo.get(0).getIndex());//获取第0个节目信息首先被显示在列表中
    }
    //节目信息处理
    private void makeProgramData(String data) {
        progDate = DataParse.getEpgProgram(data).getDateArray();//得到日期所有信息
        tvDateAdapter.addAll(progDate);//将所有日期添加到日期下拉列表

        progTime = progDate.get(0).getTimeArray();//拿到第0个日期中的节目列表
        expandableTimes = ExpandableTime.toExpandableTime(progTime);
        progItemAdapter.addAll(expandableTimes);//添加第0个日期的节目信息到列表
    }


/*****************向服务器请求数据***********************/
    private void getProgramList() {
        EventManager.send(COM_LIVE_GET_PROGRAM_LIST,"", EventMode.OUT);//获取频道列表
    }
    private void getSelectEpgInfo(int index){
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_EPG_GET_SELECT_PROGRAM, JSON.toJSONString(indexClass), EventMode.OUT);//获取选中的节目epg信息
    }
    private void sendReserveSet(ReserveEventSend reserveSet){
        EventManager.send(COM_EPG_SET_RESERVE,JSON.toJSONString(reserveSet), EventMode.OUT);//发送预定事件设置
    }


/*******************必需重写的方法和初始化***********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        System.gc();
        EventManager.register(this);//注册EventManager
        ACTCollector.add(this);//将ACT添加到列表
        handler = new Handler();
        setTitleBar();//设置标题栏
        init_adapter();//设置适配器
    }
    @Override
    public void onDestroy() {
        ACTCollector.remove(ACTCollector.getByName(EpgActivity.this));//通过名字获得ACT所在列表位置并移除
        super.onDestroy();
        EventManager.unregister(this);//取消注册event接收器
        setContentView(R.layout.activity_base);
        progInfo.clear();
        progDate.clear();
        progTime.clear();
        expandableTimes.clear();
        isUndo = false;
        datePos = 0;
        tvIndex = 0;
        handler = null;
        sBtnDraw = null;
        btnSelected = null;
        progItemAdapter = null;
        tvNameAdapter = null;
        tvDateAdapter = null;
        System.gc();//有效，一般重启两次epgACT之后会回收20MB内存，总占用内存大概在91M左右
        Log.e("消息", "将EPGACT从ACT列表中移除，取消注册event，EPGACT已经死亡");//告知取消注册event//告知EPGACT被杀死
    }
    //设置TitleBar
    public void setTitleBar() {
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);//标题栏
        titleBar.setTextTitle(R.string.epg_title);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //初始化适配器
    public void init_adapter() {
        new ImmersionLayout(this).setImmersion();
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.epg_title);
        Context context = EpgActivity.this;
        final ListView progListView = (ListView) findViewById(R.id.epg_mainProgList);
        final Spinner tvNameSpinner = (Spinner) findViewById(R.id.epg_mainTVName);
        final Spinner tvDateSpinner = (Spinner) findViewById(R.id.epg_mainTVDate);

        sBtnDraw = new SparseIntArray();//简单按钮的资源id
        sBtnDraw.put(0, R.drawable.epg_simple_btn_null);
        sBtnDraw.put(1, R.drawable.epg_simple_watchbtn_once);
        sBtnDraw.put(2, R.drawable.epg_simple_watchbtn_cycle);
        sBtnDraw.put(3, R.drawable.epg_simple_recbtn_once);
        sBtnDraw.put(4, R.drawable.epg_simple_recbtn_cycle);

        btnSelected = new SparseIntArray();
        btnSelected.put(1, R.id.epg_full_recBtnOnce);
        btnSelected.put(2, R.id.epg_full_recBtnCycle);
        btnSelected.put(3, R.id.epg_full_watchBtnOnce);
        btnSelected.put(4, R.id.epg_full_watchBtnCycle);

        //初始化适配器：ExpandableTime中包含：一个item的信息和设置是否被展开标志
        progItemAdapter = new Epg_myAdapter<ExpandableTime>(context, R.layout.epg_progitem) {
            @Override
            public void bindView(Holder holder, ExpandableTime obj) {
                Time time = obj.getTime();

                itemFold(holder, obj);//判断折叠/展开
                holder.setText(R.id.epg_progName, time.getEvent());
                holder.setText(R.id.epg_progTime, time.getStartTime() + "~" + time.getEndTime());
                holder.setText(R.id.epg_shortDesc, time.getShortDes());
                holder.setBackgroundResource(R.id.epg_simple_sBtn, sBtnDraw.get(Integer.parseInt(time.getEventType())));//设置简单按钮的资源图片
                setBtnSelect(holder, Integer.parseInt(time.getEventType()));//设置4个按钮的状态

                holder.setItemOnClickListener(progListView, new ItemClick());//设置item被点击事件的监听
                holder.setOnClickListener(R.id.epg_simple_sBtn, new ItemBtnClick(holder));//设置5个按钮被点击事件的监听
                for (int i = 1; i <= btnSelected.size(); i++) {
                    holder.setOnClickListener(btnSelected.get(i), new ItemBtnClick(holder));
                }
            }
        };
        tvNameAdapter = new Epg_myAdapter<Program>(context, R.layout.epg_spinner_tvname) {
            @Override
            public void bindView(Holder holder, Program obj) {
                holder.setText(R.id.epg_TVName, obj.getName());

                holder.setItemSelectListener(tvNameSpinner, new TvNameSelected());//设置列表被选择监听
            }
        };
        tvDateAdapter = new Epg_myAdapter<Date>(context, R.layout.epg_spinner_tvdate) {
            @Override
            public void bindView(Holder holder, Date obj) {
                holder.setText(R.id.epg_TVDate, obj.getDate());

                holder.setItemSelectListener(tvDateSpinner, new TvDateSelected());//监听日期列表被选择事件
            }
        };
        progListView.setAdapter(progItemAdapter);//设置节目列表
        tvNameSpinner.setAdapter(tvNameAdapter);//设置频道列表
        tvDateSpinner.setAdapter(tvDateAdapter);//设置日期列表
        getProgramList();//获取频道列表，装载到列表中
    }
        private void setBtnSelect(Epg_myAdapter.Holder holder, int eventType) {
        for (int i = 1; i <= 4; i++) {
            if (eventType == i) {
                holder.setBtnSelected(btnSelected.get(i), true);
            } else {
                holder.setBtnSelected(btnSelected.get(i), false);
            }
        }
    }
        private void itemFold(Epg_myAdapter.Holder holder, ExpandableTime obj) {
        //判断是否被展开
        Log.e("消息", "此时bindView判断是否展开设置，展开状态为" + obj.isExpand());
        if (obj.isExpand()) {//被展开，则显示详细设置，隐藏sBtn，将文字全部展示
            holder.setVISIBLE(R.id.epg_full_ProgSetting);
            holder.setGONE(R.id.epg_simple_sBtn);
            holder.setTextNormal(R.id.epg_shortDesc);
            holder.setTextHeight(R.id.epg_shortDesc, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {//被折叠，则隐藏详细设置，显示sBtn，将文字缩略
            holder.getView(R.id.epg_full_ProgSetting).setVisibility(View.GONE);
            holder.getView(R.id.epg_simple_sBtn).setVisibility(View.VISIBLE);
            holder.setTextMarquee(R.id.epg_shortDesc);
        }
    }
}