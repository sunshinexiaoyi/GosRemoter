package gos.media.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by wuxy on 2017/7/6.
 */

public class TabMenuAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentList;

    //getChildFragmentMange

    public TabMenuAdapter(FragmentManager fm) {
        super(fm);

    }


    public void setFragmentList( ArrayList<Fragment> fragmentList){
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);

        super.destroyItem(container, position, object);
    }


}
