package gos.remoter.data;

import java.io.Serializable;

/**
 * Created by wuxy on 2017/7/7.
 */

public class Program extends IndexClass implements Serializable{
    private String name;
    private int lcn;
    private int serviceId;
    private int type;   //节目类型

    public Program(){
        this("",-1);
    }
    public Program(String name,int index){
        this(name,index,-1,-1);
    }
    public Program(String name,int index,int lcn,int serviceId){
        this(name,index,lcn,serviceId,0);
    }
    public Program(String name,int index, int lcn, int serviceId, int type) {
        super(index);
        this.name = name;
        this.lcn = lcn;
        this.serviceId = serviceId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLcn() {
        return lcn;
    }

    public void setLcn(int lcn) {
        this.lcn = lcn;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
