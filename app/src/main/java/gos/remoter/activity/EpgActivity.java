package gos.remoter.activity;

import gos.remoter.adapter.Epg_progItem;
import gos.remoter.adapter.Epg_TVDate;
import gos.remoter.adapter.Epg_TVName;
import gos.remoter.data.Date;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.ReserveEventSend;
import gos.remoter.data.Time;
import gos.remoter.define.CS;//静态常量
import gos.remoter.R;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.adapter.Epg_myAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
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

import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;   //导入静态命令集

import java.util.ArrayList;

public class EpgActivity extends Activity {
    private AlertDialog logoutAlert;

    private Epg_myAdapter epg_progItemAdapter = null;//节目内容适配器
    private Epg_myAdapter epg_tvNameAdapter = null;//频道号和日期适配器
    private Epg_myAdapter epg_tvDateAdapter = null;//频道号和日期适配器
    private int[] index;//节目索引
    private ListView progListView;//节目列表view
    private Spinner tvNameSpinner;//节目号下拉列表view
    private Spinner tvDateSpinner;//节目号下拉列表view

    private ArrayList<Epg_progItem> itemData;//节目中的数据
    private ArrayList<Epg_TVName> tvNameData;//频道名字
    private ArrayList<Epg_TVDate> tvDateData;//频道日期

    private ArrayList<Program> programInfo;//得到的节目总信息
    private ArrayList<Date> programDate;//得到节目日期信息
    private ArrayList<Time> programData;//得到节目Epg信息

    private ArrayList<ArrayList<Epg_progItem>> date_itemdata;//每一个日期得到的各自的全部节目item
    private ArrayList<int[]> sBtnType;//每一个日期的每一个item的简单按钮的初始状态记录

    private static int tvDateCache = 0;//记录日期

    private int[] simpleBtn;
    private int[] fullSettingBtn;
    private int[] fullBtnId;

    private int[] tempBtn;


/*******************事件接收器*******************/
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
                makeProgramList(msg.getData()); //整理数据，显示到节目号列表
                break;
            }
            //设置选择的节目epg信息
            case COM_EPG_SET_SELECT_PROGRAM: {
                makeProgramData(msg.getData());//整理数据，显示到节目日期列表
                break;
            }
            //系统回应：节目的Epg设置数据
            case COM_SYSTEM_RESPOND: {
                makeProgramEpgSet(msg.getData());
                break;
            }
            default: {
                break;
            }
        }
    }


