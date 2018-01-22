package gos.remoter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.define.CommandType;
import gos.remoter.event.EventManager;
import gos.remoter.event.EventMode;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.tool.SharedPreferencesUtils;
import gos.remoter.view.TitleBarNew;

public class SettingLanguage extends Activity {
    private  final String TAG = this.getClass().getSimpleName();
    public final static String SETTING_LANGUAGE_SELECTED = "language";
    private final static String SETTING_LANGUAGE_SELECTED_POSITION = "language_position";

    private ListView languageList;
    private ArrayList<String> lanLists;//{"跟随系统", "简体中文", "English"}
    private int curPosition;
    private String curLanguage;

    ReuseAdapter<String> lanAdapter = new ReuseAdapter<String>(R.layout.item_set_language) {

        @Override
        public void bindView(ViewHolder holder, String obj, int position) {
            holder.setText(R.id.item_language, obj);
            holder.setImageResource(R.id.languageEdit, R.drawable.programlist_fav_canceled, R.drawable.programlist_fav_uncaceled);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_language);

//        EventManager.register(this);
        ACTCollector.add(this);
        initView();
        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ACTCollector.remove(this);
/*        EventManager.removeSticky(this);
        EventManager.unregister(this);*/

    }
/*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg) {
        if(msg.getEventMode() == EventMode.IN) {
            switch (msg.getCommand()) {
                case CommandType.COM_SET_SYSTEM_LANGUAGE:

        LanguageUtil.resetDefaultLanguage();
        Log.e(TAG, "configuration--" + LanguageUtil.getSetLocale().getDisplayName());

        recreate();//刷新界面

                    break;

            }
        }
    }*/

    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setTextTitle(R.string.selectLanguage);
        titleBar.setImageLeft(R.drawable.activity_return, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTextRight(R.string.saveLanguage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存状态
                saveLanguage();
            }

        });

        languageList = (ListView) findViewById(R.id.set_language);
        languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position;
                curLanguage = lanLists.get(curPosition);
                lanAdapter.setSelectedId(curPosition);
                //刷新数据
                lanAdapter.notifyDataSetInvalidated();

            }
        });
        languageList.setAdapter(lanAdapter);

    }

    private void initData() {
        lanLists = new ArrayList<>();
        lanLists.add(getResources().getString(R.string.lanSystem));
        lanLists.add(getResources().getString(R.string.lanChinse));
        lanLists.add(getResources().getString(R.string.lanEnglish));
        lanAdapter.reset(lanLists);

        curPosition = SharedPreferencesUtils.getInt(SETTING_LANGUAGE_SELECTED_POSITION, 0);
        curLanguage = SharedPreferencesUtils.get(SETTING_LANGUAGE_SELECTED, getString(R.string.lanSystem));
        Log.e(TAG, "存储的位置和语言" + curPosition + curLanguage);
        lanAdapter.setSelectedId(curPosition);

    }

    private void saveLanguage() {
        SharedPreferencesUtils.save(SETTING_LANGUAGE_SELECTED, curLanguage);
        SharedPreferencesUtils.save(SETTING_LANGUAGE_SELECTED_POSITION, curPosition);
        //通知其他Activity选择的语言
        changeOther();

//        startJump();
        finish();
    }

    private void changeOther() {
        EventManager.send(CommandType.COM_SET_SYSTEM_LANGUAGE, curLanguage, EventMode.IN);

    }

    private void startJump() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


}
