package gos.remoter.adapter;

import gos.remoter.define.CS;
import gos.remoter.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by QXTX-GOSPELL on 2017/9/6 0006.
 */

public class EpgAdapter extends BaseAdapter {
    private Context context;//布局的id
    private ArrayList<EpgItem> progData;//泛型的列表条目;
    public EpgAdapter(Context context, ArrayList<EpgItem> progData) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_INIT);//流程顺序索引
        this.context = context;
        this.progData = progData;
    }
    @Override
    public int getCount() {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETCOUNT);//流程顺序索引
        return progData != null ? progData.size() : 0;//如果为空列表就返回0长度，增加容错
    }
    @Override
    public long getItemId(int position) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETITEMID);//流程顺序索引
        return position;
    }
    @Override
    public Object getItem(int position) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETITEM);//流程顺序索引
        return progData.get(position);//？？？这是为了得到不同列表的位置
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(CS.ADAPTER_TAG, CS.ADAPTER_GETVIEW);//流程顺序索引
        ProgViewHolder progViewHolder = null;
        //节目列表条目
        convertView = LayoutInflater.from(context).inflate(R.layout.epg_progitem, parent, false);
        progViewHolder = new  ProgViewHolder(convertView, parent);

        //设置里面的属性id
        EpgItem epgItem = progData.get(position);
        if (epgItem != null) {
            progViewHolder.progName.setText(epgItem.getProgName());
            progViewHolder.progTime.setText(epgItem.getPorgTime());
            progViewHolder.progInfo.setText(epgItem.getProgInfo());
            progViewHolder.simpleRecBtnOnce.setBackgroundResource(epgItem.getSimpleRecBtnOnce());
            progViewHolder.simpleRecBtnCycle.setBackgroundResource(epgItem.getSimpleRecBtnCycle());
            progViewHolder.simpleWatchBtnOnce.setBackgroundResource(epgItem.getSimpleWatchBtnOnce());
            progViewHolder.simpleWatchBtnCycle.setBackgroundResource(epgItem.getSimpleWatchBtnCycle());

            progViewHolder.recBtnOnce.setBackgroundResource(epgItem.getRecBtnOnce());
            progViewHolder.recBtnCycle.setBackgroundResource(epgItem.getRecBtnCycle());
            progViewHolder.watchBtnOnce.setBackgroundResource(epgItem.getWatchBtnOnce());
            progViewHolder.watchBtnCycle.setBackgroundResource(epgItem.getWatchBtnCycle());
        }
        return convertView;
    }

    public static class ProgViewHolder {
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

        private ProgViewHolder(View convertView, ViewGroup parent) {
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
    public void add(EpgItem epgItem) {
        if (progData == null) {
            progData = new ArrayList<EpgItem>();
        }
        progData.add(epgItem);
        notifyDataSetChanged();
    }
    //指定位置添加一个列表
    public void add(EpgItem epgItem, int position) {
        if (progData == null) {
            progData = new ArrayList<EpgItem>();
        }
        progData.add(position, epgItem);
        notifyDataSetChanged();
    }
    //修改按钮背景
    public void setSimpleRecButtonOnce() {

    }
}