/****************节目数据处理部分****************/
    /**节目号处理
     *包括：
     * 1、获取所有节目号
     * 2、将所有节目号装载到节目号下拉列表中
     */
    private void makeProgramList(String data){
        programInfo = DataParse.getProgramList(data);//得到节目总信息
        String[] programList = new String[programInfo.size()];//得到一个合适的长度数组
        Log.e(CS.EPGACT_TAG, "节目数目是：" + programInfo.size());
        index = new int[programInfo.size()];//得到一个合适的长度数组
        //将节目号提取出来
        for (int i = 0; i < programInfo.size(); i++) {
            index[i] =  programInfo.get(i).getIndex();//得到节目索引
            programList[i] = programInfo.get(i).getName();//得到节目名字
        }

        for (String program : programList) {
            epg_tvNameAdapter.add(new Epg_TVName(program));
        }

        Log.e(CS.EPGACT_TAG, "向服务器获取第一个节目中的EPG信息，index是：" + index[0]);
        getSelectEpgInfo(index[0]);//获取第1个节目信息首先被显示在列表中
        //装载节目列表完成
    }

    /**节目信息处理
     *
     * 1、获取节目Epg信息
     * 2、将节目信息装载到节目列表中
     */
    private void makeProgramData(String data) {
        String progName = null;//频道中节目的名字
        String progWatchTime = null;//播放时间
        String progShortDes = null;//短描述
        int[] eventType = null;//一个节目中的所有item的按钮状态集合
        //简单按钮对应的资源id
        int[] simpleBtn = new int[]{R.drawable.epg_simple_btn_null, R.drawable.epg_simple_watchbtn_once, R.drawable.epg_simple_watchbtn_cycle,
                R.drawable.epg_simple_recbtn_once, R.drawable.epg_simple_recbtn_cycle,};//按钮类型
        //详细按钮对应的资源id
        int[] fullSettingBtnFalse = new int[]{R.drawable.epg_simple_btn_null, R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false,
                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false};
        //详细按钮被按下的资源id
        int [] fullSettingBtnTrue = new int[]{R.drawable.epg_simple_btn_null, R.drawable.epg_full_lbtn_true, R.drawable.epg_full_rbtn_true,
                R.drawable.epg_full_lbtn_true, R.drawable.epg_full_rbtn_true};
        //备份资源id
        final int[] temp = new int[]{R.drawable.epg_simple_btn_null, R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false,
                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false};

        epg_progItemAdapter.clear();//清除上一次的节目信息列表
        epg_tvDateAdapter.clear();//清除日期信息

        //得到日期信息
        programDate = DataParse.getEpgProgram(data).getDateArray();
        String[] dateList = new String[programDate.size()];//得到合适的长度
        //Log.e(CS.EPGACT_TAG, "日期数目：" + programDate.size() + "个");

        date_itemdata = new ArrayList<>();//一个频道中所有日期的所有信息的集合
        sBtnType = new ArrayList<int[]>();
        //这里得到多个日期，保存每个日期中的节目信息
        //Log.e(CS.EPGACT_TAG, "此节目一共有" + programDate.size() + "个日期的信息");
        for (int i = 0; i < programDate.size(); i++) {
            ArrayList<Epg_progItem> date_item = new ArrayList<Epg_progItem>();
            //得到一个日期字符串
            dateList[i] = programDate.get(i).getDate();

            //将日期添加到日期列表中
            epg_tvDateAdapter.add(new Epg_TVDate(dateList[i]));
            Log.e(CS.EPGACT_TAG, "添加了日期" + dateList[i]);

            //这里得到了一个日期的所有节目item
            programData = programDate.get(i).getTimeArray();
            eventType = new int[programData.size()];
            for (int j = 0; j < programData.size(); j++) {

                //获取到节目名、节目播放时间、节目短描述、按钮状态
                progName = programData.get(j).getEvent();
                progWatchTime = programData.get(j).getStartTime() + "~" + programData.get(j).getEndTime();
                progShortDes = programData.get(j).getShortDes();

                //获取到按钮的状态
                eventType[j] = Integer.parseInt(programData.get(j).getEventType());
                Log.e(CS.EPGACT_TAG, "得到的按钮状态是：" + eventType[j]);//0, 1, 2, 3, 4
                fullSettingBtnFalse[eventType[j]] = fullSettingBtnTrue[eventType[j]];//得到被按下状态的资源id

                //保存了一个节目item
                date_item.add(new Epg_progItem(progName, progWatchTime, progShortDes, simpleBtn[eventType[j]], fullSettingBtnFalse[1], fullSettingBtnFalse[2], fullSettingBtnFalse[3], fullSettingBtnFalse[4]));
                //Log.e(CS.EPGACT_TAG, "保存了日期" + dateList[i] + "的第" + j + "个节目item");
                fullSettingBtnFalse[eventType[j]] = temp[eventType[j]];//还原数组值
            }

            //保存当前日期的全部节目的按钮状态
            sBtnType.add(eventType);
            //保存一个日期的全部节目信息
            date_itemdata.add(date_item);
        }

        //添加第一个日期的节目信息到列表
        tvDateCache = date_itemdata.size() < tvDateCache? 0 : tvDateCache;//如果没有这个日期则回到第一天的节目
        for (int i = 0; i < date_itemdata.get(0).size(); i++) {
            epg_progItemAdapter.add((date_itemdata.get(tvDateCache)).get(i));
        }
    }

    /**节目的Epg设置数据
     *？？？
     */
    private void makeProgramEpgSet(String data) {
    }


