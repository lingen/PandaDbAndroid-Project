package org.openpanda.android.db.pandadbandroid_project;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 2016/10/12.
 */
@RunWith(AndroidJUnit4.class)
public class RepositoryTest {


    @Test
    public void test(){
        String sql =  "select * from user_ where name_ in (?) and widht = ? and name in (?)";

        Object[] returnValue = filterSQL(sql,new Object[]{new String[]{"a","b","c"},123,new String[]{"1","2"} });

        System.out.print("123");
    }

    private Object[] filterSQL(String sql,Object[] params){
        List<String> paramsList = new ArrayList<>();
        Object[] returnValue = new Object[2];
        int index = 0;
        for (int i = 0;i<params.length; i++){
            Object value = params[i];
            String stringValue = null;

            if (value instanceof Object[]){
                Object[] arraysValue = (Object[])value;
                int count = arraysValue.length;

                int findIndex = findParamIndex(sql,index);
                String replaceBefore = sql.substring(0,findIndex);
                StringBuffer appendAsk = new StringBuffer();
                for(int j = 1;j<count;j++){
                    appendAsk.append("?,");
                }

                String replaceAfter = replaceBefore + appendAsk.toString();

                sql = sql.replace(replaceBefore,replaceAfter);

                index += count;

                for (Object arrayValue:arraysValue){
                    paramsList.add(arrayValue.toString());
                }
            }
            else if (value instanceof Object){
                stringValue = value.toString();
                index += 1;
                paramsList.add(stringValue);
            }
        }

        returnValue[0] = sql;
        returnValue[1] = paramsList.toArray(new String[]{});

        return returnValue;
    }


    private int findParamIndex(String sql,int i){
        int begin = -1;
        int from = 0;
        while(begin < i){
            from = sql.indexOf("?",from + 1);
            begin ++ ;
        }
        return from;
    }


}
