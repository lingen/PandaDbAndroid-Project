package org.openpanda.android.db.pandadbandroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 2016/10/12.
 */

public class SQLParam {

    private List<Object> paramsList = new ArrayList<>();

    private SQLParam(){

    }

    public static SQLParam createInstance(){
        return new SQLParam();
    }

    public SQLParam addString(String param){
        paramsList.add(param);
        return this;
    }

    public SQLParam addStringArray(String[] params){
        paramsList.add(params);
        return this;
    }

    public Object[] params(){
        return paramsList.toArray(new Object[]{});
    }

}
