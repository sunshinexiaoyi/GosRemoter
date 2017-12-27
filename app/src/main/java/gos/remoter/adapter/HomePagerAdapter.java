package gos.remoter.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import gos.remoter.R;

/**
 * Created by lp on 2017/12/20.
 * 主页广告
 */

public class HomePagerAdapter extends PagerAdapter {

    private List<Integer> imageList;
    private String[] ads = null;

    private Context context;
    private LayoutInflater inflater;
    private ImageView imageView;


    public HomePagerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public HomePagerAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
    }

    public HomePagerAdapter(Context context, String[] ads) {
        this.context = context;
        this.ads = ads;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(null != ads) {
            return ads.length;
        }
        return imageList == null ? 0 : imageList.size();//返回一个无穷大的值，Integer.MAX_VALUE
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_viewpager, container, false);
        imageView = (ImageView) view.findViewById(R.id.cardImage);

        if(null == ads) {
            imageView.setImageResource(imageList.get(position % imageList.size()));
        } else {
            Glide.with(context)
                    .load(ads[position % ads.length])               //图片地址
                    .placeholder(R.drawable.details_bg_window)      //加载中显示的图片,设置占位图
                    .error(R.drawable.details_bg_window)            //加载失败时显示的图片
                    .thumbnail(0.1f)                                //10%的原图大小
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)      //磁盘缓存策略
                    .skipMemoryCache(true)                        //设置图片不加入到内存缓存
                    .into(imageView);                               //加载图片的ImageView
//            Log.e("ads----", ads.length + "网上图片");

        }
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
