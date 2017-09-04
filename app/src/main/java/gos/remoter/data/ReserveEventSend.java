package gos.remoter.data;

/**
 * Created by wuxy on 2017/8/16.
 */

public class ReserveEventSend {

    private String eventType;
    private  int index;
    private String eventId;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
