package gos.remoter.data;

import java.util.ArrayList;

/**
 * Created by wuxy on 2017/8/15.
 */

public class EpgProgram extends Program{
    private ArrayList<Date> dateArray = new ArrayList<>();

    public EpgProgram() {
    }

    public ArrayList<Date> getDateArray() {
        return dateArray;
    }

    public void setDateArray(ArrayList<Date> dateArray) {
        this.dateArray = dateArray;
    }
}
