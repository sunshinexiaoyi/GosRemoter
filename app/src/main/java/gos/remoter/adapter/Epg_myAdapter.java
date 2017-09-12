package gos.remoter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import gos.remoter.R;

/**一个通配适配器
 * 传参：
 * 1、上下文：为了寻找到布局的id
 * 2、item：传入一个条目的所有数据集合
 * 3、布局id：为了寻找到指定的layout布局
 *
 * 备注：ViewGroup:上层layout， 利用它可以对上层layout进行操作
 */
public abstract class Epg_myAdapter<T> extends BaseAdapter {
    private Context context;//这里保存是为了给内部类也能使用上下文
    private ArrayList<T> list;//一个通用item列表，将获取到的item全部保存到这里，组成一个listview
    private int layoutRes;//布局的id需要从外面传进来，bind中需要用它来获取convertView

    //构造方法，从ACT中传入必需的三个数据
    public Epg_myAdapter(Context context, ArrayList<T> list, int layoutRes) {
        this.context = context;
        this.list = list;
        this.layoutRes = layoutRes;
    }

    /**Adapter中总是第一个被调用的方法
     * 功能：
     * 1、每一次列表被更新，都会执行一次getCount()方法，为了计算出列表中一共有多少个item条目
     */
    @Override
    public int getCount() {
        //Log.e("消息", "一共有" + list.size() + "行");
        return list == null? 0 : list.size();
    }

    /**getItem()和getItemId()两个方法
     * 用途：
     * 1、getItem()方法用于获得某个位置上的item数据，返回一个通用的view，可能需要类型转换
     * 2、getItemId()方法用于获得某个位置上的item的id，返回long类型的id，需要类型转换（转成int）
     *
     * 备注：它们只有在被要求时才会被调用
     */
    @Override
    public T getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**getView()方法
     *
     * 这个方法在每次获取到一个item，都会被调用一次
     * 此方法提供item自动复用机制
     * 本方法中的convertView有一下特点：
     *   1、一个屏幕中可以显示n个item，那么前n+2次调用getVIew()方法时convertView都是null；（固定缓存n+2个convertView）
     *   2、当屏幕上的列表被滚动，当第n+3个item出现在屏幕中，这个item会由于convertView的复用机制，被填充第1个item的数据；
     *   也就是第一个item的convertView被回收，给了第n+3个item。
     *   3、如果给第1个item设置了Tag,则当列表回滚第1个Item再次出现在屏幕上，这个item将会直接通过Tag得到已保存的数据，
     *   不需要再次申请，而屏幕外的第n+2之后的item全部被回收。
     *
     * 备注：由于每次更新列表都会导致getView被重新调用，因此需要注意其中的局部变量将会被反复初始化而不能长时保存数据。
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**将获取convertView部分放到Holder构造中
         * 完成了findbyId部分
         * 完成了实例化convertView
         */
        Holder holder = Holder.bind(position, convertView, parent, layoutRes, context);

        /**这里的实现交由外部完成，set数据部分
         * 因为由于布局不一样，里面的属性也不一定相同，所以要在外部自定义完成；
         * holder提供了set方法
         */
        bindView(holder, getItem(position));
        //注意：这里返回的convertView不能为空！！！否则会报
        return holder.convertView;
    }


/************************静态内部类Holder*****************************/

    /**静态的内部类Holder
     *
     * 这里的Holder需要完成的工作：
     * 1、保存convertView
     * 2、设置Tag
     * 3、暴露一堆set方法
     *
     * 备注1：暴露的set方法，用于在Adapter外部也能实现view属性的设置
     * 备注2：因为这里的Adapter为泛型适配器，传进来的类型千奇百怪，无法在这里进行预先设置，所以需要暴露set方法以便在外部设置
     * 备注3：关于为什么设置为静态，是为了避免反复调用时被重复创建，增加浪费。如果只调用一次，可以不需要设置为静态
     * */
    public static class Holder {
        private int position;
        private View convertView;//保存主view
        private Context context;

        /**初始化item的view列表
         * SparseArray有更好的性能较之ArrayList
         * 这是为了保存item中的各种id
         * 因为不知道不同的item中有多少属性，所以用一个list来存储各种id
         */
        private SparseArray<View> itemView;


        /**构造方法
         * SparseArray初始化
         * 在这里找到主view并设置Tag
         * 保存context和itemView
         */
        public Holder(Context context, ViewGroup parent, int layoutRes) {
            itemView = new SparseArray<>();
            this.context = context;
            convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
        }


        /**绑定view
         * 注意findById并不能在内部做，因为不同layout可能有不同的各种属性
         * 这里获取的position，是为了给各种暴露的set方法使用
         */
        public static Holder bind(int position, View convertView,
                                  ViewGroup parent, int layoutRes, Context context) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder(context, parent, layoutRes);//这里完成了holder的两项基本工作
            } else {
                //从convertView中获取到holder
                holder = (Holder)convertView.getTag();
                holder.convertView = convertView;
            }
            holder.position = position;//得到一个position
            return holder;
        }


