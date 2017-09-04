package gos.remoter.data;

/**
 * 节目url类
 * Created by wuxy on 2017/7/11.
 */

public class ProgramUrl extends IndexClass {
    private String url;

    public ProgramUrl(){}
    public ProgramUrl(int index,String url){
        super(index);
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url = url;
    }
}
