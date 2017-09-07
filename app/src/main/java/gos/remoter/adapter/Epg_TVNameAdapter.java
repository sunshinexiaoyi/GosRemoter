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

public class Epg_TVNameAdapter extends BaseAdapter {
    private Context context;//布局的id
    private ArrayList<Epg_TVName> listData;//泛型的列表条目;
    public Epg_TVNameAdapter(Context context, ArrayList<Epg_TVName> listData) {
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
        TVNameHolder tvNameHolder = null;
        //节目内容的列表
        Log.e("消息", "是名字");
        convertView = LayoutInflater.from(context).inflate(R.layout.epg_spinner_tvname, parent, false);
        tvNameHolder = new TVNameHolder(convertView);
        //设置里面的属性id
        Epg_TVName epg_tvName = listData.get(position);
        tvNameHolder.tvName.setText(epg_tvName.getTVName());
        return convertView;
    }

    public static class TVNameHolder {
        private TextView tvName;
        public TVNameHolder(View convertView) {
            tvName = (TextView)convertView.findViewById(R.id.epg_TVName);
        }
    }

    //添加一个列表
    public void add(Epg_TVName epg_tvName) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(epg_tvName);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }
    public void add(int position, Epg_TVName epg_tvName) {
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.add(position, epg_tvName);
        notifyDataSetChanged();
        Log.e(CS.ADAPTER_TAG, "更新了一次列表");
    }
}
