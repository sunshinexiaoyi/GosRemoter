package gos.remoter.toast;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import gos.remoter.R;

/**
 * Created by QXTX-GOSPELL on 2017/9/5 0005.
 */
public class EPG_toast {
    private static Toast toast;
    private static Context context;
    private static View convertView;//承接ID索引
    public EPG_toast(Context context, ViewGroup parent) {
        this.context = context;
        convertView = LayoutInflater.from(context).inflate(R.layout.epg_toast, parent, false);
    }

    public static void myToast(String text) {
        TextView epgText = (TextView)convertView.findViewById(R.id.epg_toastText);//找到文本ID
        epgText.setText(text);//设置文本

        if (toast != null) {
            toast.cancel();//关闭旧吐司，使得吐司只实时显示一个
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 200);//设置吐司位置
        toast.setDuration(Toast.LENGTH_SHORT);//设置吐司显示时间
        toast.setView(convertView);//设置吐司布局
        toast.show();//显示吐司
    }
}
