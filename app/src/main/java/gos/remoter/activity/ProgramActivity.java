package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.Respond;
import gos.remoter.database.DBDao;
import gos.remoter.define.DataParse;
import gos.remoter.define.SystemApplication;
import gos.remoter.enumkey.SystemState;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class ProgramActivity extends Activity implements AdapterView.OnItemClickListener{
    private  final String TAG = this.getClass().getSimpleName();

    private ArrayList<Program> programList;
    private List<Program> databaseList;

    private TextView programNull;
    private ListView programView;
    private View underline;
    private Program curProgram = null;
    private Program favorProgram;//用来修改库中的favor
    private DBDao db;

    private boolean isUpdate = false;//标志服务器是否发送更新的列表


    ReuseAdapter<Program> adapter = new ReuseAdapter<Program>(R.layout.item_program_list) {
        @Override
        public void bindView(final ViewHolder holder, Program obj, final int position) {
//            holder.setText(R.id.textLcn,obj.getLcn()+"");
            if(null != obj) {
                holder.setText(R.id.textNum, (position + 1) +"");
                holder.setText(R.id.textName, obj.getName());
                holder.setImageResource(R.id.textEdit, R.drawable.programlist_fav_selected, R.drawable.programlist_fav_nomal, obj.getFavor());

                holder.setOnClickListener(R.id.textEdit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if( !databaseList.get(position).getFavor()) {
                           setSelectSinger(position, true);
                           holder.setImageResource(R.id.textEdit, R.drawable.programlist_fav_selected, R.drawable.programlist_fav_nomal, true);

                       } else {
                            setSelectSinger(position, false);
                           holder.setImageResource(R.id.textEdit, R.drawable.programlist_fav_selected, R.drawable.programlist_fav_nomal, false);
                       }
                        Log.e(TAG, "查询数据库里的信息" + db.qryAllInfo());

                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        EventManager.register(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(db != null && null != adapter) {
            databaseList = db.qryAllInfo();
            adapter.reset(databaseList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMsg msg){
        if(msg.getEventMode() == EventMode.OUT)
            return;
        Log.e(TAG,"收到的命令" + msg.getCommand());
        Log.e(TAG,"收到的数据" + msg.getData());
        switch (msg.getCommand()){
            case COM_SYS_HEARTBEAT_STOP:
                detach();
                break;
            case COM_PROGRAM_SET_All_LIST:
                setProgramList(msg.getData());
                break;
            case COM_PROGRAM_UPDATE_ALL_LIST:
                if(null != adapter) {
                    adapter.clear();
                }
                isUpdate = true;
                getProgramList();

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
                            //attach();
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

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.home_program_list);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setImageRight(R.drawable.programlist_fav, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ProgramActivity.this, ProgramFavor.class);
                startActivity(intent);
            }
        });

        programNull = (TextView) findViewById(R.id.programList_null);
        programView = (ListView)findViewById(R.id.programView);
        underline = findViewById(R.id.underline);

        underline.setVisibility(View.GONE);
        programView.setOnItemClickListener(this);
        programView.setSelector(R.color.deepgray);
        programView.setAdapter(adapter);

        if(SystemState.DETACH == SystemApplication.getInstance().getState()) {
            programNull.setVisibility(View.VISIBLE);
            programView.setVisibility(View.GONE);
        } else {
            programNull.setVisibility(View.GONE);
            programView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        curProgram = programList.get(position);
        if(SystemApplication.getInstance().getState() == SystemState.ATTACH ) {
            switchProgram(curProgram);
        } else {
            Toast.makeText(getBaseContext(), R.string.program_list_error_net, Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        if( SystemApplication.getInstance().getState() == SystemState.ATTACH) {
            getProgramList();
        }
    }

    /**
     * 更新指定位置数据
     * @param position
     * @param favor
     */
    private void setSelectSinger(int position, boolean favor) {

        databaseList.get(position).setFavor(favor);
        changeFavorStatus(position);
    }

    /**
     * 获取节目列表
     */
    private void getProgramList(){
        Log.i(TAG,"获取节目列表:");
        //errorMaskView.setLoadingStatus();
        EventManager.send(COM_PROGRAM_GET_All_LIST,"", EventMode.OUT);
    }

    /**
     * 解析数据，进行列表设置
     * @param data
     */
    private void setProgramList(String data) {

        Log.i(TAG,"是否更新---" + isUpdate);
        programList = DataParse.getProgramList(data);
        if(null != programList) {
            underline.setVisibility(View.VISIBLE);
            addDatabase();//重新设置数据，需判断 同步
        } else {
            databaseList = null;
        }
        adapter.reset(databaseList);//programList
        adapter.notifyDataSetChanged();

    }

    /**
     * 存入数据库，列表显示数据库的内容
     */
    private void addDatabase() {
        db = new DBDao(this, Program.class);//添加表的类
        databaseList = db.qryAllInfo();
        Log.e(TAG, "数据库是否为空" + databaseList);
        if(databaseList.size() == 0) {
            db.addlist(programList);
            databaseList = db.qryAllInfo();
            Log.e(TAG, "数据库所有数据" + databaseList);

        } else if(isUpdate) {
            isUpdate = false;
            updateList();
        }

    }

    /**
     * 数据库已存在数据，服务器发送更新命令
     * 同步之前数据和新解析的数据
     */
    private void updateList() {

        for(int i = 0;i < databaseList.size(); i ++) {
            for(int j = 0; j < programList.size(); j ++) {
                if(databaseList.get(i).getName().equals(programList.get(j).getName()) && databaseList.get(i).getFavor()) {
                    programList.get(j).setFavor(true);
                    Log.i(TAG, "同步数据" + programList.get(j));
                }
            }

        }
        db.deleteAll();
        db.addlist(programList);
        databaseList = db.qryAllInfo();
        Log.e(TAG, "数据库更新后的数据" + databaseList);
    }

    /**
     * 更新数据库
     * @param position
     */
    private void changeFavorStatus(int position) {
        favorProgram = databaseList.get(position);
        favorProgram.setId(favorProgram.getId());
        db.updateInfo(favorProgram);
        Log.e(TAG, "喜爱状态改变后的数据---" + favorProgram);
        Log.i(TAG, "改变后的数据库---" + db.qryAllInfo());
    }

    /**
     * 切换盒子上的节目
     * @param program
     */
    private void switchProgram(Program program) {
        Log.e(TAG, "切换盒子上的节目" + program.getIndex());
        IndexClass indexClass = new IndexClass(program.getIndex());
        EventManager.send(COM_PROGRAM_STB_SWITCH, JSON.toJSONString(indexClass), EventMode.OUT);
    }

    private void detach(){
        Log.i(TAG,"断开连接");
        Toast.makeText(this,getResources().getString(R.string.connect_detach), Toast.LENGTH_SHORT).show();

        //设置系统状态为断开连接
        SystemApplication.getInstance().setState(SystemState.DETACH);
    }


}
