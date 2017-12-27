package gos.remoter.data;

/**
 * Created by wuxy on 2017/7/7.
 *
 * 对指定命令的应答，返回值判断命令是否成功
 */

public class Respond extends Reserve{
    private int command;
    private boolean flag;

    public Respond(){super();}
    public Respond(int command, boolean flag){
        super();
        this.command = command;
        this.flag = flag;
    }

    public int getCommand(){
        return command;
    }
    public void setCommand(int command){
        this.command = command;
    }

    public boolean getFlag(){
        return flag;
    }
    public void setFlag(boolean flag){
        this.flag = flag;
    }
}
