package gos.remoter.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gos.remoter.R;
import gos.remoter.adapter.LiverClassifyListAdapter;
import gos.remoter.data.Program;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.fragment.LiveFragment;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class LiveActivity extends FragmentActivity implements AdapterView.OnItemClickListener{
    private  final String TAG = this.getClass().getSimpleName();

    private ListView mListView;
    private String[] programType;
    private LiverClassifyListAdapter mListAdapter;
    public static int selectPosition = 0;//选择的节目类型

    //Fragment资源
    private LiveFragment liveFragment;
    FragmentTransaction fragmentTransaction;
    private int[] image;

    private HashMap<Integer,ArrayList<Program>> typeList = new HashMap<>();//保存分类的节目列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liver);
        EventManager.register(this);
        initView();
        initData();
    }

    void initView(){

        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.live_title);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //分类列表
        mListView = (ListView) findViewById(R.id.live_classify_list);
        programType = getResources().getStringArray(R.array.program_type);
        mListAdapter = new LiverClassifyListAdapter(this, programType);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);

        //图片资源
        image = new int[]{R.drawable.liver_tele_image, R.drawable.liver_movies_image, R.drawable.liver_variety_image,
                R.drawable.liver_children_image, R.drawable.liver_news_image, R.drawable.liver_movies_image,
                R.drawable.liver_movies_image, R.drawable.liver_movies_image, R.drawable.liver_movies_image, R.drawable.liver_movies_image};
    }

    private void initData() {
        initFragment();
        getProgramList();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //拿到当前位置
        selectPosition = position;
        Log.e("onItemClick", "点击第: " + selectPosition + " 项");

        //即时刷新adapter
        mListAdapter.notifyDataSetChanged();
        updateFragment(position);
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

        switch (msg.getCommand()){
            case COM_LIVE_SET_PROGRAM_LIST:
                ArrayList<Program> programList = DataParse.getProgramList(msg.getData());
                Log.i(TAG,msg.getData());
                classify(programList);
                updateFragment(selectPosition);//默认设置选择的节目类型
                break;

            case COM_SYSTEM_RESPOND:    //回应
                Respond respond = DataParse.getRespond(msg.getData());
                switch (respond.getCommand()){
                    case COM_CONNECT_DETACH:
                        if(respond.getFlag()){
                            //  detach();
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

    /**
     * 初始化节目列表的fragment
     */
    public void initFragment() {
        //创建MyFragment对象
        liveFragment = new LiveFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.liver_fragment, liveFragment);
        fragmentTransaction.commit();
    }

    /**
     * 将指定类型的节目更新到列表
     * @param type  指定类型
     */
    public void updateFragment(int type){
        if(!typeList.containsKey(type)){//类型节目不存在
            String errorInfo = getResources().getString(R.string.live_not_found_program);
            Toast.makeText(this, errorInfo, Toast.LENGTH_SHORT).show();
            liveFragment.updateUi(programType[type], image[type],new ArrayList<Program>());//发送空节目数据

            return;
        }

        liveFragment.updateUi(programType[type], image[type],typeList.get(type));
    }


    //节目分类
    public void classify( ArrayList<Program> programs) {
        for(Program p:programs){
            Integer type = p.getType();

            if(!typeList.containsKey(type)){ //判断是否存在该类型的key
                typeList.put(type,new ArrayList<Program>());    //添加该类型
            }
            typeList.get(type).add(p);  //添加该类型节目
        }
    }


    /**
     * 获取节目列表
     */
    private void getProgramList(){
        Log.i(TAG,"获取节目列表:");
        //errorMaskView.setLoadingStatus();
        EventManager.send(COM_LIVE_GET_PROGRAM_LIST,"", EventMode.OUT);
    }
}
