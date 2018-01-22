package gos.remoter.tool;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import gos.remoter.R;

import static gos.remoter.activity.SettingLanguage.SETTING_LANGUAGE_SELECTED;

/**
 * Created by lp on 2018/1/19.
 */

public class LanguageUtil {

    private static Context context;

    public LanguageUtil() {
    }

    public LanguageUtil(Context context) {
        this.context = context;
    }

    /**
     * 是否是设置值
     *
     * @return 是否是设置值
     */
    public static boolean isSetValue() {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        return currentLocale.equals(getSetLocale());
    }

    /**
     * 切换语言后，从主页跳到其他界面时，更新语言
     * @return
     */
    public static boolean isSetChange() {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        return currentLocale.getDisplayLanguage().equals(getSetLocale().getDisplayLanguage());
    }

    /**
     * 得到设置的语言信息
     * @return
     */
    public static Locale getSetLocale() {
        // 读取储存的语言设置信息
        String language = SharedPreferencesUtils.get(SETTING_LANGUAGE_SELECTED , context.getResources().getString(R.string.lanSystem));
        if (language.equals(context.getResources().getString(R.string.lanEnglish))) {
            return Locale.ENGLISH;
        } else if(language.equals(context.getResources().getString(R.string.lanChinse))) {
            return Locale.SIMPLIFIED_CHINESE;
        } else {
            return Locale.getDefault();
        }
    }

    public static void resetDefaultLanguage() {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = getSetLocale();
        resources.updateConfiguration(configuration, displayMetrics);
    }


}
