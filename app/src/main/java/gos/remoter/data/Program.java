package gos.remoter.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by wuxy on 2017/7/7.
 */

@DatabaseTable(tableName = "program_list")

public class Program extends IndexClass implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name", dataType = DataType.STRING)
    private String name;

//    @DatabaseField(columnName = "lcn", dataType = DataType.INTEGER)
    private int lcn;

//    @DatabaseField(columnName = "serviceId", dataType = DataType.INTEGER)
    private int serviceId;

//    @DatabaseField(columnName = "type", dataType = DataType.INTEGER)
    private int type;   //节目类型

    @DatabaseField(columnName = "isFavor", dataType = DataType.BOOLEAN)
    private boolean isFavor;

    private boolean isSelect;

    public Program(){
        this("",-1);
    }
    public Program(String name,int index){
        this(name,index,-1,-1);
    }
    public Program(String name,int index,int lcn,int serviceId){
        this(name,index,lcn,serviceId,0, false, false);
    }
    public Program(String name,int index, int lcn, int serviceId, int type , boolean isFavor, boolean isSelect) {
        super(index);
        this.name = name;
        this.lcn = lcn;
        this.serviceId = serviceId;
        this.type = type;
        this.isFavor = isFavor;
        this.isSelect = isSelect;

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLcn() {
        return lcn;
    }

    public void setLcn(int lcn) {
        this.lcn = lcn;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean getFavor() {
        return isFavor;
    }

    public void setFavor(boolean favor) {
        isFavor = favor;
    }

    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "Program [id=" + id + ", name=" + name +  ", isFavor=" + isFavor + ", isSelect=" + isSelect +"]";
        //", lcn=" + lcn + ", serviceId=" + serviceId + ", type=" + type +
    }
}
