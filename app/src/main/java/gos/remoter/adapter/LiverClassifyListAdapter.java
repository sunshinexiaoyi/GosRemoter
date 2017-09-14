package gos.remoter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import gos.remoter.R;
import gos.remoter.activity.LiverActivity;

/**
 * Created by djk on 2017/9/12.
 */

public class LiverClassifyListAdapter extends BaseAdapter {
    private Context context;
    private String[] strings;
    public static int mPosition;

    public LiverClassifyListAdapter(Context context, String[] strings){
        this.context =context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = LayoutInflater.from(context).inflate(R.layout.liver_program_classify_list_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.live_classify_list_item_id);
        mPosition = position;
        tv.setText(strings[position]);
        if (position == LiverActivity.selectPosition) {
            tv.setBackgroundResource(R.drawable.liver_checked);
            tv.setTextColor(Color.parseColor("#3490DB"));
            tv.setTextSize(18);
            Log.i("live",  tv.getLayoutParams()+"");
        } else {
            tv.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        return convertView;
    }
}