/*******************自定义点击事件***************************/
    //频道下拉列表被选择事件
    private class NameItemSelect implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            //列表被选择事件
            Log.e(CS.EPGACT_TAG, "选择了第" + pos + "个节目，向服务器请求获取第" + pos + "个节目的EPG信息");
            getSelectEpgInfo(index[pos]);//获取第pos个节目的EPG信息
            //设置被选中的列表文字颜色
            TextView textName = (TextView)findViewById(R.id.epg_TVName);
            textName.setTextColor(Color.parseColor("#2891e2"));//被选中的item设置蓝色字体
        }
        @Override
        public void onNothingSelected (AdapterView<?> adapterView) {}
    }
    //日期下拉列表被选择事件
    private class DateItemSelect implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            Log.e(CS.EPGACT_TAG, "选择了第" + pos + "个节目日期");
            tvDateCache = pos;

            //重置频道颜色
            TextView textName = (TextView)findViewById(R.id.epg_TVName);
            textName.setTextColor(Color.parseColor("#808080"));
            //设置被选中的列表文字颜色
            TextView textDate = (TextView) view.findViewById(R.id.epg_TVDate);
            textDate.setTextColor(Color.parseColor("#2891e2"));//被选中的item设置蓝色字体

            //刷新到这个日期的节目信息
            epg_progItemAdapter.clear();//清除当前的列表中信息
            for (int i = 0; i < date_itemdata.get(pos).size(); i++) {
                //这里需要更新按钮的状态
                int[] tempSimpleBtn = sBtnType.get(tvDateCache);
                int temp = fullSettingBtn[tempSimpleBtn[i]];
                fullSettingBtn[tempSimpleBtn[i]] = tempBtn[tempSimpleBtn[i]];
                Epg_progItem epg_progItem = new Epg_progItem(date_itemdata.get(tvDateCache).get(i).getProgName(),
                        date_itemdata.get(tvDateCache).get(i).getPorgTime(), date_itemdata.get(tvDateCache).get(i).getProgInfo(),
                        simpleBtn[tempSimpleBtn[i]], fullSettingBtn[1], fullSettingBtn[2], fullSettingBtn[3], fullSettingBtn[4]);
                date_itemdata.get(tvDateCache).set(i, epg_progItem);
                fullSettingBtn[tempSimpleBtn[i]] = temp;//还原
                epg_progItemAdapter.add(date_itemdata.get(tvDateCache).get(i));
            }
        }
        @Override
        public void onNothingSelected (AdapterView<?> adapterView) {}
    }
    //Item的点击事件
    private class ItemClick implements AdapterView.OnItemClickListener {
        private boolean[] isSpread;
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            if (isSpread == null) {
                int len = adapterView.getCount();
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
        private Epg_myAdapter.Holder holder;
        int itemEventType;//简单按钮状态
        int itemPos;//本日期内的列表行数
        int[] DateEventType;//一个日期内的所有节目的按钮状态
        private boolean[] isBtnSelected = new boolean[] {false, false, false, false, false};//删除 + 4个按钮的状态开关
        private boolean[] temp = new boolean[] {false, false, false, false, false};//4个按钮的状态开关+删除

        //构造方法初始化
        private ButtonClick(Epg_myAdapter.Holder holder) {
            this.holder = holder;
            itemPos = holder.getItemPosition();
            DateEventType = sBtnType.get(tvDateCache);//当前日期的按钮状态
            itemEventType = DateEventType[itemPos];//当前item的按钮状态
            Log.e(CS.EPGACT_TAG, "item的行数是：" + itemPos + "日期是" + tvDateCache + "按钮状态是：" + itemEventType);
            isBtnSelected[itemEventType] = true;//第0个是没有任何状态
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.epg_simple_btn: {
                    Log.e(CS.ADAPTER_TAG, "点中了简单按钮");
                    clickSimpleBtn();
                    break;
                }
                case R.id.epg_full_recBtnOnce: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定录制一次按钮");
                    clickRecordOnce();
                    break;
                }
                case R.id.epg_full_recBtnCycle: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定循环录制按钮");
                    clickRecordCycle();
                    break;
                }
                case R.id.epg_full_watchBtnOnce: {
                    Log.e(CS.ADAPTER_TAG, "点中了预定观看一天按钮");
                    clickWatchOnce();
                    break;
                }
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

        //点击简略按钮
        public void clickSimpleBtn() {
            //这里要知道上面的type，只有不为0时才有点击效果
            Log.e(CS.EPGACT_TAG, "简单按钮的状态是：" + itemEventType);

            int tempBtnType = 0;//记录此状态，请求更改失败时回滚
            if (itemEventType != 0) {
                Log.e(CS.EPGACT_TAG, "处于状态模式");
                tempBtnType = itemEventType;//记录此状态
                //改变状态
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_btn_null);//使简单按钮透明化
                //取消按钮状态
                holder.setBackgroundResource(fullBtnId[itemEventType - 1], fullSettingBtn[itemEventType]);
                isBtnSelected[itemEventType] = false;
                itemEventType = 0;//表明已经被取消状态

                DateEventType[itemPos] = 0;//将这个状态更新到列表信息中
                sBtnType.set(tvDateCache, DateEventType);//这里需要改掉列表中的按钮资源图片
                Log.e(CS.EPGACT_TAG, "将日期" + tvDateCache + "的行数：" + DateEventType[itemPos] + "的按钮状态更改为0");
            }
//将按钮设置发送给服务器
//                ReserveEventSend reserveSet = new ReserveEventSend();
//                reserveSet.setEventId();
//                reserveSet.setEventType("0");//请求取消状态
//                reserveSet.setIndex();
//                sendReserveSet(reserveSet);
//            if (false) {
//                DateEventType[itemPos] = tempBtnType;
//            }
//如果回应成功，则回滚，如果回应失败了，则回滚
        }
        //点击录制一次按钮
        public void clickRecordOnce() {
            Log.e(CS.EPGACT_TAG, "被点击时isBtnSelected[1]的值为：" + isBtnSelected[1]);
            //更改按钮样式，要将按钮状态更改提交给保存的列表
            if (!isBtnSelected[1]) {
                Log.e(CS.EPGACT_TAG, "需要设置按钮状态");
                //这里应该设置为透明，防止GONE后文本描述占满父容器
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_recbtn_once);
                DateEventType[itemPos] = 1;//记录效果
                itemEventType = DateEventType[itemPos];//更新简单按钮状态
                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_true);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);

                System.arraycopy(temp, 0, isBtnSelected, 0, temp.length);
            } else {
                Log.e(CS.EPGACT_TAG, "需要取消按钮状态");
                DateEventType[itemPos] = 0;//清除状态
                itemEventType = 0;//更新简单按钮状态
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_btn_null);
                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
            }

            sBtnType.set(tvDateCache, DateEventType);//这里需要改掉列表中的按钮资源图片
            Log.e(CS.EPGACT_TAG, "将日期" + tvDateCache + "的行数：" + DateEventType[itemPos] + "的按钮状态更改为" + DateEventType[itemPos]);
            isBtnSelected[1] = !isBtnSelected[1];
            Log.e(CS.EPGACT_TAG, "点击后isBtnSelected[1]的值为：" + isBtnSelected[1]);
        }
        //点击循环录制按钮
        public void clickRecordCycle() {
            //更改按钮样式
            if (!isBtnSelected[2]) {
                //这里应该设置为透明，防止GONE后文本描述占满父容器
                DateEventType[itemPos] = 2;//记录效果
                itemEventType = DateEventType[itemPos];
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_recbtn_cycle);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_true);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);

                System.arraycopy(temp, 0, isBtnSelected, 0, temp.length);
            } else {
                DateEventType[itemPos] = 0;
                itemEventType = 0;
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_btn_null);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
            }

            sBtnType.set(tvDateCache, DateEventType);//这里需要改掉列表中的按钮资源图片
            Log.e(CS.EPGACT_TAG, "将日期" + tvDateCache + "的行数：" + DateEventType[itemPos] + "的按钮状态更改为" + DateEventType[itemPos]);
            isBtnSelected[2] = !isBtnSelected[2];
        }
        //点击观看一次按钮
        public void clickWatchOnce() {
            //更改按钮样式
            if (!isBtnSelected[3]) {
                DateEventType[itemPos] = 3;//记录效果
                itemEventType = DateEventType[itemPos];
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_watchbtn_once);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_true);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);

                System.arraycopy(temp, 0, isBtnSelected, 0, temp.length);
            } else {
                DateEventType[itemPos] = 0;
                itemEventType = 0;
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_btn_null);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
            }

            sBtnType.set(tvDateCache, DateEventType);//这里需要改掉列表中的按钮资源图片
            Log.e(CS.EPGACT_TAG, "将日期" + tvDateCache + "的行数：" + DateEventType[itemPos] + "的按钮状态更改为" + DateEventType[itemPos]);
            isBtnSelected[3] = !isBtnSelected[3];
        }
        //点击循环观看按钮
        public void clickWatchCycle() {
            //更改按钮样式
            if (!isBtnSelected[4]) {
                DateEventType[itemPos] = 4;//记录效果
                itemEventType = DateEventType[itemPos];
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_watchbtn_cycle);

                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, R.drawable.epg_full_rbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, R.drawable.epg_full_lbtn_false);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_true);

                System.arraycopy(temp, 0, isBtnSelected, 0, temp.length);
            } else {
                DateEventType[itemPos] = 0;
                itemEventType = 0;
                holder.setBackgroundResource(R.id.epg_simple_btn, R.drawable.epg_simple_btn_null);
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, R.drawable.epg_full_rbtn_false);
            }

            sBtnType.set(tvDateCache, DateEventType);//这里需要改掉列表中的按钮资源图片
            Log.e(CS.EPGACT_TAG, "将日期" + tvDateCache + "的行数：" + DateEventType[itemPos] + "的按钮状态更改为" + DateEventType[itemPos]);
            isBtnSelected[4] = !isBtnSelected[4];
        }
    }


