package gos.remoter.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import gos.remoter.R;
import gos.remoter.define.CS;

/**
 * Created by QXTX-GOSPELL on 2017/9/7 0007.
 */

public class Epg_TVDateAdapter extends BaseAdapter {
    private Context context;//布局的id
    private ArrayList<Epg_TVDate> listData;//泛型的列表条目;
    public Epg_TVDateAdapter(Context context, ArrayList<Epg_TVDate> listData) {
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
        Log.e(CS.ADAPTER_TAG, "position的值是" + position);
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
        TVDateHolder tvDateHolder = null;
        //节目内容的列表
        Log.e("消息", "是日期");
        convertView = LayoutInflater.from(context).inflate(R.layout.epg_spinner_tvdate, parent, false);
        tvDateHolder = new TVDateHolder(convertView);
        //设置里面的属性id
        Epg_TVDate epg_tvDate = listData.get(position);
        tvDateHolder.tvDate.setText(epg_tvDate.getTVDate());
        return convertView;
    }

    public static class TVDateHolder {
        private TextView tvDate;
        public TVDateHolder(View convertView) {
            tvDate = (TextView)convertView.findViewById(R.id.epg_TVDate);
        }
    }

    //添加一个列表
    public void add(Epg_TVDate epg_tvDate) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(epg_tvDate);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }
    public void add(int position, Epg_TVDate epg_tvDate) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(position, epg_tvDate);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }
}