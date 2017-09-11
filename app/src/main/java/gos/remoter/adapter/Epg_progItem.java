package gos.remoter.adapter;

import gos.remoter.R;
import gos.remoter.activity.EPGActivity;

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
    private int simpleRecBtnOnce;//缩略图预定记录
    private int simpleRecBtnCycle;//缩略图预定记录
    private int simpleWatchBtnOnce;//缩略图预定记录
    private int simpleWatchBtnCycle;//缩略图预定记录
    private int recBtnOnce;//预定记录一次
    private int recBtnCycle;//预定记录每天
    private int watchBtnOnce;//预定观看一次
    private int watchBtnCycle;//预定观看每天

    public Epg_progItem(String progName, String progTime, String progInfo,
                   int simpleRecBtnOnce, int simpleRecBtnCycle,
                   int simpleWatchBtnOnce, int simpleWatchBtnCycle,
                   int recBtnOnce, int recBtnCycle,
                   int watchBtnOnce, int watchBtnCycle) {
        this.progName = progName;
        this.progTime = progTime;
        this.progInfo = progInfo;
        this.simpleRecBtnOnce = simpleRecBtnOnce;
        this.simpleRecBtnCycle = simpleRecBtnCycle;
        this.simpleWatchBtnOnce = simpleWatchBtnOnce;
        this.simpleWatchBtnOnce = simpleWatchBtnOnce;
        this.simpleWatchBtnCycle = simpleWatchBtnCycle;
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
    public int getSimpleRecBtnOnce() {
        return simpleRecBtnOnce;
    }
    public int getSimpleRecBtnCycle() {
        return simpleRecBtnCycle;
    }
    public int getSimpleWatchBtnOnce() {
        return simpleWatchBtnOnce;
    }
    public int getSimpleWatchBtnCycle() {
        return simpleWatchBtnCycle;
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
    public void setSimpleRecBtnOnce(int simpleRecBtnOnce) {
        this.simpleRecBtnOnce = simpleRecBtnOnce;
    }
    public void setSimpleRecBtnCycle(int simpleRecBtnCycle) {
        this.simpleRecBtnCycle = simpleRecBtnCycle;
    }
    public void setSimpleWatchBtnOnce(int simpleWatchBtnOnce) {
        this.simpleWatchBtnOnce = simpleWatchBtnOnce;
    }
    public void setSimpleWatchBtnCycle(int simpleWatchBtnCycle) {
        this.simpleWatchBtnCycle = simpleWatchBtnCycle;
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
