package gos.media.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import gos.media.R;
import gos.media.data.IndexClass;
import gos.media.data.Program;
import gos.media.data.ProgramUrl;
import gos.media.data.Respond;
import gos.media.define.CommandType;
import gos.media.define.DataParse;
import gos.media.define.SystemInfo;
import gos.media.enumkey.SystemState;
import gos.media.event.EventManager;
import gos.media.event.EventMsg;
import gos.media.event.MsgKey;
import gos.media.event.EventMode;
import gos.media.view.ErrorMaskView;
import gos.media.view.TitleBar;
import gos.media.activity.*;

import static gos.media.define.CommandType.*;   //导入静态命令集


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private  final String TAG = this.getClass().getSimpleName();
    private View rootView = null;   //缓存页面
    private Context context;
    private ListView listView = null;
    ArrayList<Program> programList;

    private Program  curProgram =null;

    private ErrorMaskView errorMaskView = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveFragment newInstance(String param1, String param2) {
        LiveFragment fragment = new LiveFragment();
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
    public void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(null == rootView){

            rootView   = inflater.inflate(R.layout.fragment_live, container, false);
            initLayout(rootView);
            initData();

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
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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

    //接收内部事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){//对内
            switch (msg.getCommand()){
                case COM_SYS_HEARTBEAT_STOP:
                    detach();
                    break;
                case COM_LIVE_SET_PROGRAM_LIST:
                    setProgramList(parseProgramData(msg.getData()));
                    break;
                case COM_LIVE_SET_PROGRAM_URL:
                    errorMaskView.setVisibleGone();
                    ProgramUrl programUrl = JSON.parseObject(msg.getData(),ProgramUrl.class);
                    if(curProgram == null)
                        break;
                    Log.i(TAG,"getIndex:"+curProgram.getIndex());
                    if(curProgram.getIndex() == programUrl.getIndex()) {//发送的节目索引与接收的节目索引相同
                        startPlayByUrl(programUrl.getUrl());
                    }
                    break;
                case COM_LIVE_UPDATE_PROGRAM_LIST:
                    sendFinishLive();
                    getProgramList();   //节目列表更新时，重新获取节目列表
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
                        case COM_LIVE_STOP_PROGRAM:
                            Log.i(TAG,"停止当前播放节目成功");
                            curProgram = null;
                            break;
                        case COM_LIVE_SET_PROGRAM_URL:
                            if(!respond.getFlag()){     //获取节目url失败
                                errorMaskView.setVisibleGone();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void initLayout(View view) {
        TitleBar mTitleBar = (TitleBar) view.findViewById(R.id.titlebar);
        mTitleBar.setTitleInfoWithText(R.string.app_name);

        listView = (ListView) rootView.findViewById(R.id.programList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null != curProgram){     //停止上一个节目
                    stopProgram(curProgram.getIndex());
                }
                curProgram = programList.get(position);
                Log.i(TAG,"getIndex:\n"+curProgram.getIndex());
                getProgramUrl(curProgram.getIndex());
            }
        });

        errorMaskView = (ErrorMaskView)rootView.findViewById(R.id.maskView);
        if( SystemInfo.getInstance().getState() == SystemState.DETACH){
            detach();
        }

    }

    private void initData(){
        if( SystemInfo.getInstance().getState() == SystemState.ATTACH) {
            getProgramList();
        }
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
     * 获取节目url
     * @param index 节目索引
     */
    private void getProgramUrl(int index){
        Log.i(TAG,"获取节目url:"+index);
        errorMaskView.setLoadingStatus();
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_LIVE_GET_PROGRAM_URL, JSON.toJSONString(indexClass), EventMode.OUT);
    }

    /**
     * 停止节目
     */
    private void stopProgram(int index ){
        Log.i(TAG,"停止节目:"+index);
        IndexClass indexClass = new IndexClass(index);
        EventManager.send(COM_LIVE_STOP_PROGRAM,JSON.toJSONString(indexClass), EventMode.OUT);
    }

    /**
     * 跳转到连接界面
     * 在MainActivity.class中做出反应
     */
    private void jumpToConnect(){
        Log.i(TAG,"jumpToConnect:");
        EventManager.send(COM_SYS_JUMP_CONNECT,"", EventMode.IN);
    }

    private void setProgramList(String[] programs)
    {
        errorMaskView.setVisibleGone();
        //创建ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (context,android.R.layout.simple_expandable_list_item_1,programs);
        //获取ListView对象，通过调用setAdapter方法为ListView设置Adapter设置适配器
        listView.setAdapter(adapter);
    }

    private String[] parseProgramData(String data){
        //Log.i(TAG,"programList:\n"+data);
        programList = DataParse.getProgramList(data);
        Log.i(TAG,"programList:\n"+JSON.toJSONString(programList));
        String[] programs = new String[programList.size()];
        int i = 0;
        for (Program p :
                programList) {
            programs[i++] = p.getName();
        }

        return programs;
    }

    private void startPlayByUrl(String url){
        Intent intent = new Intent(context,LiveActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MsgKey.url,url);
        intent.putExtras(bundle);
        startActivityForResult(intent,0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"onActivityResult:"+resultCode);
        if(null != curProgram){     //停止上一个节目
            stopProgram(curProgram.getIndex());
        }

    }


    /**
     * 结束直播activity
     */
    private void sendFinishLive(){
        EventManager.send(COM_SYS_FINISH_LIVE,"",EventMode.IN);
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
        getProgramList();
    }


}
