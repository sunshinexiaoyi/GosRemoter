package gos.remoter.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.GridActivity;
import gos.remoter.data.Program;
import gos.remoter.data.Respond;
import gos.remoter.define.DataParse;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.event.EventMsg;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

import static gos.remoter.define.CommandType.*;

public class ProgramActivity extends Activity {
    private  final String TAG = this.getClass().getSimpleName();

    ReuseAdapter<Program> adapter = new ReuseAdapter<Program>(R.layout.item_program_list) {
        @Override
        public void bindView(ViewHolder holder, Program obj) {

            holder.setText(R.id.textLcn,obj.getLcn()+"");
            holder.setText(R.id.textName,obj.getName());
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
                adapter.reset(programList);
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

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);


        titleBar.setTextTitle(R.string.program_title);
        titleBar.setImageRight(R.drawable.program_list_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//更多
                Toast.makeText(ProgramActivity.this, "更多", Toast.LENGTH_SHORT).show();

            }
        });

        ListView programView = (ListView)findViewById(R.id.programView);
        programView.setAdapter(adapter);
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
