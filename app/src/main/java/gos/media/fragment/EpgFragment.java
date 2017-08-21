package gos.media.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import gos.media.callback.ReserveSetCallback;
import gos.media.R;
import gos.media.adapter.EpgTimeAdapter;
import gos.media.data.Date;
import gos.media.data.EpgProgram;
import gos.media.data.IndexClass;
import gos.media.data.Program;
import gos.media.data.ReserveEventSend;
import gos.media.data.Respond;
import gos.media.data.Time;
import gos.media.define.DataParse;
import gos.media.define.SystemInfo;
import gos.media.enumkey.SystemState;
import gos.media.event.EventManager;
import gos.media.event.EventMode;
import gos.media.event.EventMsg;
import gos.media.view.ErrorMaskView;
import gos.media.view.TitleBar;
import static gos.media.define.CommandType.*;   //导入静态命令集


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EpgFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EpgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EpgFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private  final String TAG = this.getClass().getSimpleName();
    private View rootView = null;   //缓存页面
    private Context context;
    private ErrorMaskView errorMaskView = null;

    private ListView programListView = null;
    private ArrayList<Program> programList;

    private ListView dateListView = null;
    private EpgProgram epgProgram = null;
    private ArrayList<Date> dateList = new ArrayList<>();

    private ListView epgListView = null;

    TextView proText ;
    TextView dateText;

    ViewGroup mainFrame;
    ViewGroup proFrame;
    ViewGroup dateFrame;

    private Program  curProgram ;
    private Date curDate;
    AlertDialog dialog = null; //egp 弹出框

    EpgTimeAdapter epgTimeAdapter;

    int curProPosition = -1;//当前节目选中项
    int curDatePosition = -1;//当前日期选中项
    boolean aotuSet = false;//自动设置标志

    public EpgFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EpgFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EpgFragment newInstance(String param1, String param2) {
        EpgFragment fragment = new EpgFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventManager.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(null == rootView){

            rootView   = inflater.inflate(R.layout.fragment_epg, container, false);
            initLayout(rootView);

        }

        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if(null != parent){
            parent.removeView(rootView);
        }
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg){
        if(msg.getEventMode() == EventMode.OUT)
            return;

        switch (msg.getCommand()){
            case COM_LIVE_SET_PROGRAM_LIST:
                setProgramList(parseProgramData(msg.getData()));
                aotuSet = false;
                if(-1 == curProPosition){
                    proItemClick(0);
                }
                break;
            case COM_EPG_SET_SELECT_PROGRAM:
                setDateList(parseDateData(msg.getData()));
                if(aotuSet){
                    Log.i(TAG,"自动设置date");
                    dateItemClick(curDatePosition);
                }else if(-1 == curDatePosition){
                    dateItemClick(0);
                }
                break;
            case COM_EPG_CLASH_RESERVE:
                break;
            case COM_SYSTEM_RESPOND:    //回应
                Respond respond = DataParse.getRespond(msg.getData());
                switch (respond.getCommand()){
                    case COM_CONNECT_DETACH:
                        if(respond.getFlag()){
                            detach();
                        }
                        break;
                    case COM_CONNECT_ATTACH:
                        if(respond.getFlag()){
                            attach();
                        }
                        break;
                    case COM_EPG_SET_RESERVE:
                        if(respond.getFlag()){
                            aotuSet = true;
                            getSelectEpgInfo(curProgram.getIndex());
                        }
                    default:
                        break;
                }
                break;
            default:
                break;
        }

    }


    protected void initLayout(View view) {
        TitleBar mTitleBar = (TitleBar) view.findViewById(R.id.titlebar);
        mTitleBar.setTitleInfoWithText(R.string.app_name);

        proText = (TextView)view.findViewById(R.id.programText);
        dateText = (TextView)view.findViewById(R.id.dateText);
        mainFrame = (ViewGroup) view.findViewById(R.id.contentFrame);
        proFrame = (ViewGroup) view.findViewById(R.id.programFrame);
        dateFrame = (ViewGroup) view.findViewById(R.id.dateFrame);

        /*监听列表点击事件*/
        programListView = (ListView)view.findViewById(R.id.programListView);
        programListView.setOnItemClickListener(new ItemClickProListener());

        dateListView = (ListView)view.findViewById(R.id.dateListView);
        dateListView.setOnItemClickListener(new ItemClickDateListener());

        epgListView =  (ListView)view.findViewById(R.id.epgView);
        epgListView.setOnItemClickListener(new ItemClickEpgListener());


         /*监听节目、日期按钮事件*/
        ImageButton proBtn = (ImageButton)view.findViewById(R.id.programBtn);
        proBtn.setOnClickListener(new ClickProgramListener());

        proText.setOnClickListener(new ClickProgramListener());

        ImageButton timeBtn = (ImageButton)view.findViewById(R.id.timeBtn);
        timeBtn.setOnClickListener(new ClickDateListener());

        dateText.setOnClickListener(new ClickDateListener());

         /*监听退出按钮,隐藏节目列表*/
        ImageButton exitProBtn = (ImageButton) view.findViewById(R.id.programExit);
        exitProBtn.setOnClickListener(new ClickExitFrame());

        ImageButton exitDateBtn = (ImageButton) view.findViewById(R.id.dateExit);
        exitDateBtn.setOnClickListener(new ClickExitFrame());

        errorMaskView = (ErrorMaskView)rootView.findViewById(R.id.maskView);
        if( SystemInfo.getInstance().getState() == SystemState.DETACH){
            detach();
        }
    }


    /**
     * click program listener event
     */
    class ClickProgramListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //initProView(programList);
            showProgramFrame();
            if(null == programList){
                getProgramList();
            }
        }
    }

    /**
     *click date listener event
     */
    class ClickDateListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //initDateView(dateList);
           showDateFrame();
        }
    }

    class ClickExitFrame implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            showMainFrame();
        }
    }

    /**
     * 节目列表点击事件
     */
    class ItemClickProListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(null == programList){
                return;
            }

            proItemClick(position);
        }
    }

    private void proItemClick(int position){
        curProPosition = position;
        curProgram = programList.get(position);
        proText.setText(curProgram.getName());
        setTextMarquee(proText);//设置内容超出时，跑马灯效果

        getSelectEpgInfo(curProgram.getIndex());
        showDateFrame();
    }

    /**
     * 日期列表点击事件
     */
    class ItemClickDateListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(null == dateList){
                return;
            }

            dateItemClick(position);
        }
    }

    private void dateItemClick(int position){
        curDatePosition = position;
        curDate = dateList.get(position);

        dateText.setText(curDate.getDate());
        setTextMarquee(dateText);//设置内容超出时，跑马灯效果

        setEpgList(curDate.getTimeArray());
        showMainFrame();
    }

    /**
     * epg列表点击事件
     */
    class ItemClickEpgListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(null == dateList){
                return;
            }

            setEpgDialog(epgDetailInfo(curDate.getTimeArray().get(position)));
        }
    }


    class SendReserveSet implements ReserveSetCallback
    {
        @Override
        public void sendSetInfo(Time setInfo) {
            ReserveEventSend reserveEventSend = new ReserveEventSend();
            reserveEventSend.setEventId(setInfo.getEventID());
            reserveEventSend.setIndex(curProgram.getIndex());
            reserveEventSend.setEventType(setInfo.getEventType());
            sendReserveSet(reserveEventSend);
        }
    }



  /*  class ClashDialog {

        //dialog
        AlertDialog alert = null;
        AlertDialog.Builder builder = null;

        int select = 0;

        AppReserveClashEvent eventInfo;

        ClashDialog(AppReserveClashEvent eventInfo) {
            this.eventInfo = eventInfo;
            String conflictEvent = eventInfo.conflictEvent.get(AppReserveClashEvent.eventName) + "\n" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.Date) + "\n" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.startTime) + "->" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.endTime);

            String selectEvent = eventInfo.selectEvent.get(AppReserveClashEvent.eventName) + "\n" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.Date) + "\n" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.startTime) + "->" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.endTime);
            final String[] fruits = new String[]{conflictEvent, selectEvent};


            alert = null;
            builder = new AlertDialog.Builder(EpgActivity.this);
            alert = builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(getResources().getString(R.string.epg_clash_dialog_title))
                    .setSingleChoiceItems(fruits, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            select = which;
                            //Toast.makeText(getApplicationContext(), "你选择了" + fruits[which], Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(getResources().getString(R.string.epg_clash_dialog_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.epg_clash_dialog_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (1 == select) {
                                sendClashInfo();
                            }
                            //Toast.makeText(getApplicationContext(), "你选择了" + select, Toast.LENGTH_SHORT).show();
                        }
                    }).create();
            alert.show();
        }


        void sendClashInfo() {

            Log.i("ss","sendClashInfo");


        }
    }*/

    /*显示控制*/
    public void showDateFrame()
    {
        dateFrame.setVisibility(View.VISIBLE);
        mainFrame.setVisibility(View.GONE);
        proFrame.setVisibility(View.GONE);

    }

    public void showProgramFrame()
    {
        proFrame.setVisibility(View.VISIBLE);

        dateFrame.setVisibility(View.GONE);
        mainFrame.setVisibility(View.GONE);
    }

    public void showMainFrame()
    {
        mainFrame.setVisibility(View.VISIBLE);
        proFrame.setVisibility(View.GONE);
        dateFrame.setVisibility(View.GONE);

    }


    private void setProgramList(String[] programs)
    {
        errorMaskView.setVisibleGone();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (context,android.R.layout.simple_expandable_list_item_1,programs);
        programListView.setAdapter(adapter);
    }

    private String[] parseProgramData(String data){
        //Log.i(TAG,"programList:\n"+data);
        programList = DataParse.getProgramList(data);
        Log.i(TAG,"programList:\n"+ JSON.toJSONString(programList));
        String[] programs = new String[programList.size()];
        int i = 0;
        for (Program p :
                programList) {
            programs[i++] = p.getName();
        }

        return programs;
    }

    private void setDateList(String[] dates)
    {
        errorMaskView.setVisibleGone();
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (context,android.R.layout.simple_expandable_list_item_1,dates);
        dateListView.setAdapter(adapter);
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

    private void setEpgList(ArrayList<Time> timeArrayList){
        epgTimeAdapter = new EpgTimeAdapter(context);
        epgTimeAdapter.setMenuCallback(new SendReserveSet());

        epgListView.setAdapter(epgTimeAdapter);
        epgTimeAdapter.setNotifyDataSetChanged(timeArrayList);

    }


    private void setEpgDialog(LinearLayout epgDetailForm){
        if(null != dialog)
        {
            dialog.dismiss();
            dialog = null;
        }

        ImageButton closeBtn = (ImageButton)epgDetailForm.findViewById(R.id.dialogClose);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;

            }
        });

        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder.setView(epgDetailForm).create();

        dialog = builder.show();
    }


    private LinearLayout epgDetailInfo(Time time){
        LinearLayout epgDetailForm = (LinearLayout)getActivity().getLayoutInflater().inflate( R.layout.epg_dialog,null);
        TextView textView = (TextView)epgDetailForm.findViewById(R.id.dialogProName);
        textView.setText(epgProgram.getName());

        textView = (TextView)epgDetailForm.findViewById(R.id.dialogDate);
        textView.setText(curDate.getDate());

        textView = (TextView)epgDetailForm.findViewById(R.id.dialogStartTime);
        textView.setText(time.getStartTime());

        textView = (TextView)epgDetailForm.findViewById(R.id.dialogEndTime);
        textView.setText(time.getEndTime());

        textView = (TextView)epgDetailForm.findViewById(R.id.dialogEventName);
        textView.setText(time.getEvent());

        textView = (TextView)epgDetailForm.findViewById(R.id.dialogShortDescriptor);
        textView.setText(time.getShortDes());

        return epgDetailForm;
    }

    /**
     * 获取节目列表
     */
    private void getProgramList(){
        Log.i(TAG,"获取节目列表:");
        errorMaskView.setLoadingStatus();
        EventManager.send(COM_LIVE_GET_PROGRAM_LIST,"", EventMode.OUT);
    }

    /**
     * 获取选中的节目epg信息
     */
    private void getSelectEpgInfo(int index){
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_EPG_GET_SELECT_PROGRAM,JSON.toJSONString(indexClass), EventMode.OUT);
    }


    /**
     * 跳转到连接界面
     * 在MainActivity.class中做出反应
     */
    private void jumpToConnect(){
        Log.i(TAG,"jumpToConnect:");
        EventManager.send(COM_SYS_JUMP_CONNECT,"", EventMode.IN);
    }


    /**
     * 发送预定事件设置
     * @param reserveSet
     */
    private void sendReserveSet(ReserveEventSend reserveSet){
        EventManager.send(COM_EPG_SET_RESERVE,JSON.toJSONString(reserveSet), EventMode.OUT);

    }


    private void detach(){
        setProgramList(new String[0]);  //清空节目列表

        errorMaskView.setErrorStatus(true,R.string.jump_connect);
        errorMaskView.setOnRetry(R.string.jump,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToConnect();
            }
        });
    }

    private void attach(){
        errorMaskView.setVisibleGone();
        getProgramList();
    }

    /**
     * 设置textView跑马灯效果
     * @param textView
     */
    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }

}

