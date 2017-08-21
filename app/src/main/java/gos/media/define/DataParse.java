package gos.media.define;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import gos.media.data.Device;
import gos.media.data.EpgProgram;
import gos.media.data.IndexClass;
import gos.media.data.Program;
import gos.media.data.ReserveEventSend;
import gos.media.data.Respond;

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
