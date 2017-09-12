package gos.remoter.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import gos.remoter.R;
import gos.remoter.adapter.LiverClassifyListAdapter;
import gos.remoter.data.Program;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.fragment.LiverFragment;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class LiverActivity extends FragmentActivity implements AdapterView.OnItemClickListener{
    private  final String TAG = this.getClass().getSimpleName();

    private ListView mListView;
    private String[] programType;
    private LiverClassifyListAdapter mListAdapter;
    public static int mPosition;
    ArrayList<Program> programs;

    //Fragment资源
    private LiverFragment liveFragment;
    private int[] image;
    private String[] program;

    //节目分类下的节目
    private List<String> list0 = new ArrayList<>();
    private List<String> list1 = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();
    private List<String> list3 = new ArrayList<>();
    private List<String> list4 = new ArrayList<>();
    private List<String> list5 = new ArrayList<>();
    private List<String> list6 = new ArrayList<>();
    private List<String> list7 = new ArrayList<>();
    private List<String> list8 = new ArrayList<>();
    private List<String> list9 = new ArrayList<>();

    private List<List<String>> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liver);
        EventManager.register(this);
        initView();
    }

    void initView(){
        initData();
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.live_title);

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //拿到当前位置
        mPosition = position;
        Log.e("onItemClick", "点击第: " + mPosition + " 项");

        //即时刷新adapter
        mListAdapter.notifyDataSetChanged();

        for (int a = 0; a < programType.length; a++) {
            liveFragment = new LiverFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.liver_fragment, liveFragment);
            Bundle bundle = new Bundle();
            bundle.putInt("image", image[mPosition]); //需要创建一个图片数组
            bundle.putString("text", programType[mPosition]); //xml存在一个分类数组

            program = new String[lists.get(mPosition).size()];
            for (int i = 0; i < lists.get(mPosition).size(); i++) {
                program[i] = lists.get(mPosition).get(i);
            }

            bundle.putStringArray("program", program); //得到的数据
            liveFragment.setArguments(bundle);
            fragmentTransaction.commit();
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

        switch (msg.getCommand()){
            case COM_LIVE_SET_PROGRAM_LIST:
                ArrayList<Program> programList = DataParse.getProgramList(msg.getData());
                Log.i(TAG,msg.getData());
                classify(programList);
                initFragment();
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

    public void initFragment() {
        //创建MyFragment对象
        liveFragment = new LiverFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.liver_fragment, liveFragment);

        //通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
        bundle.putInt("image", image[mPosition]); //图片数组
        bundle.putString("text", programType[mPosition]); //分类节目名数组

        program = new String[lists.get(mPosition).size()]; //分类后的节目名
        for (int i = 0; i < lists.get(mPosition).size(); i++) {
            program[i] = lists.get(mPosition).get(i);
        }

        bundle.putStringArray("program", program);
        liveFragment.setArguments(bundle);
        fragmentTransaction.commit();
    }


    //节目分类
    public void classify( ArrayList<Program> programs) {

        for (Program p : programs) {
            if(0 == p.getType()) {
                list0.add(p.getName());
            } else if (1 == p.getType()) {
                list1.add(p.getName());
            } else if (2 == p.getType()) {
                list2.add(p.getName());
            } else if (3 == p.getType()) {
                list3.add(p.getName());
            } else if (4 == p.getType()) {
                list4.add(p.getName());
            } else if (5 == p.getType()) {
                list5.add(p.getName());
            } else if (6 == p.getType()) {
                list6.add(p.getName());
            } else if (7 == p.getType()) {
                list7.add(p.getName());
            } else if (8 == p.getType()) {
                list8.add(p.getName());
            } else if (9 == p.getType()) {
                list9.add(p.getName());
            }
        }

        lists.add(list0);
        lists.add(list1);
        lists.add(list2);
        lists.add(list3);
        lists.add(list4);
        lists.add(list5);
        lists.add(list6);
        lists.add(list7);
        lists.add(list8);
        lists.add(list9);
    }

    private void initData() {
        getProgramList();
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
