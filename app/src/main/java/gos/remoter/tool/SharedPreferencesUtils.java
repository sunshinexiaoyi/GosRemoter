package gos.remoter.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtils {

    public static Context mContext;//设为静态的主要是可以直接在Application中初始化，若在每个Activity中传入context，则不为静态
    private static SharedPreferences mSp; //只获取数据

    public SharedPreferencesUtils(Context context)
    {
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getDefaultPreference()
    {
        mSp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mSp;
    }

    /**
     * 默认的配置文件preferences.xml，使用getDefaultSharedPreferences获取
     * @param context
     */
    public static void init(Context context)
    {
        mContext = context;
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @param context
     * @param s   文件名
     *     mode ：文件操作模式，  MODE_PRIVATE：只能被本应用读写
     */
    public SharedPreferencesUtils(Context context, String s)
    {
        mSp = context.getSharedPreferences(s, MODE_PRIVATE);
    }

    public SharedPreferencesUtils(Context context, String s, int i)
    {
        mSp = context.getSharedPreferences(s, i);
    }

    public static String get(Context context, String s, String s1, String s2)
    {
        return context.getSharedPreferences(s, 0).getString(s1, s2);
    }

    public static void clear()
    {
        SharedPreferences.Editor editor = mSp.edit();//对数据进行存储和修改
        editor.clear();
        editor.apply();
    }

    public static String get(String s)
    {
        return get(s, "");
    }

    public static String get(String s, String s1)
    {
        return mSp.getString(s, s1);
    }

    public static boolean getBoolean(String s, boolean flag)
    {
        return mSp.getBoolean(s, flag);
    }

    public static int getInt(String s, int i)
    {
        return mSp.getInt(s, i);
    }

    public static long getLong(String s, long i)
    {
        return mSp.getLong(s, i);
    }

    public static float getFloat(String s, float i)
    {
        return mSp.getFloat(s, i);
    }

    public static void save(String s, int i)
    {
        mSp.edit().putInt(s, i).apply();
    }

    public static void save(String s, String s1)
    {
        mSp.edit().putString(s, s1).apply();
    }

    public static void save(String s, boolean flag)
    {
        mSp.edit().putBoolean(s, flag).apply();
    }

    public static void save(String s, long i)
    {
        mSp.edit().putLong(s, i).apply();
    }

    public static void save(String s, float i)
    {
        mSp.edit().putFloat(s, i).apply();
    }

}
/* // 从SharedPreferences中获取历史记录数据
    private String getHistoryFromSharedPreferences(String key) {
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        return sp.getString(key, SP_EMPTY_TAG); // 读取字符串数据，默认为""
    }

    // 将历史记录数据保存到SharedPreferences中
    private void saveHistoryToSharedPreferences(String key, String history) {
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, history);//添加新的配置数据
        editor.apply();//提交
    }

    // 清除保存在SharedPreferences中的历史记录数据
    private void clearHistoryInSharedPreferences() {
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }*/