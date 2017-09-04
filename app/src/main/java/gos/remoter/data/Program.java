package gos.remoter.data;

/**
 * Created by wuxy on 2017/7/7.
 */

public class Program extends IndexClass{
    private String name;
    private int lcn;
    private int serviceId;

    public Program(){
        this("",-1);
    }
    public Program(String name,int index){
        this(name,index,-1,-1);
    }
    public Program(String name,int index,int lcn,int serviceId){
        super(index);
        this.name = name;
        this.serviceId = serviceId;
        this.lcn = lcn;
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
}
