package gos.remoter.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gos.remoter.R;
import gos.remoter.adapter.ServiceSettingListAdapter;
import gos.remoter.data.Device;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.define.InputCheck;
import gos.remoter.define.NetProtocol;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.service.NetService;
import gos.remoter.view.TitleBar;

import static gos.remoter.define.CommandType.*;   //导入静态命令集


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFragment extends Fragment implements OnClickListener,OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private  final String TAG = this.getClass().getSimpleName();
    private View rootView = null;   //缓存页面
    private Context context;
    
    private LinearLayout ll_connect = null;
    private LinearLayout ll_disconnect = null;
    private EditText editText = null;
    private Button button = null;
    private ListView listview = null;
    private ImageView iv_delete = null;
    private ImageView iv_disconnect = null;
    private TextView textDisconnect = null;
    private ProgressDialog mProgressDialog;
    private int updateList = 0;//更新列表

    private ServiceSettingListAdapter serviceSettingListAdapter = null;

    private ArrayList<Device> deviceList = new ArrayList<>();
    private ArrayList<String> myAddress = null;//更新后
    private ArrayList<String> allAddress = new ArrayList<>();
    private String edit = "";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static int PERMISSION_CAMERA = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConnectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectFragment newInstance(String param1, String param2) {
        ConnectFragment fragment = new ConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    edit = editText.getText().toString();
                    myAddress = new ArrayList<>();
                    for(int i = 0; i <  allAddress.size(); i++){
                        if(allAddress.get(i).contains(edit)){
                            myAddress.add(allAddress.get(i));
                        }
                    }
                    serviceSettingListAdapter.notifyDataSetChanged(myAddress);
                    break;
            }
        }
    };

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
            //Log.i(TAG,"null == rootView");
            rootView = inflater.inflate(R.layout.fragment_connect, container, false);
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
      /*  if (context instanceof OnFragmentInteractionListener) {
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
                case COM_CONNECT_SET_DEVICE:
                    addDevice(msg.getData());
                    break;
                case COM_SYS_EXIT:  //系统退出时，断开连接
                    detachDevice();
                    break;
                case COM_SYSTEM_RESPOND:
                    Respond respond = DataParse.getRespond(msg.getData());
                    switch (respond.getCommand()){
                        case COM_CONNECT_DETACH:
                            if(respond.getFlag()){
                                detach();
                            }else{
                                Log.i(TAG,"断开连接失败");
                            }
                            break;
                        case COM_CONNECT_ATTACH:
                            if(respond.getFlag()){
                                attach();
                            }else {
                                Log.i(TAG,"连接设备失败");
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

    /**
     * 初始化页面
     * @param view 页面组件
     */
    private void initLayout(View view) {
        //顶部标题栏
        TitleBar mTitleBar = (TitleBar) view.findViewById(R.id.titlebar);
        mTitleBar.setTitleInfoWithText(R.string.app_name, R.drawable.ic_scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "scan", Toast.LENGTH_SHORT).show();
                startScan();
            }
        });

        ll_connect = (LinearLayout) view.findViewById(R.id.ll_connect);
        ll_disconnect = (LinearLayout) view.findViewById(R.id.ll_disconnect);
        editText = (EditText) view.findViewById(R.id.edit_server_ip);
        button = (Button) view.findViewById(R.id.btn_setting);
        listview = (ListView) view.findViewById(R.id.list_hint);
        iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        iv_disconnect = (ImageView) view.findViewById(R.id.iv_disconnect);
        textDisconnect = (TextView)view.findViewById(R.id.tx_disconnect);

        editText.setOnClickListener(this);
        button.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        iv_delete.setOnClickListener(this);
        iv_disconnect.setOnClickListener(this);
        textDisconnect.setOnClickListener(this);


        serviceSettingListAdapter = new ServiceSettingListAdapter(context, allAddress);
        listview.setAdapter(serviceSettingListAdapter);

        //添加EditText文本改变的监听器
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //文本框改变之前会执行的动作
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    iv_delete.setVisibility(View.GONE);//当文本框为空时，则叉叉消失
                }
                else {
                    iv_delete.setVisibility(View.VISIBLE);//当文本框不为空时，出现叉叉
                    Message message = new Message();
                    message.obj = charSequence;
                    message.what = updateList;
                    myHandler.sendMessage(message);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //文本框改变之后会执行的动作
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_setting:
                String editAddress = editText.getText().toString();
                Device connectDevice = new Device(editAddress,"","");
                if(checkDevice(connectDevice)){
                    attachDevice(connectDevice);
                }
                break;
            case R.id.edit_server_ip:
                listview.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_delete:
                editText.setText("");
                serviceSettingListAdapter.notifyDataSetChanged(allAddress);
                break;
            /*点击图标或者按钮都断开连接*/
            case R.id.iv_disconnect:
            case R.id.tx_disconnect:
                detachDevice();//断开连接
                break;
            default:
                break;
        }
    }

    private void checkEditText() {
        String editAddress = editText.getText().toString();
        if((InputCheck.isboolIp(editAddress))){
            Log.i(TAG,"isboolIp true-----");
            Device connectDevice = findDeviceByIp(editAddress);
            if(null == connectDevice){
                connectDevice = new Device(editAddress,"","");
            }
            attachDevice(connectDevice);
        } else{
            Log.i(TAG,"isboolIp false-----");
            Toast.makeText(context, R.string.connect_format, Toast.LENGTH_SHORT).show();
            editText.setText("");
        }

    }


    /**
     * 检查输入设备信息是否正确
     * @param inDevice  输入设备
     * @return  true正确 false错误
     */
    private boolean checkDevice(Device inDevice){
        String ip = inDevice.getIp();
        if((InputCheck.isboolIp(ip))){
            Device outDevice  = findDeviceByIp(ip);
            if(null != outDevice){  //如果列表中存在，则更新输入的设备信息
                inDevice.setId(outDevice.getId());
                inDevice.setMac(outDevice.getMac());
                Log.i(TAG,"列表中存在，更新输入的设备信息");
            }else{
                Log.i(TAG,"列表中不存在，添加输入的设备信息");
                allAddress.add(inDevice.getIp());
                deviceList.add(inDevice);
            }
            editText.setText(inDevice.getIp());
            return true;
        } else{
            Toast.makeText(context, R.string.connect_format, Toast.LENGTH_SHORT).show();
            editText.setText("");
        }
        return false;
    }

    /**
     * 添加设备
     * @param data 设备类json字符串
     */
    private void addDevice(String data){
        try{
            Device device = DataParse.getDevice(data);
            Log.i(TAG,"device:\n"+ JSON.toJSONString(device));
            checkDevice(device);
        }catch (com.alibaba.fastjson.JSONException e){
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.scan_data_parse_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据ip查找列表中的设备
     * @param ip 输入的ip
     * @return  未查找到null
     */
    private Device findDeviceByIp(String ip){
        for (int i=0;i<deviceList.size();i++){
            if(deviceList.get(i).getIp().equals(ip)){
                return deviceList.get(i);
            }
        }
        return null;
    }


    public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
    {
        editText.setText(allAddress.get(position));
        editText.clearFocus();  //清除输入框焦点，隐藏小键盘
    }

    private void initData(){
        getDevice();
    }
    private void getDevice(){
        EventManager.send(COM_CONNECT_GET_DEVICE,"", EventMode.OUT);
    }

    private void attachDevice(Device device){
        Toast.makeText(context, getResources().getString(R.string.connect_try_connect), Toast.LENGTH_SHORT).show();
        try {
            if(null != NetService.netSender){//如果发送套接字不为空，则关闭
                NetService.netSender.close();
                NetService.netSender = null;
            }
            NetService.netSender = new NetProtocol.UdpUnicastSocket(device.getIp(), NetProtocol.sendPort, NetProtocol.SocketType.SEND);
            System.out.println("发送");
            System.out.println("port:"+NetService.netSender.getPort());
            System.out.println("ip:"+NetService.netSender.getAddress());
            String data = JSON.toJSONString(device);
            EventManager.send(COM_CONNECT_ATTACH,data, EventMode.OUT);
            //设置服务器设备信息
            SystemInfo.getInstance().setService(device);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //断开与服务器的连接
    private void detachDevice(){
        if(SystemState.ATTACH == SystemInfo.getInstance().getState()) {
            EventManager.send(COM_CONNECT_DETACH, "", EventMode.OUT);
        }
    }

    /**
     * 跳转
     * @param delay    延时毫秒
     */
    private void jumpToLiveDelay(int delay){
        Log.i(TAG,"跳转到连接界面，延时"+delay+"ms");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                EventManager.send(COM_SYS_JUMP_LIVE,"", EventMode.IN);
            }
        },delay);
    }

    private void startScan(){
        //点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
        //扫描完了之后调到该界面
       if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
           jumpToScan();
       }else {
           this.requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
        }
    }

    private void jumpToScan(){
        Log.i(TAG,"jumpToScan");
        Intent intent = new Intent();
        intent.setClass(context,qr.zxing.MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG,"权限开启结果");
        switch(requestCode){
            case  PERMISSION_CAMERA ://如果用户取消，permissions可能为null.
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //同意
                    jumpToScan();
                }else{//拒绝
                    Toast.makeText(context,getResources().getString(R.string.allow_camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == getActivity().RESULT_OK){
                    //显示扫描到的内容
                    Bundle bundle = data.getExtras();
                    String retData = bundle.getString("result");
                    Log.i(TAG,retData);
                    try{
                        Device retDevice =  DataParse.getDevice(retData);
                        System.out.println(JSON.toJSONString(retDevice));
                        if(checkDevice(retDevice)){
                            attachDevice(retDevice);
                        }
                    }catch (com.alibaba.fastjson.JSONException e){
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.scan_data_parse_error), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void attach(){
        Log.i(TAG,"连接设备成功");
        listview.setVisibility(View.GONE);
        Toast.makeText(context,getResources().getString(R.string.connect_attach), Toast.LENGTH_SHORT).show();
        ll_connect.setVisibility(View.GONE);
        ll_disconnect.setVisibility(View.VISIBLE);
        //设置系统状态为已连接
        SystemInfo.getInstance().setState(SystemState.ATTACH);
        jumpToLiveDelay(500);
    }

    private void detach(){
        Log.i(TAG,"断开连接成功");
        Toast.makeText(context,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();
        ll_connect.setVisibility(View.VISIBLE);
        ll_disconnect.setVisibility(View.GONE);
        //设置系统状态为断开连接
        SystemInfo.getInstance().setState(SystemState.DETACH);
    }


}
