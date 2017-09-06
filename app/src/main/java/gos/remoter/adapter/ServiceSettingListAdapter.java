package gos.remoter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gos.remoter.R;

/**
 * Created by lpei on 2017/7/10.
 */

public class ServiceSettingListAdapter extends BaseAdapter{

    private Context mcontext;
    public static ArrayList<String> address = null;

    private class ViewHolder{
        private TextView tv_address;
    }

    public ServiceSettingListAdapter(Context context, ArrayList<String> address){
        this.mcontext = context;
        this.address = address;
    }
    public int getCount(){
        if(address == null)
            return 0;
        else
            return address.size();
    }

    public long getItemId(int arg0){
        return arg0;
    }

    public Object getItem(int position) {
        return address.get(position);
    }

    public View getView(int position, View view, ViewGroup viewGroup){
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mcontext).inflate(R.layout.service_address_item,null);

            viewHolder.tv_address = (TextView)view.findViewById(R.id.item_address);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_address.setText(address.get(position));
        return view;
    }

    public void notifyDataSetChanged(ArrayList<String> myAddress){
        super.notifyDataSetChanged();
        if(myAddress == null){
            this.address = null;
        }else{
            this.address = myAddress;
        }
    }
}
