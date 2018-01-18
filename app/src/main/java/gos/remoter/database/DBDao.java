package gos.remoter.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 用户信息操作类
 * 定义数据访问对象，对指定的表进行增删改查操作
 */

public class DBDao<T> {

    private Context context;
    private Dao<T, Integer> DBDaoOpe;//每张表对应一个
    private DatabaseHelper helper;

    /**
     * 获得数据库帮助类实例，通过传入Class对象得到相应的Dao
     * @param context
     * @param clazz
     */
    public DBDao(Context context, Class<T> clazz) {

        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
            DBDaoOpe = helper.getDao(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /**
     * 添加数据
     *
     * @param data
     */

    public void add(T data) {
        try {
            DBDaoOpe.create(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
     * 添加列表数据
     *
     * @param datas
*/

    public void addlist(List<T> datas) {
        try {
            for (T data : datas) {
                DBDaoOpe.create(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id查询用户
     *
     * @param id
     */

    public T qryUserById(int id) {
        T data = null;
        try {
            data = DBDaoOpe.queryForId(id);
            //DBDaoOpe.refresh(data.getUser());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

/**
     * 查询全部数据
     *
     * @return
 */

    public List<T> qryAllInfo() {
        List<T> datas = new ArrayList<T>();
        try {
            datas = DBDaoOpe.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * 删除全部
     */
    public void deleteAll() {
        try {
            DBDaoOpe.delete(qryAllInfo());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInfo(int id) {
        try {
            DBDaoOpe.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新一条记录
     *
     */
    public void updateInfo(T data) {
        try {

            DBDaoOpe.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAllInfo(ArrayList<T> datas) {

        try {
            for(T data :datas) {
                DBDaoOpe.update(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
