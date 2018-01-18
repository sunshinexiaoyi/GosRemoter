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
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

public class SettingLanguage extends Activity {
    private  final String TAG = this.getClass().getSimpleName();
    private final static int SETTING_SELECTED_LANGUAGE_CONTENT = 2;

    private ListView languageList;
    private View setUnderline;
    private ArrayList<String> lanLists;//{"跟随系统", "简体中文", "English"}
    private int curPosition = 0;
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

        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

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
        titleBar.setImageRight(R.drawable.new_live_list, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回传数据,保存状态
                sendData();
            }

        });

        languageList = (ListView) findViewById(R.id.set_language);
        setUnderline = findViewById(R.id.underline);

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
        languageList.setSelector(R.color.deepgray);
        languageList.setAdapter(lanAdapter);
        lanAdapter.setSelectedId(curPosition);

        lanLists = new ArrayList<>();
        lanLists.add(getResources().getString(R.string.lanSystem));
        lanLists.add(getResources().getString(R.string.lanChinse));
        lanLists.add(getResources().getString(R.string.lanEnglish));
        lanAdapter.reset(lanLists);

        curLanguage = lanLists.get(curPosition);

    }

    //用SharedPreference存储语言，


    private void sendData() {
        Log.e(TAG,"intent----intent");
        Intent intent = new Intent();
        intent.putExtra("language", curLanguage);
        setResult(SETTING_SELECTED_LANGUAGE_CONTENT, intent);
        finish();
    }


}
