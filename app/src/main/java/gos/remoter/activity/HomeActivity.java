package gos.remoter.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import gos.remoter.R;
import gos.remoter.adapter.ReuseAdapter;
import gos.remoter.data.GridActivity;
import gos.remoter.tool.ImmersionLayout;
import gos.remoter.view.TitleBarNew;

public class HomeActivity extends Activity {
    AlertDialog logoutAlert;
    long firstTime;//保存第一次按退出键的时间

    ReuseAdapter<GridActivity> gridAdapter = new ReuseAdapter<GridActivity>(R.layout.item_grid_icon) {
        @Override
        public void bindView(ViewHolder holder, GridActivity obj) {

            holder.setText(R.id.txt_icon,getResources().getString(obj.getName()) );
            holder.setImageResource(R.id.img_icon,obj.getIcon());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if(System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, R.string.exit2, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true; //不返回，一次就立马退出，
        }
        return super.onKeyDown(keyCode, event);
    }


    void initView(){
        new ImmersionLayout(this).setImmersion();

        /*标题栏*/
        TitleBarNew titleBar = (TitleBarNew)findViewById(R.id.titleBar);
        titleBar.setNullBackground();

        titleBar.setTextTitle(R.string.connect_title);
        titleBar.setImageRight(R.drawable.home_logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//退出连接
                //Toast.makeText(HomeActivity.this, "logout", Toast.LENGTH_SHORT).show();
                logoutAlert();
            }
        });

        gridAdapter.add(new GridActivity(RemoterActivity.class,R.drawable.home_micontro,R.string.home_remoter));
        gridAdapter.add(new GridActivity(ProgramActivity.class,R.drawable.home_programlist,R.string.home_program_list));
        gridAdapter.add(new GridActivity(EpgActivity.class,R.drawable.home_epg,R.string.home_epg));
        gridAdapter.add(new GridActivity(LiverActivity.class,R.drawable.home_live,R.string.home_live));
        gridAdapter.add(new GridActivity(null,R.drawable.home_more,R.string.home_more));


        GridView gridView = (GridView)findViewById(R.id.gridActivity);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridActivity selectActivity = gridAdapter.getItem(position);
                if(selectActivity.getActivity()==null){
                    String info =  getResources().getString(R.string.home_info);
                    Toast.makeText(HomeActivity.this, info, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(HomeActivity.this,selectActivity.getActivity());
                startActivity(intent);
            }
        } );

    }

    void logout(){
        Intent intent = new Intent(this,ConnectActivity.class);
        startActivity(intent);
        finish();
    }

    void initLogoutAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        logoutAlert = builder.setIcon(R.drawable.home_logout_black)
                .setTitle(R.string.homeTitle)
                .setMessage(R.string.home_alert_msg)
                .setNegativeButton(R.string.home_alert_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutAlert.dismiss();
                    }
                })
                .setPositiveButton(R.string.home_alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                }).create();
    }

    void logoutAlert(){
        if(null == logoutAlert){
            initLogoutAlert();
        }

        logoutAlert.show();
    }
}
