package gos.remoter.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import gos.remoter.R;
import gos.remoter.data.Device;
import gos.remoter.data.Respond;
import gos.remoter.define.CommandType;
import gos.remoter.define.DataParse;
import gos.remoter.define.InputCheck;
import gos.remoter.define.SystemApplication;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.service.NetService;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.tool.SharedPreferencesUtils;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class ConnectActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private final static int SCANNING_GREQUEST_CODE = 1;
    private final static int PERMISSION_CAMERA = 2;
    private final static int MAX_HISTORY_COUNT = 5;//最大保存记录
    private final static String SP_NAME = "ipAddress";//文件名
    private final static String SP_EMPTY_TAG = "empty";
    private final static String SP_KEY_SEARCH = "search";
    public final static String SP_KEY_SUCCESS = "success";

    private AutoCompleteTextView autoTxt;
    private ImageView deleteIamge;
    private RelativeLayout relative;
    private ArrayAdapter<String> deviceAdapter;
    private ArrayList<String> saveAdapterData;//更新适配器里的数据，不超过最大保存记录

    private Device selectDevice;
    private TextView textVersionName;

   /* ReuseAdapter<Device> deviceAdapter = new ReuseAdapter<Device>(R.layout.item_connect_device) {
        @Override
        public void bindView(ViewHolder holder, Device obj) {
            holder.setText(R.id.address,obj.getIp());
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        System.gc();
        ACTCollector.add(this);//添加到收集器
        initView();
        initData();
        EventManager.register(this);
    }

    @Override
    protected void onDestroy() {
        EventManager.unregister(this);
        ACTCollector.remove(this);//从收集器移除
        if(ACTCollector.isEmpty()){
            sendExitSystem();
        }

        Log.e(TAG,"销毁");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(deviceAdapter != null) {
            Log.e(TAG, "重连----" + SharedPreferencesUtils.get(SP_KEY_SEARCH));

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            ACTCollector.finishAll();
            sendExitSystem();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case COM_NET_SOCKET_PREPARED:
                    sendFindDevice();
                    break;
                case COM_NET_DISABLE:
                    break;
                case COM_SYS_HEARTBEAT_STOP: {
                    detach();
                    break;
                }
                case COM_CONNECT_SET_DEVICE: {
                    Log.e(TAG, msg.getData());
                    Device device = DataParse.getDevice(msg.getData());
                    if(checkDevice(device.getIp())) {
//                        autoTxt.setText(device.getIp());
                        saveSearchHistory(device.getIp());
                    }
                    //deviceAdapter.add(device);
                    break;
                }
                case COM_SYS_EXIT: {

                    SharedPreferencesUtils.clear();
                    if(deviceAdapter != null) {
                        deviceAdapter.clear();
                     }
                    //系统退出时，断开连接
                    detachDevice();
                    finish();
                    break;
                }
                case COM_SYSTEM_RESPOND: {
                    Respond respond = DataParse.getRespond(msg.getData());
                    switch (respond.getCommand()) {
                        case COM_CONNECT_DETACH:
                            if (respond.getFlag()) {
                                detach();
                            } else {
                                Log.i(TAG, "断开连接失败");
                            }
                            break;
                        case COM_CONNECT_ATTACH:
                            if (respond.getFlag()) {
                                attach();
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
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
        titleBar.setTextTitle(R.string.homeTitle);

        titleBar.setImageRight(R.drawable.connect_scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        initAutoTextView();
        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputText();
                autoTxt.dismissDropDown();//关闭下拉提示框

                if(null != selectDevice && autoTxt.getText().toString().length() != 0){
                    attachDevice(selectDevice);
                }
            }
        });

        textVersionName = (TextView) findViewById(R.id.versionName);

    }

    private void initAutoTextView() {
        autoTxt = (AutoCompleteTextView) findViewById(R.id.autoTxt);
        deleteIamge = (ImageView) findViewById(R.id.deleteImage);
        relative = (RelativeLayout) findViewById(R.id.relative);

//        autoTxt.setDropDownHeight(270);// 设置下拉提示框的高度为200dp
        autoTxt.setThreshold(1);// 设置输入1个字符就自动提示,默认2个
        autoTxt.setSingleLine(true); // 设置单行输入限制
        autoTxt.setDropDownHorizontalOffset(10);	//设置下拉菜单于文本框之间的水平偏移量

        saveAdapterData = new ArrayList(Arrays.asList(getHistoryArray(SP_KEY_SEARCH)));
        //ArrayAdapter(Context, int, List), 第三个参数传入ArrayList, 将Objects[] 转化为 ArrayList，否则易异常

        deviceAdapter = new ArrayAdapter<>(this, R.layout.item_connect_device, saveAdapterData);
        autoTxt.setAdapter(deviceAdapter);  // 设置适配器

        autoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTxt.setCursorVisible(true);
                deleteIamge.setVisibility(View.VISIBLE);
                relative.setPadding(0, 0, 0, 150);  //使得键盘不挡住编辑框

            }
        });
        autoTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ip = deviceAdapter.getItem(position);
                autoTxt.setText(ip);
                selectDevice = new Device(ip, "", "");
                autoTxt.setSelection(ip.length());//设置光标位置

            }
        });

        deleteIamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTxt.setText("");
            }
        });
    }

    /**
     * 同步键盘的 确定键
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(ConnectActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initData(){
        getPackageVersionName();
        startService();
//        saveSearchHistory();
        // sendFindDevice();
    }

    /**
     * 最多只提示最近的5条历史记录,MAX_HISTORY_COUNT,
     *
     *   方法存在问题
     * @param key
     * @return
     */
    private String[] getHistoryArray(String key) {
        String[] array = SharedPreferencesUtils.get(key).split(",");
        if (array.length > MAX_HISTORY_COUNT) {
            String[] newArray = new String[MAX_HISTORY_COUNT];
            System.arraycopy(array, 0, newArray, (array.length - 5), MAX_HISTORY_COUNT); // 实现数组间的内容复制
            Log.e(TAG,"超出数量5");
            return newArray;
        }
        return array;
    }

    /**
     * 保存历史记录数据到SharedPreferences中
     * trim()返回调用字符串对象的一个副本，但是所有起始和结尾的空格都被删除了
     */
    private void checkInputText() {
        String text = autoTxt.getText().toString().trim();       // 获取搜索框文本信息  .trim()
        if (TextUtils.isEmpty(text)) {                      // null or ""
            deleteIamge.setVisibility(View.GONE);
            return;
        }
        autoTxt.showDropDown();//让下拉框弹出来
        if(checkDevice(text)) {
            selectDevice = new Device(text, "", "");
        }

    }

    /**
     * 检查输入设备信息的格式是否正确
     * @param ip  输入设备
     * @return  true正确 false错误
     *
     */
    private boolean checkDevice(String ip){
        if(InputCheck.isboolIp(ip)) {
            return true;
        } else {
            autoTxt.setText("");
            Toast.makeText(this, "格式错误，请重新输入", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     *  判断是否已存在于历史文件中，已存在则不再添加
     * @param ip
     */
    private  void saveSearchHistory(String ip) {
        String old_text = SharedPreferencesUtils.get(SP_KEY_SEARCH);// 获取SP中保存的历史记录
        StringBuilder builder;
        if (SP_EMPTY_TAG.equals(old_text)) {
            builder = new StringBuilder();
        } else {
            builder = new StringBuilder(old_text);
        }
        builder.append(ip + ",");      // 使用逗号来分隔每条历史记录
        Log.e(TAG, "builder---" + builder);

        if (!old_text.contains(ip + ",")) {
            SharedPreferencesUtils.save(SP_KEY_SEARCH, builder.toString());
            deviceAdapter.add(ip);        // 实时更新下拉提示框中的历史记录
            deviceAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 发送查找设备
     * Event事件已经发布，此时还没有注册订阅。
     * 当设置了sticky = true，在Event的事件发布后，进行注册。依然能够接收到之前发布的事件
     */
    private void sendFindDevice(){
        Log.i(TAG,"发送粘性事件 获取设备");
        EventManager.sendSticky(COM_CONNECT_GET_DEVICE,"", EventMode.OUT);
        //        checkDevice(new Device("192.168.100.113", "", ""));  //服务器不回应时，设置默认ip
    }

    /**
     * 先判断是否已经打开权限，如果权限已经打开，则直接跳转；否则申请相应权限,
     * 点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
     * 扫描完了之后跳回界面
     *
     * 6.0之前的系统使用checkPermission()和checkSelfPermission()方法判断相机权限，
     * 只是判断清单文件中是否注册过此类权限，用户的操作是获取不到的
     */
    private void startScan(){
        //ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (cameraIsCanUse()){
            jumpToScan();
        }else {
            //设置权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
        }
    }

    /**
     * 判断相机权限
     *  返回true 表示可以使用  返回false表示不可以使用
     */
    public boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }
        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    private void jumpToScan(){
        Log.i(TAG,"jumpToScan");
        Intent intent = new Intent();
        intent.setClass(this,qr.zxing.MipcaActivityCapture.class);
        //如果activity在task存在，将Activity之上的所有Activity结束掉
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNING_GREQUEST_CODE);
    }

    /**
     * 相机权限请求
     * 用户选择允许或拒绝后，会回调onRequestPermissionsResult()
     * @param requestCode  请求码
     * @param permissions   权限
     * @param grantResults  授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG,"权限开启结果");
        switch(requestCode){
            case  PERMISSION_CAMERA ://如果用户取消，permissions可能为null.
                //grantResults[0] == PackageManager.PERMISSION_GRANTED
                if(cameraIsCanUse() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //同意
                    jumpToScan();
                } else{//拒绝
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
                        if(checkDevice(retDevice.getIp())) {
                            attachDevice(retDevice);
                            saveSearchHistory(retDevice.getIp());//二维码扫出的结果
                        }
                       /* if(!deviceAdapter.exist(retDevice)){//不存在则添加
                            deviceAdapter.add(retDevice);
                        }*/
                        Log.i(TAG,"扫码连接设备");
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
        SystemApplication.getInstance().setState(SystemState.ATTACH);
        startHomeActivity();
        saveSearchHistory(selectDevice.getIp());

        finish();
    }

    private void detach(){
        Log.i(TAG,"断开连接成功");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemApplication.getInstance().setState(SystemState.DETACH);
    }

    /**
     * 连接设备
     * @param device
     */
    private void attachDevice(Device device){
        Toast.makeText(this, getResources().getString(R.string.connect_try_connect), Toast.LENGTH_SHORT).show();
        String data = JSON.toJSONString(device);
        EventManager.send(COM_CONNECT_ATTACH,data, EventMode.OUT);
        //设置服务器设备信息
        SystemApplication.getInstance().setService(device);
        Log.e(TAG, "getService().getIp()--" +  SystemApplication.getInstance().getService().getIp());

        SharedPreferencesUtils.save(SP_KEY_SUCCESS, device.getIp());

    }

    //断开与服务器的连接
    private void detachDevice(){
        if(SystemState.ATTACH == SystemApplication.getInstance().getState()) {
            EventManager.send(CommandType.COM_CONNECT_DETACH, "", EventMode.OUT);
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
        if(SystemState.EXIT != SystemApplication.getInstance().getState()) {
            Log.e(TAG,"退出系统");
            EventManager.send(COM_SYS_EXIT,"",EventMode.IN);
        }
    }

    void startService(){
        //start NetService
        Intent intent = new Intent(this, NetService.class);
        startService(intent);
    }

    /**
     * 获取软件版本名称
     */
    public void getPackageVersionName() {
        PackageManager manager = getBaseContext().getPackageManager();
        String versionName = "";
        try {
            PackageInfo info = manager.getPackageInfo(getBaseContext().getPackageName(), 0);
            versionName = info.versionName;//info.versionCode
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("versionName-------", versionName);
        textVersionName.setText("EditionV " + versionName);
    }

}
