package gos.remoter.adapter;

import gos.remoter.R;
import gos.remoter.activity.EpgActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by QXTX-GOSPELL on 2017/9/5 0005.
 */

public class Epg_progItem {
    private String progName;//节目名
    private String progTime;//节目播放时间
    private String progInfo;//节目信息
    private int simpleBtn;//希望只有一个简单按钮
    private int recBtnOnce;//预定记录一次
    private int recBtnCycle;//预定记录每天
    private int watchBtnOnce;//预定观看一次
    private int watchBtnCycle;//预定观看每天

    //希望只有一个简单按钮
    public Epg_progItem(String progName, String progTime, String progInfo, int simpleBtn,
                        int recBtnOnce, int recBtnCycle,
                        int watchBtnOnce, int watchBtnCycle) {
        this.progName = progName;
        this.progTime = progTime;
        this.progInfo = progInfo;
        this.simpleBtn = simpleBtn;
        this.recBtnOnce = recBtnOnce;
        this.recBtnCycle = recBtnCycle;
        this.watchBtnOnce = watchBtnOnce;
        this.watchBtnCycle = watchBtnCycle;

    }

    public String  getProgName() {
        return progName;
    }
    public String getPorgTime() {
        return progTime;
    }
    public String getProgInfo() {
        return progInfo;
    }
    public int getSimpleBtn() {
        return simpleBtn;
    }

    public int getRecBtnOnce() {
        return recBtnOnce;
    }
    public int getRecBtnCycle() {
        return recBtnCycle;
    }
    public int getWatchBtnOnce() {
        return watchBtnOnce;
    }
    public int getWatchBtnCycle() {
        return watchBtnCycle;
    }

    public void setProgName(String progName) {
        this.progName = progName;
    }
    public void setTime(String progTime) {
        this.progTime = progTime;
    }
    public void setProgInfo(String progInfo) {
        this.progInfo = progInfo;
    }

    public void setSimpleBtn(int simpleBtn) {
        this.simpleBtn = simpleBtn;
    }
    public void setRecBtnOnce(int recBtnOnce) {
        this.recBtnOnce = recBtnOnce;
    }
    public void setRecBtnCycle(int recBtnCycle) {
        this.recBtnCycle = recBtnCycle;
    }
    public void setWatchBtnOnce(int watchBtnOnce) {
        this.watchBtnOnce = watchBtnOnce;
    }
    public void setWatchBtnCycle(int watchBtnCycle) {
        this.watchBtnCycle = watchBtnCycle;
    }
}
