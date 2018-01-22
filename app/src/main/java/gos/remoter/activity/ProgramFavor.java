package gos.remoter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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

import static gos.remoter.define.CommandType.COM_CONNECT_ATTACH;
import static gos.remoter.define.CommandType.COM_CONNECT_DETACH;
import static gos.remoter.define.CommandType.COM_PROGRAM_STB_SWITCH;
import static gos.remoter.define.CommandType.COM_PROGRAM_UPDATE_ALL_LIST;
import static gos.remoter.define.CommandType.COM_SYSTEM_RESPOND;
import static gos.remoter.define.CommandType.COM_SYS_HEARTBEAT_STOP;

public class ProgramFavor extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private  final String TAG = this.getClass().getSimpleName();

    private ArrayList<Program> programFavorList;//列表
    private List<Program> programAll;//数据库
    private Program favorProgram;//被修改的某一行
    private List<Integer> nums;//删除的position集合，被选中

    private TitleBarNew titleBar;
    private TextView programNull;
    private LinearLayout groupLinear;
    private LinearLayout deleteAllFavLin;
    private LinearLayout deleteSingleFavLin;
    private Button deleteAllFav;
    private Button deleteSingle;
    private Button deleteOk;
    private ListView programView;
    private View underline;
    private Program curProgram = null;

    private boolean isDelete = false;
    private boolean isSelect = false;// false:单选；true：全选

    private DBDao db;


    ReuseAdapter<Program> adapter = new ReuseAdapter<Program>(R.layout.item_program_list) {
        @Override
        public void bindView(final ViewHolder holder, Program obj, final int position) {
            if(null != obj) {
                holder.setText(R.id.textNum, (position + 1) +"");
                holder.setText(R.id.textName, obj.getName());
                holder.setVisibility(R.id.textEdit, View.GONE);
                holder.setImageResource(R.id.textDelete, R.drawable.programlist_fav_canceled, R.drawable.programlist_fav_uncaceled, obj.getSelect());
                holder.setOnClickListener(R.id.textDelete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!programFavorList.get(position).getSelect()) {
                            setSelectSinger(position, false, true);//设置被选后的状态和更新数据库
                            holder.setImageResource(R.id.textDelete, R.drawable.programlist_fav_canceled, R.drawable.programlist_fav_uncaceled, true);

                        } else {
                            setSelectSinger(position, true, false);
                            holder.setImageResource(R.id.textDelete, R.drawable.programlist_fav_canceled, R.drawable.programlist_fav_uncaceled, false);

                        }
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
        System.gc();
        ACTCollector.add(this);//添加到收集器

        initView();
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
        ACTCollector.remove(this);//从收集器移除
        nums.clear();

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
            case COM_PROGRAM_UPDATE_ALL_LIST:
                if(null != adapter) {
                    adapter.clear();
                }
                setProgramList();
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
                }
                break;
            default:
                break;
        }

    }

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.program_favor_list);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTextRight(R.string.program_favor_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDelete = true;
                setVisibleLayout();
            }
        });

        groupLinear = (LinearLayout) findViewById(R.id.groupLinear);
        deleteAllFavLin = (LinearLayout) findViewById(R.id.fav_delete_all_lin);
        deleteSingleFavLin = (LinearLayout) findViewById(R.id.fav_delete_single_lin);
        deleteAllFav = (Button) findViewById(R.id.fav_delete_all);
        deleteSingle = (Button) findViewById(R.id.fav_delete_single);
        deleteOk = (Button) findViewById(R.id.fav_delete_ok);

        programNull = (TextView) findViewById(R.id.programList_null);
        programView = (ListView)findViewById(R.id.programView);
        underline = findViewById(R.id.underline);

        groupLinear.setVisibility(View.GONE);
        deleteOk.setVisibility(View.GONE);
        underline.setVisibility(View.GONE);
        programNull.setVisibility(View.GONE);

        deleteAllFavLin.setOnClickListener(this);
        deleteSingleFavLin.setOnClickListener(this);
        deleteOk.setOnClickListener(this);

        programView.setOnItemClickListener(this);
