package gos.remoter.define;

import android.util.Log;

import java.util.Arrays;

import gos.remoter.exception.DataPackageException;

/**
 * 完整数据包
 * Created by wuxy on 2017/7/6.
 */

public class DataPackage extends PackageHead{
    final String TAG = this.getClass().getSimpleName();

    public static final int headLen = 11;   //头部长度
    public String data = null;//数据段

    public int testCrc = 2017;//默认crc值

    public DataPackage(){}

    public DataPackage(byte[] inputData)throws DataPackageException{
        toDataPackage(inputData);
    }

    public DataPackage(byte command, String data){
        setCommand(command);
        setData(data);
    }

    /**
     * 设置长度
     * @param data
     */
    public void setData(String data){
        if(null != data){
            this.data = data;
            setDataLen(data.getBytes().length);
        }
    }

    /**
     * 不设置长度
     * @param data
     */
    public void setData(byte[] data) {
        this.data = new String(data);
    }

    public String getData(){
        return data;
    }

    @Override
    public String toString() {
        return    "命令:"+ getCommand()
                 +"CRC:"+ getCrc()
                 +"数据长度:"+getDataLen()
                 +"数据:"+getData();

    }
    
    public void toDataPackage(byte[] data) throws DataPackageException{
        if(data.length >= headLen){
            int p = 0;

            setCommand(data[p++]);
            setReceive(data[p++]);
            setCrc(byteToInt(data,p,4));
            p +=4;

            if(!crcCheckout(getCrc())){
                throw new DataPackageException("crc校验出错!"+"当前crc:"+getCrc());
            }

            setEncrypt(data[p++]);
            setDataLen(byteToInt(data,p,4));

            setData(Arrays.copyOfRange(data,headLen,data.length));

        }else {
            throw new DataPackageException("数据长度小于"+headLen+"!当前数据长度:"+data.length);
        }
    }

    public static String getFormatStr(byte[] byteData){
        if(null == byteData){
            Log.e("getFormatStr","null == byteData");
            return null;
        }

        StringBuilder str = new StringBuilder();
        for(int i =0;i<byteData.length;i++){
            str.append(byteData[i]);
            str.append(" ");
        }
        return str.toString();
    }

    /**
     * 将对象转化成byte[]
     * @return  byte[]
     */
    public byte[] toByte(){
        byte[] sendData = new byte[headLen+getDataLen()];
        int p = 0;

        sendData[p++] = getCommand();
        sendData[p++] = getReceive();

        p += intToByte(sendData,p,4,testCrc);
        sendData[p++] = getEncrypt();

        p += intToByte(sendData,p,4,getDataLen());

        if (null != getData()) {
            byte[] data = getData().getBytes();
            System.arraycopy(data,0, sendData,p,data.length);
        }

        return sendData;
    }

    /**
     * 将byte数组转化成int类型 小端模式 低地址低位
     * @param srcBytes  输入数组
     * @param from  开始索引 包括该适索引
     * @param len   长度  <=4
     * @return  -1 失败 否则成功
     */
    private int byteToInt(byte[] srcBytes,int from,int len){
        int num = 0;
        if(4 < len){
            return -1;
        }

        for(int i=0;i<len;i++){

            num |= (srcBytes[from+i]&0xff)<<(Byte.SIZE*i);
        }

        return num;
    }

    /**
     * 将int转为byte数组
     * @param desBytes  目的数组
     * @param from  开始索引 包括该适索引
     * @param len   长度 小于4
     * @param srcInt    源int
     * @return  数组索引偏移长度
     */
    private int intToByte(byte[] desBytes,int from,int len,int srcInt){
        int i = 0;
        for(;i<len;i++){
            desBytes[from+i] = (byte)((srcInt>>(Byte.SIZE*i))&0xff);
        }
        return i;
    }

    /**
     * crc校验
     * @param crc 输入crc
     * @return  true校验成功，false校验失败
     */
    private boolean crcCheckout(int crc){
        //Log.i("data","crcCheckout:"+crc);

       return (crc == testCrc);
    }


}

/**
 * 数据头部
 */
class PackageHead{
    private byte command = 0;//命令类型  1byte
    private byte receive = 0;//预留        1byte
    private int crc = 2017;  //crc校验值    2byte
    private byte encrypt = 0; //data加密类型0:不加密  1:rsa加密  2:aes加密  1byte
    private int dataLen = 0; //数据长度  4byte

    public byte getCommand(){
        return command;
    }
    public void setCommand(byte command){
        this.command = command;
    }

    public byte getReceive(){
        return receive;
    }
    public void setReceive(byte receive){
        this.receive = receive;
    }

    public int getCrc(){
        return crc;
    }
    public void setCrc(int crc){
        this.crc = crc;
    }

    public int getDataLen(){
        return dataLen;
    }
    public void setDataLen(int dataLen){
        this.dataLen = dataLen;
    }

    public byte getEncrypt(){
        return encrypt;
    }
    public void setEncrypt(byte encrypt){
        this.encrypt = encrypt;
    }

}
