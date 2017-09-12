package gos.remoter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gos.remoter.R;
import gos.remoter.data.HomeIcon;

/**
 * Created by lp on 2017/9/6.
 */

public class HomeGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HomeIcon> homeIcons;

    private HomeGridAdapter() {
    }
    private HomeGridAdapter(Context context, ArrayList<HomeIcon> homeIcons) {
        this.context = context;
        this.homeIcons = homeIcons;
    }

    @Override
    public int getCount() {
        if(homeIcons == null) {
            return 0;
        } else {
            return homeIcons.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return homeIcons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.home_grid_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.homeItemImage = (ImageView) convertView.findViewById(R.id.homeItemImage);
            viewHolder.homeItemName = (TextView) convertView.findViewById(R.id.homeItemName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.homeItemImage.setImageResource(homeIcons.get(position).getIcon());
        viewHolder.homeItemName.setText(homeIcons.get(position).getiName());
        return convertView;
    }

    private class ViewHolder {
        ImageView homeItemImage;
        TextView homeItemName;
    }

    /**
     * 添加一个元素
     * @param homeIcon
     */
    public void add(HomeIcon homeIcon) {
        if(homeIcons == null) {
            homeIcons = new ArrayList<>();
        }
        homeIcons.add(homeIcon);
        notifyDataSetChanged();
    }

    /**
     * 移除
     * @param homeIcon
     */
    public void remove(HomeIcon homeIcon) {
        if(homeIcons != null) {
            homeIcons.remove(homeIcon);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if(homeIcons != null) {
            homeIcons.remove(position);
        }
        notifyDataSetChanged();
    }

    /**
     * 清除所有
     */
    public void clear() {
        if(homeIcons != null) {
            homeIcons.clear();
        }
        notifyDataSetChanged();
    }
}
