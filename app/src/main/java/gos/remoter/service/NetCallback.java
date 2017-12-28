package gos.remoter.service;

/**
 * 网络回调
 * Created by wuxy on 2017/12/27.
 */

public interface NetCallback {
    void recv(byte[] data); //接收回调
    void prepared();        //网络准备成功,sendSocket
}
