package gos.remoter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import gos.remoter.data.Program;

/**
 * Created by lp on 2018/1/15.
 */

public class DBAdapter {

    private static final String TAG = "DBAdapter";

    public static final String KEY_ID = "_id";

    public static final String KEY_NAME = "name";

    public static final String KEY_LCN = "lcn";

    public static final String KEY_SERVICEID = "serviceId";

    public static final String KEY_TYPE = "type";

    public static final String KEY_ISFAVOR = "isFavor";
    //数据库名称
    private static final String DATABASE_NAME = "program.db";
    //数据表名,一个数据库可以创建多个表
    private static final String TABLE_NAME = "programList";
    //数据库版本号
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table programList (_id integer primary key autoincrement, "
            + "name text not null, "
            +" lcn integer not null, "
            +" serviceId integer not null, "
            +" type integer not null, "
            + "isFavor Boolean not null);";

    private Context context;
    private ProgramSQLiteOpenHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        DBHelper = new ProgramSQLiteOpenHelper(context);
    }

    /**
     * SQLiteOpenHelper 是一个辅助类，用来管理数据库的创建和版本
     */
    private class ProgramSQLiteOpenHelper extends SQLiteOpenHelper {

        private ProgramSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //初始化数据库的表结构
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS programList");
            onCreate(db);
        }
    }

    //---打开数据库，抛出异常，防止出错---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---关闭数据库---
    public void close() {
        DBHelper.close();
    }

    //---向数据库中插入一条音乐信息---
    public long insertInfo(String name, int lcn, int serviceId, int type, boolean isFavor) {
        ContentValues initialValues = new ContentValues();//存储键/值对

        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LCN, lcn);
        initialValues.put(KEY_SERVICEID, serviceId);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_ISFAVOR, isFavor);
        return db.insert(TABLE_NAME, null, initialValues);
    }

    //---删除一个指定---
    public boolean deleteInfo(long rowId) {
        return db.delete(TABLE_NAME, KEY_ID + "=" + rowId, null) > 0;
    }

    //---删除一个指定---
    public boolean deleteInfo(String name) {
        return db.delete(TABLE_NAME, KEY_NAME + "=" + name, null) > 0;/////
    }

    //--删除所有数据--
    public void deleteAllInfo() {
        Cursor old_cursor = db.query(TABLE_NAME, new String[] {
                KEY_ID,KEY_NAME,KEY_LCN,KEY_SERVICEID,
                KEY_TYPE,KEY_ISFAVOR},null,null,null,null,null);
        if (old_cursor.moveToFirst())
        {
            do {
                long program_id = old_cursor.getLong(0);
                //执行删除单个数据SQL语句
                db.delete(TABLE_NAME, KEY_ID + "=" + program_id, null);
            } while (old_cursor.moveToNext());
        }
    }

    //---检索所有信息，查询---
    public Cursor getAllInfo() {
        db = DBHelper.getReadableDatabase();
        return db.query(TABLE_NAME, new String[] {
                KEY_ID,KEY_NAME,KEY_LCN,KEY_SERVICEID,
                KEY_TYPE,KEY_ISFAVOR},null,null,null,null,null);
    }

    //---检索一个指定行的数据---
    public Cursor getTitle(long rowId) throws SQLException
    {
        db = DBHelper.getReadableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME, new String[] {
                        KEY_ID,KEY_NAME,KEY_LCN,KEY_SERVICEID,
                        KEY_TYPE,KEY_ISFAVOR},
                KEY_ID + "=" + rowId,null,null,null,null,null);/////
        if (mCursor != null) {
            mCursor.moveToFirst();
            mCursor.close();
        }
        return mCursor;//相当于结果集
    }

    //---检索一个指定标题---
    public Cursor getTitle(String name) throws SQLException {
        db = DBHelper.getReadableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME, new String[] {
                        KEY_ID,KEY_NAME,KEY_LCN,KEY_SERVICEID,
                        KEY_TYPE,KEY_ISFAVOR},
                KEY_NAME + "=" + name,null,null,null,null,null);////
        if (mCursor != null) {
            mCursor.moveToFirst();
            mCursor.close();
        }
        return mCursor;
    }

    //---更新修改， name---
    public boolean updateInfo(String name, int lcn, int serviceId, int type, boolean isFavor)
    {
        ContentValues args = new ContentValues();

        args.put(KEY_NAME, name);
        args.put(KEY_LCN, lcn);
        args.put(KEY_SERVICEID, serviceId);
        args.put(KEY_TYPE, type);
        args.put(KEY_ISFAVOR, isFavor);
        return db.update(TABLE_NAME, args,KEY_NAME + "=" + name, null) > 0;//////
    }

    //---更新,位置---
    public boolean updateInfo(long rowId, String name, int lcn, int serviceId, int type, boolean isFavor)
    {
        ContentValues args = new ContentValues();

        args.put(KEY_ID, rowId);
        args.put(KEY_NAME, name);
        args.put(KEY_LCN, lcn);
        args.put(KEY_SERVICEID, serviceId);
        args.put(KEY_TYPE, type);
        args.put(KEY_ISFAVOR, isFavor);
        return db.update(TABLE_NAME, args,KEY_ID + "=" + rowId, null) > 0;//////
    }

    //根据id来查询数据库，比如查询id为6的数据库内容并返回
    public Program find(Integer id){
        //如果只对数据进行读取，建议使用此方法
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        //得到游标
        Cursor cursor = db.rawQuery("select * from programList where _id=?", new String[]{id.toString()});
        if(cursor.moveToFirst()){
            boolean isFavor = false;
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            int lcn = cursor.getInt(cursor.getColumnIndex(KEY_LCN));
            int serviceId = cursor.getInt(cursor.getColumnIndex(KEY_SERVICEID));
            int type = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));

            if(cursor.getInt(cursor.getColumnIndex(KEY_ISFAVOR)) == 1) {
                isFavor = true;
            } else {
                isFavor = false;
            }
            Program program = new Program();
            program.setName(name);
            program.setLcn(lcn);
            program.setServiceId(serviceId);
            program.setType(type);
            program.setFavor(isFavor);
            return program;
        }
        return null;
    }

    public void save(Program program){
        //如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
        db.execSQL("insert into musiclist (name,sname,url,duration,size,album,favorite,word_url,album_url) values(?,?,?,?,?,?,?,?,?)",
                new Object[]{program.getName(),program.getLcn(),program.getServiceId(),
                        program.getType(),program.getFavor()});
    }

    public void clearFeedTable(){
        //DBHelper.onUpgrade(db, 1, 2);
        db.execSQL("DROP TABLE IF EXISTS musiclist");
        DBHelper.onCreate(db);
    }





}
