package gos.remoter.data;

import com.alibaba.fastjson.JSON;

/**
 * Created by wuxy on 2017/8/15.
 */

public class Time {
    private String startTime;
    private String endTime;
    private String event;
    private String shortDes;
    private String longDes;

    private String eventType;//（0：delete， 1：View， 2：View Series， 3： Record，4： Record Series）
    private String eventID;

    public Time() {
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getShortDes() {
        return shortDes;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getLongDes() {
        return longDes;
    }

    public void setLongDes(String longDes) {
        this.longDes = longDes;
    }

    public Time clone()  {
        return JSON.parseObject(JSON.toJSONString(this),getClass());
    }
}
