package gos.remoter.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.Device;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.define.NetProtocol;
import gos.remoter.define.SystemInfo;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.service.NetService;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.tool.SystemClear;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class ConnectActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private final static int SCANNING_GREQUEST_CODE = 1;
    private final static int PERMISSION_CAMERA = 2;

    ReuseAdapter<Device> deviceAdapter = new ReuseAdapter<Device>(R.layout.item_connect_device) {
        @Override
        public void bindView(ViewHolder holder, Device obj) {
            holder.setText(R.id.address,obj.getIp());
        }
    };

    private Spinner deviceSpinner;
    private Device selectDevice;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        System.gc();
        view = findViewById(R.id.connect);
        ACTCollector.add(this);//添加到收集器

        initView();
        initData();
        EventManager.register(this);
    }

    @Override
    protected void onDestroy() {
        EventManager.unregister(this);
        ACTCollector.remove(ACTCollector.getByName(this));//从收集器移除
        if(ACTCollector.isEmpty()){
            sendExitSystem();
        }
        Log.e(TAG,"销毁");
        super.onDestroy();
    }

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_NET_ENABLE:
                    sendFindDevice();
                    break;
                case COM_NET_DISABLE:
                    deviceAdapter.clear();
                    break;
                case COM_SYS_HEARTBEAT_STOP: {
                    break;
                }
                case COM_CONNECT_SET_DEVICE: {
                    Device device = DataParse.getDevice(msg.getData());
                    deviceAdapter.add(device);
                    break;
                }
                case COM_SYS_EXIT: {
                    //系统退出时，断开连接
                    finish();
                    break;
                }
                case COM_SYSTEM_RESPOND: {
                    Respond respond = DataParse.getRespond(msg.getData());
                    switch (respond.getCommand()) {
                        case COM_CONNECT_DETACH:
                            if (respond.getFlag()) {

                            } else {
                                Log.i(TAG, "断开连接失败");
                            }
                            break;
                        case COM_CONNECT_ATTACH:
                            if (respond.getFlag()) {
                                attach();

                            } else {
                                Log.i(TAG, "连接设备失败");
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                }
                default:
                    break;

            }
        }
    }

    void initView(){
        //沉浸式隐藏标题栏
        new ImmersionLayout(this).setImmersion();

        //标题栏
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setNullBackground();
        titleBar.setTextTitle(R.string.connect_title);

        titleBar.setImageRight(R.drawable.connect_scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
                Toast.makeText(ConnectActivity.this, "open scan", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null != selectDevice){
                  attachDevice(selectDevice);
                }
            }
        });

        deviceSpinner = (Spinner)findViewById(R.id.deviceSpinner);
        deviceSpinner.setAdapter(deviceAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDevice = deviceAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initData(){
        startService();
        sendFindDevice();
    }
    private void sendFindDevice(){
        EventManager.send(COM_CONNECT_GET_DEVICE,"", EventMode.OUT);
    }

    private void startScan(){
        //点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
        //扫描完了之后调到该界面
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            jumpToScan();
        }else {
            //requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
    }

    private void jumpToScan(){
        Log.i(TAG,"jumpToScan");
        Intent intent = new Intent();
        intent.setClass(this,qr.zxing.MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNING_GREQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG,"权限开启结果");
        switch(requestCode){
            case  PERMISSION_CAMERA ://如果用户取消，permissions可能为null.
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //同意
                    jumpToScan();
                }else{//拒绝
                    Toast.makeText(this,getResources().getString(R.string.allow_camera_permission), Toast.LENGTH_SHORT).show();
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
            case SCANNING_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    //显示扫描到的内容
                    Bundle bundle = data.getExtras();
                    String retData = bundle.getString("result");
                    Log.i(TAG,retData);
                    try{
                        Device retDevice =  DataParse.getDevice(retData);
                        System.out.println(JSON.toJSONString(retDevice));
                        /*if(checkDevice(retDevice)){
                            attachDevice(retDevice);
                        }*/
                    }catch (com.alibaba.fastjson.JSONException e){
                        e.printStackTrace();
                        Toast.makeText(this, getResources().getString(R.string.scan_data_parse_error), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void attach(){

        Toast.makeText(this,getResources().getString(R.string.connect_attach), Toast.LENGTH_SHORT).show();

        //设置系统状态为已连接
        SystemInfo.getInstance().setState(SystemState.ATTACH);
        startHomeActivity();
        finish();
    }

    private void detach(){
        Log.i(TAG,"断开连接成功");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemInfo.getInstance().setState(SystemState.DETACH);
    }

    private void attachDevice(Device device){
        Toast.makeText(this, getResources().getString(R.string.connect_try_connect), Toast.LENGTH_SHORT).show();
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


    void startHomeActivity(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }


    /**
     * 退出系统
     */
    void sendExitSystem(){
        if(SystemState.EXIT != SystemInfo.getInstance().getState()) {
            Log.e(TAG,"退出系统");
            EventManager.send(COM_SYS_EXIT,"",EventMode.IN);
        }
    }

    void startService(){
        //start NetService
        Intent intent = new Intent(this, NetService.class);
        startService(intent);
    }

}
