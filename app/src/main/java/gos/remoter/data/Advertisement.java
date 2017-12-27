package gos.remoter.data;

/**
 * 广告类
 * Created by lp on 2017/12/25.
 */

public class Advertisement {
    private String adUrl;

    public Advertisement() {
    }

    public Advertisement(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getAdUrl() {
        return adUrl;
    }
    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }
}