/**************实例化item的各种属性、获取item位置*******************/

        /**id实例化，保存得到的view
         *
         * 1、将获取到的各种view的id进行实例化，以便提供给暴露的set方法使用
         * 2、将得到的view添加到一个list中保存
         * 3、返回一个泛型view，可以是layout的view，也可以是dialog的view，等等
         *
         * 备注：set方法中使用这个返回的view，可能需要将这个泛型view进行类型转换后，以便调用某些特定view才有的方法
         */
        public <T extends View>T getView(int id) {
            T t = (T)itemView.get(id);//从列表中获得id
            if (t == null) {
                //如果列表中没有，则从主View中拿，然后放进列表中，变相的完成了findById()方法
                t = (T)convertView.findViewById(id);
                //将id保存起来。因为sparseArray还可以用一个key来索引数据，提供两种索引方式。
                itemView.put(id, t);
            }
            return t;//返回的可以是一个id
        }

        //提供获取当前的item
        public View getItem() {
            return convertView;
        }
        //提供获取当前条目的位置
        public int getItemPosition() {
            return position;
        }


/*******************实例化item中的各种属性************************/

        /**上文获取了一堆id，这里将要set一堆数据
         *
         * 目前set方法有
         * 1、setText
         * 2、setTextWidth
         * 3、setTextHeight
         * 4、setGONE
         * 5、setVISIBLE
         * 6、setTextMarquee
         * 7、setTextNormal
         * 8、setBackgroundResource
         * 9、setImageResource
         * 10、setOnClickListener
         * 11、setItemOnClickLIstener
         *
         * 备注1：通过传入一个id，将此id保存到item列表中
         * 备注2：CharSequence：字符数据类型接口，String为其中一种实现
         * 备注3：Holder类型的返回值目前看起来用不到，可以将这些方法置为void类型
         */

        //1、设置文本
        public Holder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text.toString());//需要将view转换成相应的view
            }
            return this;
        }

        //2、设置文本框宽度
        public void setTextWidth(int id, int px) {
            //不处理特殊数据：wrap_content/match_parent/fill_parent（都是负数）
            if (px >= 0) {
                px = (int)(px * 401 / 160);//这里强制以5.5寸、1080P的属性计算，后期可以同通过获取设备ppi的方式精确计算
            }
            View view = getView(id);
            view.getLayoutParams().width = px;
        }
        //3、设置文本框高度
        public void setTextHeight(int id, int px) {
            //不处理特殊数据：wrap_content/match_parent/fill_parent（都是负数）
            if (px >= 0) {
                px = (int)(px * 401 / 160);//这里强制以5.5寸、1080P的属性计算，后期可以同通过获取设备ppi的方式精确计算
            }
            View view = getView(id);
            view.getLayoutParams().height = px;
        }

        //4、隐藏控件
        public void setGONE(int id) {
            View view = getView(id);
            view.setVisibility(View.GONE);
        }
        //5、显示控件
        public void setVISIBLE(int id) {
            View view = getView(id);
            view.setVisibility(View.VISIBLE);
        }

        //6、设置文本过长时显示模式
        public void setTextMarquee(int id) {
            View view = getView(id);
            ((TextView)view).setEllipsize(TextUtils.TruncateAt.END);//省略号
            ((TextView)view).setSingleLine(true);
        }
        //7、取消文本过长处理状态
        public void setTextNormal(int id) {
            View view = getView(id);
            ((TextView)view).setEllipsize(null);
            ((TextView)view).setSingleLine(false);
        }

        //8、设置背景，包括Button， ImageButton，但不包括ImageView
        public Holder setBackgroundResource(int id, int drawableRes) {
            View view = getView(id);
            view.setBackgroundResource(drawableRes);
            return this;
        }
        //9、设置背景，ImageView独占
        public Holder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            ((ImageView)view).setImageResource(drawableRes);
            return this;
        }

        //10、设置监听item中的按钮点击
        public Holder setOnClickListener(int id, View.OnClickListener click) {
            View view = getView(id);
            view.setOnClickListener(click);
            return this;
        }

        //11、设置监听item点击
        public Holder setItemOnClickLIstener(ListView listView, AdapterView.OnItemClickListener itemClick) {
            listView.setOnItemClickListener(itemClick);
            return this;
        }
    }


/*******************为Adapter暴露添加元素的方法************************/

    /**这里是Adapter方法
     * 目前可暴露的方法有：
     * 1、添加一个item（默认在队列末端）
     * 2、在制定位置添加 一个tiem
     */

    //添加 一个item（默认位置为列表末端）
    public void add(T obj) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(obj);
        notifyDataSetChanged();
    }

    //在制定位置添加 一个item
    public void add(int position, T obj) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(position, obj);
        notifyDataSetChanged();
    }


/*************************抽象方法*****************************/

    /**抽象方法，
     * 1、为不同蜡yout设置不同的数据
     * 2、实现暴露的方法
     * 3、可回调obj方法实现属性数据的获取
     * 4、增强Adapter的外部扩展性
     */
    public abstract void bindView(Holder holder, T obj);
}
