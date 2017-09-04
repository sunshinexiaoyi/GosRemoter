package gos.remoter.define;

import android.util.Log;

import java.util.Arrays;

/**
 * 将字节数据转化成数据包
 * Created by wuxy on 2017/7/6.
 */

public class DataPackage extends PackageHead{

    public String data = null;
    public int testCrc = 2017;

    public  byte[] byteData =  new byte[1024*10];

    public DataPackage(){}

    public DataPackage(byte[] inputData)throws Exception{
        toDataPackage(inputData);
    }

    @Override
    public String toString() {
        try {
            return new String(toByte());
        }catch (Exception e){
            e.printStackTrace();}
        return null;
    }

    /**
     * @return  将数据包转化成byte[]
     * @throws Exception
     */
    public byte[] toByte()throws Exception{

        int i = 0;
        //command
        byteData[i++] = command;
        byteData[i++] = receive;

        //crc
        i += intToByte(crc,byteData,i);

        //encrypt
        byteData[i++] = encrypt;

        //datalen
        i += intToByte(dataLen,byteData,i);

        //data
        if(null !=data){
            byte[] byteSrc = data.getBytes("utf-8");
            System.arraycopy(byteSrc,0,byteData,i,byteSrc.length);
            i += byteSrc.length;

        }
        byteData = Arrays.copyOf(byteData,i);

        return byteData;
    }

    /**
     * 将输入的byte数组转化成数据包
     * @param srcByte 输入byte数组
     */
    public void toDataPackage(byte[] srcByte) throws Exception{
        int i = 0;
        this.command = srcByte[i++];
        this.receive = srcByte[i++];
        try {
            this.crc = byteToInt(srcByte,i);
            i += 4;
            if(!crcCheckout(this.crc)) {
                throw new Exception("crcCheckout is faield");
            }
            this.encrypt = srcByte[i++];

            this.dataLen = byteToInt(srcByte,i);
            i += 4;

            this.data = new String(Arrays.copyOfRange(srcByte,i,srcByte.length));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("toDataPackage is faield");

        }
    }

    public void setData(String data){
        if(null != data){
            this.data = data;
            this.dataLen = data.length();
        }
    }

    public String getData(){
        return data;
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
     * @param srcByte   源数组
     * @return  返回转化的int
     * @throws Exception
     */
    private int byteToInt(byte[] srcByte,int fromIndex) throws Exception{
        if(srcByte.length < 4){
            throw new Exception("byte[].length < int length");
        }
        return  (srcByte[fromIndex++]&0xff)|
                ((srcByte[fromIndex++]&0xff)<<8)|
                ((srcByte[fromIndex++]&0xff)<<16)|
                ((srcByte[fromIndex++]&0xff)<<24);
    }

    /**
     * @param srcInt    要转化的int型
     * @param desByte   目标数组
     * @param fromIndex 从指定索引开始(包含该索引位置)
     * @throws Exception
     * @return  索引偏移数
     */
    private int intToByte(int srcInt,byte[] desByte,int fromIndex) throws Exception{
        if(desByte.length < fromIndex + 4){
            throw new Exception("desByte.length < fromIndex + 4");
        }

        desByte[fromIndex++] = (byte)(srcInt&0xff);
        desByte[fromIndex++] = (byte)((srcInt>>8)&0xff);
        desByte[fromIndex++] = (byte)((srcInt>>16)&0xff);
        desByte[fromIndex++] = (byte)((srcInt>>24)&0xff);

        return 4;
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

class PackageHead{
    public byte command = 0;//命令类型
    public byte receive = 0;//预留
    public int crc = 2017;  //crc校验值
    public byte encrypt = 0; //data加密类型0:不加密  1:rsa加密  2:aes加密
    public int dataLen = 0; //数据长度

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
