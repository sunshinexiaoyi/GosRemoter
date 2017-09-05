package gos.remoter.adapter;

/**
 * Created by QXTX-GOSPELL on 2017/9/5 0005.
 */

public class EpgItem {
    private String progName;//节目名
    private String time;//节目播放事件
    private String progInfo;//节目信息
    private boolean showSimpleButton = false;//缩略图下的右侧按钮
    private boolean showProgSetting = false;//详情下的设置模块
    private boolean showCurrentSelectImage = false;//左上角的选中角标

    public EpgItem(String progName, String time, String progInfo,
                   boolean showSimpleButton, boolean showProgSetting,
                   boolean showCurrentSelectImage) {
        this.progName = progName;
        this.time = time;
        this.progInfo = progInfo;
        this.showSimpleButton = showSimpleButton;
        this.showProgSetting = showProgSetting;
        this.showCurrentSelectImage = showCurrentSelectImage;
    }

    public String  getProgName() {
        return progName;
    }
    public String getTime() {
        return time;
    }
    public String getProgInfo() {
        return progInfo;
    }
    public boolean getShowCurrentSelectImage() {
        return showCurrentSelectImage;
    }
    public boolean getShowSimopleButton() {
        return showSimpleButton;
    }
    public boolean getShowProgSetting() {
        return showProgSetting;
    }

    public void setProgName(String progName) {
        this.progName = progName;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setProgInfo(String progInfo) {
        this.progInfo = progInfo;
    }
    public void setShowSimpleButton(boolean showSimpleButton) {
        this.showSimpleButton = showSimpleButton;
    }
    public void setShowProgSetting(boolean showProgSetting) {
        this.showProgSetting = showProgSetting;
    }
    public void setShowCurrentSelectImage(boolean showCurrentSelectImage) {
        this.showCurrentSelectImage = showCurrentSelectImage;
    }
}
