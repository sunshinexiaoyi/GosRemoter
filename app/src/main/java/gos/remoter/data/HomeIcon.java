package gos.remoter.data;

/**
 * Created by lp on 2017/9/6.
 */

public class HomeIcon {
    private int icon;
    private String iName;

    public HomeIcon() {
    }

    public HomeIcon(int icon, String iName) {
        this.icon = icon;
        this.iName = iName;
    }

    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getiName() {
        return iName;
    }
    public void setiName(String iName) {
        this.iName = iName;
    }
}