/*****************初始化部分***********************/
    public void init_adapter() {
        Context context = EpgActivity.this;
        //初始化数据
        itemData = new ArrayList<Epg_progItem>();
        tvNameData = new ArrayList<Epg_TVName>();
        tvDateData = new ArrayList<Epg_TVDate>();
        //找到各自列表的Id
        progListView = (ListView) findViewById(R.id.epg_mainProgList);
        tvNameSpinner = (Spinner) findViewById(R.id.epg_mainTVName);
        tvDateSpinner = (Spinner) findViewById(R.id.epg_mainTVDate);
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
                holder.setBackgroundResource(R.id.epg_simple_btn,obj.getSimpleBtn());
                holder.setBackgroundResource(R.id.epg_full_recBtnOnce, obj.getRecBtnOnce());
                holder.setBackgroundResource(R.id.epg_full_recBtnCycle, obj.getRecBtnCycle());
                holder.setBackgroundResource(R.id.epg_full_watchBtnOnce, obj.getWatchBtnOnce());
                holder.setBackgroundResource(R.id.epg_full_watchBtnCycle, obj.getWatchBtnCycle());

                //item的监听
                holder.setItemOnClickListener(progListView, new ItemClick());

                //item中的按钮监听
                ButtonClick buttonClick = new ButtonClick(holder);
                holder.setOnClickListener(R.id.epg_simple_btn, buttonClick);
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

                //添加节目号列表选择事件
                holder.setItemSelectListener(tvNameSpinner, new NameItemSelect());
            }
        };
        epg_tvDateAdapter = new Epg_myAdapter<Epg_TVDate>(context, tvDateData, R.layout.epg_spinner_tvdate) {
            @Override
            public void bindView(Holder holder, Epg_TVDate obj) {
                holder.setText(R.id.epg_TVDate, obj.getTVDate());

                //添加节目号列表选择事件
                holder.setItemSelectListener(tvDateSpinner, new DateItemSelect());
            }
        };
        //列表使用适配器
        progListView.setAdapter(epg_progItemAdapter);
        tvNameSpinner.setAdapter(epg_tvNameAdapter);
        tvDateSpinner.setAdapter(epg_tvDateAdapter);
        Log.e(CS.EPGACT_TAG, CS.EPGACT_INITADA_SUCEESS);//初始化适配器成功

        //向服务器获取第一个节目信息，但节目列表不需要重复获取了
        Log.e(CS.EPGACT_TAG, "向服务器获取节目数据");
        getProgramList();//获取第一个节目的所有信息，装载到列表中
    }


