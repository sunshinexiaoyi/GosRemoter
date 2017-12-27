package gos.remoter.define;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import gos.remoter.data.Advertisement;
import gos.remoter.data.Device;
import gos.remoter.data.EpgProgram;
import gos.remoter.data.IndexClass;
import gos.remoter.data.Program;
import gos.remoter.data.ReserveEventSend;
import gos.remoter.data.Respond;

/**
 * Created by wuxy on 2017/7/7.
 */

public class DataParse {
    public static Respond getRespond(String data){
        return JSON.parseObject(data,Respond.class);
    }
    public static Program getProgram(String data){
        return JSON.parseObject(data,Program.class);
    }
    public static ArrayList<Program> getProgramList(String data){
        return (ArrayList<Program>)JSON.parseArray(data,Program.class);
    }

    public static ArrayList<Advertisement> getAdList(String data){
        return (ArrayList<Advertisement>)JSON.parseArray(data,Advertisement.class);
    }
    public static Device getDevice(String data){
        return JSON.parseObject(data,Device.class);
    }
    public static IndexClass getIndexClass(String data){
        return JSON.parseObject(data,IndexClass.class);
    }

    public static EpgProgram getEpgProgram(String data){
        return JSON.parseObject(data,EpgProgram.class);
    }

    public static ReserveEventSend getReserveEventSend(String data){
        return JSON.parseObject(data,ReserveEventSend.class);
    }


}
