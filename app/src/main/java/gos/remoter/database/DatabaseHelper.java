
package gos.remoter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import gos.remoter.data.Program;

/**
 * 数据库工具类
 *
 * 应用场景: 本地存储大量结构化的数据时，比如List集合数据
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "gosMedia.db";

    private Map<String, Dao> daos = new HashMap<>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            //创建表
            TableUtils.createTable(connectionSource, Program.class);//类为需要创建表的泛型类
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try
        {
            //更新表，删除
            TableUtils.dropTable(connectionSource, Program.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper helper;

    /**
     * 单例获取该Helper
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        context = context.getApplicationContext();

        if (helper == null) {
            synchronized (DatabaseHelper.class) {
                if (helper == null)
                    helper = new DatabaseHelper(context);
            }
        }
        return helper;
    }

    /**
     * 获得dao
     * @param clazz
     * @return
     */
    public synchronized Dao getDao(Class clazz) throws SQLException {

        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) { 
            dao = daos.get(className);
        }
        if(dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;

    }

    /**
     * 释放
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao.clearObjectCache();
            dao = null;
        }
    }

}
