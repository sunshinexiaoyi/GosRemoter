package gos.remoter.adapter;

/**
 * Created by QXTX-GOSPELL on 2017/9/6 0006.
 */

public class EpgTVList {
    private String TVName;

    public EpgTVList(String TVName) {
        this.TVName = TVName;
    }

    public void setTVName(String TVName) {
        this.TVName = TVName;
    }
    public String getTVName() {
        return TVName;
    }
}
