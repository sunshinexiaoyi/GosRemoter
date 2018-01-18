package gos.remoter.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wuxy on 2017/7/11.
 */

@DatabaseTable(tableName = "program_list")

public class IndexClass {

    @DatabaseField(columnName = "index", dataType = DataType.INTEGER)
    private int index;

    public IndexClass(){}
    public IndexClass(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
    }

}
