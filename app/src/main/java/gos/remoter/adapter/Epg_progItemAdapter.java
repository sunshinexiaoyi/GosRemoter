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

public class Epg_progItemAdapter extends BaseAdapter {
    private Context context;//布局的id
    private ArrayList<Epg_progItem> listData;//泛型的列表条目;
    public Epg_progItemAdapter(Context context, ArrayList<Epg_progItem> listData) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_INIT);//流程顺序索引
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        //Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETCOUNT);//流程顺序索引
        return listData != null ? listData.size(): 0;//如果为空列表就返回0长度，增加容错
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
        TVItemHolder tvItemHolder = null;
        //节目内容的列表
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.epg_progitem, parent, false);
            tvItemHolder = new TVItemHolder(convertView);
        }
        Epg_progItem epg_progItem = listData.get(position);
        //设置里面的属性id
        tvItemHolder.progName.setText(epg_progItem.getProgName());
        tvItemHolder.progTime.setText(epg_progItem.getPorgTime());
        tvItemHolder.progInfo.setText(epg_progItem.getProgInfo());
        tvItemHolder.simpleRecBtnOnce.setBackgroundResource(epg_progItem.getSimpleRecBtnOnce());
        tvItemHolder.simpleRecBtnCycle.setBackgroundResource(epg_progItem.getSimpleRecBtnCycle());
        tvItemHolder.simpleWatchBtnOnce.setBackgroundResource(epg_progItem.getSimpleWatchBtnOnce());
        tvItemHolder.simpleWatchBtnCycle.setBackgroundResource(epg_progItem.getSimpleWatchBtnCycle());

        tvItemHolder.recBtnOnce.setBackgroundResource(epg_progItem.getRecBtnOnce());
        tvItemHolder.recBtnCycle.setBackgroundResource(epg_progItem.getRecBtnCycle());
        tvItemHolder.watchBtnOnce.setBackgroundResource(epg_progItem.getWatchBtnOnce());
        tvItemHolder.watchBtnCycle.setBackgroundResource(epg_progItem.getWatchBtnCycle());
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

    //添加一个列表
    public void add(Epg_progItem progData) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(progData);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }
    public void add(int position, Epg_progItem progData) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(position, progData);
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