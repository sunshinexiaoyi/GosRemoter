package gos.remoter.data;

import java.util.ArrayList;

/**
 * Created by wuxy on 2017/8/15.
 */

public class Date {
    private String date;
    private ArrayList<Time> timeArray = new ArrayList<>();

    public Date() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Time> getTimeArray() {
        return timeArray;
    }

    public void setTimeArray(ArrayList<Time> timeArray) {
        this.timeArray = timeArray;
    }
}