/*******************必需重写的方法***********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_main);
        new ImmersionLayout(this).setImmersion();
        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.connect_title);
        titleBar.setImageRight(R.drawable.home_logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                logoutAlert();
            }
        });
        EventManager.register(this);//注册EventManager

        init_adapter();//完成初始化EpgAdapter

        simpleBtn = new int[] {R.drawable.epg_simple_btn_null, R.drawable.epg_simple_recbtn_once, R.drawable.epg_simple_recbtn_cycle,
                R.drawable.epg_simple_watchbtn_once, R.drawable.epg_simple_watchbtn_cycle};

        fullSettingBtn = new int[] {R.drawable.epg_simple_btn_null, R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false,
                R.drawable.epg_full_lbtn_false, R.drawable.epg_full_rbtn_false};

        tempBtn = new int[] {R.drawable.epg_simple_btn_null, R.drawable.epg_full_lbtn_true, R.drawable.epg_full_rbtn_true,
                R.drawable.epg_full_lbtn_true, R.drawable.epg_full_rbtn_true};

        fullBtnId = new int[] {R.id.epg_full_recBtnOnce, R.id.epg_full_recBtnCycle, R.id.epg_full_watchBtnOnce, R.id.epg_full_watchBtnCycle};
    }
    @Override
    public void onDestroy() {
        Log.e(CS.EPG_TAG, CS.EPG_ONDESTROY);//流程顺序索引
        super.onDestroy();
        EventManager.unregister(this);//取消注册event接收器
        Log.e(CS.EPG_TAG, CS.EPGACT_UNREGISTER_EVENTMANAGER);//告知取消注册event
        Log.e(CS.EPGACT_TAG, CS.EPGACT_DEATH);//告知EPGACT被杀死
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


/*******************关于退出和状态栏***********************/
    //退出
    void logout(){
        Intent intent = new Intent(this,ConnectActivity.class);
        startActivity(intent);
        finish();
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
                        logout();
                    }
                }).create();
    }
    void logoutAlert(){
        if(null == logoutAlert){
            initLogoutAlert();
        }

        logoutAlert.show();
    }
}
