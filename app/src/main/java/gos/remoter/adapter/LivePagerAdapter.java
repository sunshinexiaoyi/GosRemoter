package gos.remoter.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gos.remoter.R;
import gos.remoter.data.Program;
import gos.remoter.data.Time;

/**
 * Created by lp on 2018/1/9.
 * 主页广告
 */

public class LivePagerAdapter extends PagerAdapter {

    private ArrayList<Time> epgInform;
    private Program curProgram;

    private Context context;
    private LayoutInflater inflater;
    private LinearLayout layout;
    private TextView programName;
    private TextView titleName;
    private TextView titleTime;
    private TextView longDesc;


    public LivePagerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public LivePagerAdapter(Context context, ArrayList<Time> epgInform) {
        this.context = context;
        this.epgInform = epgInform;
        inflater = LayoutInflater.from(context);
    }

    public LivePagerAdapter(Context context, ArrayList<Time> epgInform, Program curProgram) {
        this.context = context;
        this.epgInform = epgInform;
        this.curProgram = curProgram;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return epgInform == null ? 0 : epgInform.size();//返回一个无穷大的值，Integer.MAX_VALUE
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.live_epg_list, container, false);
        layout = (LinearLayout) view.findViewById(R.id.pager_item);
        programName = (TextView) view.findViewById(R.id.live_programTitle);
        titleName = (TextView) view.findViewById(R.id.live_epg_titleName);
        titleTime = (TextView) view.findViewById(R.id.live_epg_titleTime);
        longDesc = (TextView) view.findViewById(R.id.live_epg_longDesc);

        programName.setText("EPG" + "——" + curProgram.getName());
        titleName.setText(epgInform.get(position).getEvent());
        titleTime.setText(epgInform.get(position).getStartTime() + " - " + epgInform.get(position).getEndTime());
        longDesc.setText("\t\t" + epgInform.get(position).getLongDes());//长描述,首行缩进

        container.addView(view);
        return view;

        /*
        //只有ViewPager，没设置CardView时
        ImageView image = new ImageView(context);
        image.setImageResource(list.get(position));
        container.addView(image);
        return image;*/
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }
}
