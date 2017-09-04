package gos.remoter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import gos.remoter.callback.ReserveSetCallback;
import gos.remoter.R;
import gos.remoter.data.Time;

/**
 * Created by wuxy on 2017/6/26.
 */

public class EpgTimeAdapter extends BaseAdapter{

    ArrayList<Time> timeArrayList = new ArrayList<>();
    Context context;
    HashMap<Integer,Integer>  imgIcon = new HashMap<>();//0：delete， 1：View， 2：View Series， 3： Record，4： Record Series

    ReserveSetCallback reserveSetCallback = null;

    public  EpgTimeAdapter(Context context)
    {
        this.context = context;

        imgIcon.put(0, R.drawable.ic_set);
        imgIcon.put(1,R.drawable.ic_av_timer);
        imgIcon.put(2,R.drawable.ic_av_timer);

        imgIcon.put(3,R.drawable.ic_record);
        imgIcon.put(4,R.drawable.ic_record);

    }

    @Override
    public int getCount() {
        return timeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return timeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item,parent,false);
            holder = new ViewHolder();
            holder.imgBtn = (ImageButton) convertView.findViewById(R.id.btnSet);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.event = (TextView) convertView.findViewById(R.id.event);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //Log.i("getEventType",timeArrayList.get(position).getEventType());
        Time time = timeArrayList.get(position);
        holder.imgBtn.setImageResource(imgIcon.get(Integer.parseInt(time.getEventType())));
        holder.imgBtn.setOnClickListener(new ClickSet(context,time));
        holder.imgBtn.setFocusable(false); //设置为false，item才能触发click事件

        String timeStr = time.getStartTime()+"->"+time.getEndTime();
        holder.time.setText(timeStr);
        holder.event.setText(time.getEvent());
        setTextMarquee( holder.event);
        return convertView;
    }


    public void setNotifyDataSetChanged(ArrayList<Time> timeArrayList) {

        this.timeArrayList.clear();
        this.timeArrayList.addAll(timeArrayList);
       // Log.i("reserve","setNotifyDataSetChanged");

        super.notifyDataSetChanged();
    }

    public void setMenuCallback(ReserveSetCallback reserveSetCallback)
    {
        this.reserveSetCallback = reserveSetCallback;
    }

    class ClickSet implements View.OnClickListener
    {
        Context context;
        Time time;


        public ClickSet(Context context,Time time)
        {
            this.time = time.clone();
            this.context = context;
        }


        @Override
        public void onClick(View v) {
            int bookState = Integer.parseInt(time.getEventType());
            if(bookState == 0)
            {
                //show menu
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_reserve_set,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.i("getItemId",""+item.getItemId());
                        int command = 0;
                        switch (item.getItemId())
                        {
                            case R.id.menu_view:
                                time.setEventType("1");
                                break;
                            case R.id.menu_view_series:
                                time.setEventType("2");
                                break;
                            case R.id.menu_record:
                                time.setEventType("3");
                                break;
                            case R.id.menu_record_series:
                                time.setEventType("4");
                                break;
                            default:
                                break;
                        }

                        Log.i("reserve",""+ time.getEventType());

                        if(null != reserveSetCallback)
                        {
                            reserveSetCallback.sendSetInfo(time);
                        }
                        return true;
                    }
                });

                popupMenu.show();


            }else {
                time.setEventType("0");
                //cancle
                Log.i("reserve",""+ time.getEventType());

                notifyDataSetChanged();

                if(null != reserveSetCallback)
                {
                    reserveSetCallback.sendSetInfo(time);
                }

            }

        }

    }

    static class ViewHolder{
        ImageButton imgBtn;
        TextView time;
        TextView event;
    }

    /**
     * 设置textView 文字过长则末尾用...代替
     * @param textView
     */
    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setSingleLine(true);
            //textView.setSelected(true);
           // textView.setFocusable(true);
            //textView.setFocusableInTouchMode(true);
        }
    }


}