//        programView.setSelector(R.color.deepgray);
        programView.setAdapter(adapter);

    }

    private void initData() {
        nums = new ArrayList<>();
        setProgramList();
        /*if( SystemApplication.getInstance().getState() == SystemState.ATTACH) {
            getProgramList();
        }*/
    }

    private void setVisibleLayout() {
        titleBar.setRightVisible(View.GONE);
        programNull.setVisibility(View.GONE);
        groupLinear.setVisibility(View.VISIBLE);
        deleteOk.setVisibility(View.VISIBLE);

        //显示item中的删除选择按钮
        showDeleteIamge(View.VISIBLE);

    }

    private void showDeleteIamge(int visible) {
        for (int i = 0; i < programFavorList.size(); i++) {
            programView.getChildAt(i).findViewById(R.id.textDelete).setVisibility(visible);
        }

    }

    /**
     * 获取数据库中喜爱的节目，进行列表设置
     *
     */
    private void setProgramList() {
        programFavorList = new ArrayList<>();
        db = new DBDao(this, Program.class);//添加表的类
        programAll = new ArrayList<>();
        programAll = db.qryAllInfo();
//        Log.e(TAG, "数据库全部信息--" + programAll);
        for(int i = 0; i < programAll.size(); i++) {
            if(programAll.get(i).getFavor()) {
                Log.i(TAG,"数据库中的喜爱--" + programAll.get(i));
                programFavorList.add(programAll.get(i));
            }
        }
        if(0 != programFavorList.size()) {
            underline.setVisibility(View.VISIBLE);
            programNull.setVisibility(View.GONE);
        } else {
            underline.setVisibility(View.GONE);
            programNull.setVisibility(View.VISIBLE);
        }
        adapter.reset(programFavorList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新数据库，目前是喜爱
     * @param position
     */
    private void changeFavorStatus(int position) {
        favorProgram = programFavorList.get(position);
        favorProgram.setId(favorProgram.getId());
        db.updateInfo(favorProgram);
        Log.e(TAG, "喜爱状态改变后的数据---" + favorProgram);
        Log.i(TAG, "改变后的数据库---" + db.qryAllInfo());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fav_delete_all_lin:
                if(!isSelect) {
                    AllStatus();
                }

                break;
            case R.id.fav_delete_single_lin:
                if(isSelect) {
                    SingerStatus();
                }

                break;
            case R.id.fav_delete_ok:
                isDelete = false;
                titleBar.setRightVisible(View.VISIBLE);
                groupLinear.setVisibility(View.GONE);
                deleteOk.setVisibility(View.GONE);
                showDeleteIamge(View.GONE);

                upDateData();

                break;
        }

    }

    /**
     *  设置全选状态
     */
    private void AllStatus() {
        deleteAllFav.setBackgroundResource(R.drawable.programlist_fav_canceled);
        deleteSingle.setBackgroundResource(R.drawable.programlist_fav_uncaceled);
        isSelect = true;
        if(programFavorList.size() != 0) {
            setSelectedAll(false, true);
        }
    }

    /**
     * 设置选择状态
     */
    private void SingerStatus() {
        deleteAllFav.setBackgroundResource(R.drawable.programlist_fav_uncaceled);
        deleteSingle.setBackgroundResource(R.drawable.programlist_fav_canceled);
        isSelect = false;
        if(programFavorList.size() != 0) {
            setSelectedAll(true, false);
        }
    }

    /**
     * 只有点击确定时才修改数据
     */
    private void upDateData() {
        Log.e(TAG, "被选中的删除项" + nums);
        if(nums.size() != 0 && nums != null) {
            for (int i = 0; i < nums.size(); i ++) {
                changeFavorStatus(nums.get(i));
            }
            nums.clear();
            setProgramList();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        curProgram = programFavorList.get(position);
        if( SystemApplication.getInstance().getState() == SystemState.ATTACH  ) {
            if( !isDelete) {
                switchProgram(curProgram);
            } else {
                Log.e(TAG, " 点击的item" + curProgram);
                setSelectSinger(position, !(curProgram.getFavor()), !(curProgram.getSelect()));
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.program_list_error_net, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 全选时的数据更新
     * @param favor
     * @param select
     */
    private void setSelectedAll(boolean favor, boolean select) {
        for(int i = 0; i < programFavorList.size(); i ++) {
           setSelectSinger(i, favor, select);

        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新指定位置数据
     * @param position
     * @param favor
     * @param select
     */
    private void setSelectSinger(int position, boolean favor, boolean select) {
        programFavorList.get(position).setFavor(favor);
        programFavorList.get(position).setSelect(select);

        changeNumbers(select, position);
//        changeFavorStatus(position);
    }

    /**
     * 修改List--nums 里的数值
     * @param select
     * @param position
     */
    private void changeNumbers(boolean select, int position) {
        if(select && ! nums.contains(position)) {
            nums.add(position);
//            Log.e(TAG, "----nums---num---" + nums.get(0));
        } else if(!select && nums.contains(position)) {
            for(int i= 0; i < nums.size(); i ++) {
                int num = nums.get(i);
                if(num == position) {
                    nums.remove(i);
                }
            }
        }
      /*  for(int i : nums) {
            Log.e(TAG, "----nums------" + i);
        }*/

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
