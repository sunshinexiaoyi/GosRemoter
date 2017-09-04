package gos.remoter.define;


import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by wuxy on 2017/7/5.
 * 底部菜单管理
 */

public class TabMenuManager {
    private ArrayList<TabMenuItem> TabMenu = new ArrayList<>();


    public class TabMenuItem{

        public int img;
        public int title;
        public Class fragment;
        public Fragment instance = null;

        public TabMenuItem(int title,int img,Class fragment){
            this.img = img;
            this.title = title;
            this.fragment = fragment;
        }

    }

    /**
     * @param title 菜单标题
     * @param img   菜单图片
     * @param fragment  菜单fragment
     */
    public void addTabMenuItem(int title,int img,Class fragment) {

        TabMenu.add(new TabMenuItem(title,img,fragment));
    }

    public int getTabMenuNum() {
        return TabMenu.size();
    }


    public ArrayList<TabMenuItem> getTabMenu(){
        return TabMenu;
    }

}


