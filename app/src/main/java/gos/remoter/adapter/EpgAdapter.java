package gos.remoter.adapter;

import gos.remoter.R;
import gos.remoter.define.CS;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

/**四个重写方法
 * 1、int getCount()
 * 2、Object getItem(int position)
 * 3、long getItemId(int position)
 * 4、View getView(int position, View convertView, ViewGroup parent)
 * 用法：
 * 1、convertView用来定位layout布局id；
 * 2、通过converView就能找到layout中所有的控件id
 * 3、可以用一个类来保存这些id，执行.setTag（Object）方法就行
 * 4、设置每一个控件的属性
 * 5、getView返回一个convertView
 */
public class EpgAdapter extends BaseAdapter {
    private int type;
    private Context context;//布局的id
    private ArrayList<Object> listData;//泛型的列表条目;
    public EpgAdapter(Context context, ArrayList<Object> listData) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_INIT);//流程顺序索引
        this.context = context;
        this.listData = listData;
    }

    //通过这个来判别布局的选择
    @Override
    public int getItemViewType(int position) {
        if (listData.get(position) instanceof EpgItem) {
            return CS.ADAPTER_TVITEM;
        } else if (listData.get(position) instanceof Epg_TVName) {
            return CS.ADAPTER_TVNAME;
        } else if (listData.get(position) instanceof Epg_TVDate) {
            return CS.ADAPTER_TVDATE;
        }
        else {
            return super.getItemViewType(position);
        }
    }
    @Override
    public int getViewTypeCount() {
        return CS.ADAPTER_VIEWTYPE;//3个布局样式
    }

    @Override
    public int getCount() {
        //Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETCOUNT);//流程顺序索引
        return listData != null ? listData.size() : 0;//如果为空列表就返回0长度，增加容错
    }
    @Override
    public long getItemId(int position) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETITEMID);//流程顺序索引
        return position;
    }
    @Override
    public Object getItem(int position) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETITEM);//流程顺序索引
        return listData.get(position);//？？？这是为了得到不同列表的位置
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETVIEW);//流程顺序索引
        type = getItemViewType(position);//得到布局条目类型
        Log.e(CS.ADAPTER_TAG, "type的值为：" + type);
        TVItemHolder tvItemHolder = null;
        TVNameHolder tvNameHolder = null;
        TVDateHolder tvDateHolder = null;
        if (type == CS.ADAPTER_TVITEM) {
            //节目内容的列表
            convertView = LayoutInflater.from(context).inflate(R.layout.epg_progitem, parent, false);
            tvItemHolder = new TVItemHolder(convertView);
        } else if (type == CS.ADAPTER_TVNAME) {
            //节目号的列表
            convertView = LayoutInflater.from(context).inflate(R.layout.epg_spinner_tvname, parent, false);
            tvNameHolder = new TVNameHolder(convertView);
        } else if (type == CS.ADAPTER_TVDATE) {
            //日期的列表
            convertView = LayoutInflater.from(context).inflate(R.layout.epg_spinner_tvdate, parent, false);
            tvDateHolder = new TVDateHolder(convertView);
        }

        //设置里面的属性id
        Object obj = listData.get(position);
        if (type == CS.ADAPTER_TVITEM) {
            //节目内容列表
            EpgItem epgItem = (EpgItem) obj;
            if (epgItem != null) {
                tvItemHolder.progName.setText(epgItem.getProgName());
                tvItemHolder.progTime.setText(epgItem.getPorgTime());
                tvItemHolder.progInfo.setText(epgItem.getProgInfo());
                tvItemHolder.simpleRecBtnOnce.setBackgroundResource(epgItem.getSimpleRecBtnOnce());
                tvItemHolder.simpleRecBtnCycle.setBackgroundResource(epgItem.getSimpleRecBtnCycle());
                tvItemHolder.simpleWatchBtnOnce.setBackgroundResource(epgItem.getSimpleWatchBtnOnce());
                tvItemHolder.simpleWatchBtnCycle.setBackgroundResource(epgItem.getSimpleWatchBtnCycle());

                tvItemHolder.recBtnOnce.setBackgroundResource(epgItem.getRecBtnOnce());
                tvItemHolder.recBtnCycle.setBackgroundResource(epgItem.getRecBtnCycle());
                tvItemHolder.watchBtnOnce.setBackgroundResource(epgItem.getWatchBtnOnce());
                tvItemHolder.watchBtnCycle.setBackgroundResource(epgItem.getWatchBtnCycle());
            }
        } else if (type == CS.ADAPTER_TVNAME) {
            //节目号列表
            Epg_TVName epg_tvName = (Epg_TVName) obj;
            if (epg_tvName != null) {
                tvNameHolder.tvName.setText(epg_tvName.getTVName());
            }
        } else if (type == CS.ADAPTER_TVDATE) {
            //节目日期列表
            Epg_TVDate epg_tvDate = (Epg_TVDate) obj;
            if (epg_tvDate != null) {
                tvDateHolder.tvDate.setText(epg_tvDate.getTVDate());
            }
        }
        return convertView;
    }

    public static class TVItemHolder {
        private TextView progName;//节目名
        private TextView progTime;//节目播放时间
        private TextView progInfo;//节目信息
        private Button simpleRecBtnOnce;//缩略图预定记录
        private Button simpleRecBtnCycle;//缩略图预定记录
        private Button simpleWatchBtnOnce;//缩略图预定记录
        private Button simpleWatchBtnCycle;//缩略图预定记录
        private Button recBtnOnce;//预定记录一次
        private Button recBtnCycle;//预定记录每天
        private Button watchBtnOnce;//预定观看一次
        private Button watchBtnCycle;//预定观看每天

        private TVItemHolder(View convertView) {
            progName = (TextView)convertView.findViewById(R.id.epg_progName);
            progTime = (TextView)convertView.findViewById(R.id.epg_progTime);
            progInfo = (TextView)convertView.findViewById(R.id.epg_simpleProgInfo);
            simpleRecBtnOnce = (Button)convertView.findViewById(R.id.epg_simpleRecordBtnOnce);
            simpleRecBtnCycle = (Button)convertView.findViewById(R.id.epg_simpleRecordBtnCycle);
            simpleWatchBtnOnce = (Button)convertView.findViewById(R.id.epg_simpleWatchBtnOnce);
            simpleWatchBtnCycle = (Button)convertView.findViewById(R.id.epg_simpleWatchBtnCycle);

            recBtnOnce = (Button)convertView.findViewById(R.id.epg_recBtnOnce);
            recBtnCycle = (Button)convertView.findViewById(R.id.epg_recBtnCycle);
            watchBtnOnce = (Button)convertView.findViewById(R.id.epg_watchBtnOnce);
            watchBtnCycle = (Button)convertView.findViewById(R.id.epg_watchBtnCycle);
        }
    }
    public static class TVNameHolder {
        private TextView tvName;
        public TVNameHolder(View convertView) {
            tvName = (TextView)convertView.findViewById(R.id.epg_TVName);
        }
    }
    public static class TVDateHolder {
        private TextView tvDate;
        public TVDateHolder(View convertView) {
            tvDate = (TextView)convertView.findViewById(R.id.epg_TVDate);
        }
    }

    //添加一个列表
    public void add(Object objData) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(objData);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }
    public void add(int position, Object objData) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(objData);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }

    //设置textView 文字过长则末尾用...代替
    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setSingleLine(true);
        }
    }
}