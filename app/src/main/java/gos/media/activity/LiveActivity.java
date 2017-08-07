package gos.media.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.player.widget.PlayStateParams;
import com.player.widget.PlayerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import gos.media.R;
import gos.media.define.CommandType;
import gos.media.event.EventManager;
import gos.media.event.EventMode;
import gos.media.event.EventMsg;
import gos.media.event.MsgKey;

public class LiveActivity extends Activity {
    private PlayerView mVideoView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg) {
        if (EventMode.IN == msg.getEventMode()) {//对内
            switch (msg.getCommand()) {
                case CommandType.COM_SYS_FINISH_LIVE:
                    delayFinish(3000, getResources().getString(R.string.switch_freq));
                    break;
                case CommandType.COM_SYS_HEARTBEAT_STOP:
                    delayFinish(0, getResources().getString(R.string.heartbeat_stop));
                    break;
                default:
                    String s = new String("hh");
                    s.length();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_player);
        initPlayerView();
        startPlay();

        EventManager.register(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        EventManager.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mVideoView != null && mVideoView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVideoView.onConfigurationChanged(newConfig);
    }

    /**
     * 播放视频
     *
     * @param s 视频流地址
     */
    private void playVideo(String s) {
        if (s != null) {
            mVideoView.setLivePlay(true);
            mVideoView.setPlaySource(s);
            mVideoView.startPlay();
        }
    }

    /**
     * 初始化播放器布局
     */
    protected void initPlayerView() {
        mVideoView = new PlayerView(this);
        mVideoView.setScaleType(PlayStateParams.fitparent);
        mVideoView.forbidTouch(false);
        mVideoView.hideMenu(true);
        mVideoView.setOnlyFullScreen(true);
    }

    private void startPlay(){
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString(MsgKey.url);
        playVideo(url);
    }

    /**
     * @param delay 延时多少毫秒结束
     * @param info  打印提示信息
     */
    private void delayFinish(long delay,String info ){
        if(delay <0){
            Log.e("delayFinish","delay <0");
            return;
        }
        Log.e("delayFinish","delay "+delay);
        if(null != info){
            Toast.makeText(this,info, Toast.LENGTH_SHORT).show();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("delayFinish","finish");
                finish();
            }
        },delay);
    }

}
