package gos.remoter.data;

/**
 * Created by wuxy on 2017/7/7.
 */

public class Device {
    private String ip;
    private String mac;
    private String id;

    public Device(){}

    public Device(String ip, String mac, String id){
        this.ip = ip;
        this.mac = mac;
        this.id = id;
    }

    public String getIp(){
        return ip;
    }
    public void setIp(String ip){
        this.ip = ip;
    }

    public String getMac(){
        return mac;
    }
    public void setMac(String mac){
        this.mac = mac;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }



}
