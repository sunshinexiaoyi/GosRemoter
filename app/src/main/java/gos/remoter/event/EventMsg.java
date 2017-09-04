package gos.remoter.event;

/**
 * EventBus 消息类
 * Created by wuxy on 2017/7/7.
 */

public class EventMsg {
    private byte command;
    private EventMode eventMode;
    private String data;

    public EventMsg(){}

    public EventMsg(byte command,String data,EventMode eventMode){
        this.data = data;
        this.command = command;
        this.eventMode = eventMode;
    }

    public byte getCommand(){
        return command;
    }

    public void setCommand(byte command){
        this.command = command;
    }


    public void setData(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }

    public void setEventMode(EventMode eventMode){
        this.eventMode = eventMode;
    }

    public EventMode getEventMode(){
        return eventMode;
    }
}
