package gos.remoter.data;

import java.util.ArrayList;

/**
 * epg可折叠列表
 * Created by wuxy on 2017/9/11.
 */

public class ExpandableTime {
    private boolean expand; //是否折叠
    private Time time;

    public ExpandableTime(boolean expand, Time time) {
        this.expand = expand;
        this.time = time;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


    /**
     * 将ArrayList<Time> 转化成 ArrayList<ExpandableTime>
     * @param timeArrayList
     * @return
     */
    public static ArrayList<ExpandableTime> toExpandableTime(ArrayList<Time> timeArrayList){
        ArrayList<ExpandableTime> expandableTimes = new ArrayList<>();
        for (Time t :
                timeArrayList) {
            expandableTimes.add(new ExpandableTime(false,t));
        }
        return expandableTimes;
    }
}
