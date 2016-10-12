package org.openpanda.android.db.pandadbandroid;

import java.util.Map;
import java.util.Set;

/**
 * Created by lingen on 2016/10/12.
 */

public class SQLResult {

    private Map<String,Object> value;

    private SQLResult(){
    }

    public static SQLResult createInstance(Map<String,Object> value){
        SQLResult sqlResult = new SQLResult();
        sqlResult.value = value;
        return sqlResult;
    }


    public Object getValue(String key){
        return value.get(key);
    }

    public Set<String> allKeys(){
        return value.keySet();
    }

    public int count(){
        return value.size();
    }
}
