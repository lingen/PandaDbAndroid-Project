package org.openpanda.android.db.pandadbandroid;

import android.content.Context;
import android.util.Log;
import android.util.StringBuilderPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lingen on 2016/9/30.
 */

public class Repository {


    private static String CREATE_VERSION_TABLE = "create table if not exists panda_version_ (value_ int not null)";

    private static String INIT_VERSION_TABLE_CONTENT = "insert into panda_version_ (value_) values (:value)";

    private static String QUERY_CURRENT_VERSION =  "SELECT VALUE_ FROM PANDA_VERSION_ LIMIT 1";

    private static String UPDATE_VERSION = "UPDATE PANDA_VERSION_ SET VALUE_ = :value";

    private static String DB_THREAD_MARK = "PANDA DB ANDROID THREAD";

    private int version;

    private String dbName;

    private List<TableCreate> createList;

    private TableUpdate tableUpdate;

    private Context context;

    private SQLiteManager sqLiteManager;


    private Repository(Context context,String dbName,int dbVersion,List<TableCreate> creates,TableUpdate tableUpdate){
        this.context = context;
        this.dbName = dbName;
        this.version = dbVersion;
        this.createList = creates;
        this.tableUpdate = tableUpdate;
        this.sqLiteManager = SQLiteManager.createInstance(context,dbName);

        initOrUpdateRepository();
    }

    public static Repository createInstance(Context context,String dbName,int dbVersion,List<TableCreate> creates,TableUpdate tableUpdate){
        Repository repository = new Repository(context,dbName,dbVersion,creates,tableUpdate);
        return repository;
    }


    private void initOrUpdateRepository(){
        boolean exists = tableExists("PANDA_VERSION_");
        if (exists){
            updateRepository();
        }else{
            initRepository();
        }
    }

    private void initRepository(){

        sqLiteManager.beginTransaction();

        boolean success = sqLiteManager.executeUpdate(CREATE_VERSION_TABLE);
        if (success){
            Log.d(this.getClass().getName(),"创建版本号表成功");
        }else{
            Log.e(this.getClass().getName(),"创建版本号表失败");
        }

        success = sqLiteManager.executeUpdate(INIT_VERSION_TABLE_CONTENT,new String[]{String.valueOf(version)});

        if (success){
            Log.d(this.getClass().getName(),"更新版本号成功");
        }else{
            Log.e(this.getClass().getName(),"更新版本号失败");
        }

        StringBuffer createTableSQLs = new StringBuffer();

        if (this.createList != null){
            for (TableCreate tableCreate:createList){
                Table table = tableCreate.createTable();

                createTableSQLs.append(table.createTableSQL());
                createTableSQLs.append(table.createIndexSQL());

            }

            success = sqLiteManager.executeUpdate(createTableSQLs.toString());
            if (success){
                Log.d(this.getClass().getName(),"建表成功");
            }else{
                Log.e(this.getClass().getName(),"建表失败");
            }
        }

        sqLiteManager.endTransaction();

    }

    private void updateRepository(){
        if (this.tableUpdate == null){
            return;
        }

        sqLiteManager.beginTransaction();

        List<SQLResult> queryVersionList = sqLiteManager.executeQuery(QUERY_CURRENT_VERSION);

        int currentVersion = (int) queryVersionList.get(0).getValue("value_");

        StringBuffer sqls = new StringBuffer();

        for (int i=currentVersion;i<version;i++){
            String updateSql = tableUpdate.updateTable(i,i+1);
            if (updateSql!=null && updateSql.trim().length()>0){
                sqls.append(updateSql);
            }
        }
        boolean success;
        if (!sqls.toString().trim().equals("")){
            success = sqLiteManager.executeUpdate(sqls.toString());
            if (!success){
                Log.e(this.getClass().getName(),"数据库升级脚本发生错误");
                throw new RuntimeException("数据库升级脚本发生错误");
            }
        }

        success = sqLiteManager.executeUpdate(UPDATE_VERSION,new String[]{String.valueOf(version)});

        if (success){
            Log.d(this.getClass().getName(),"数据库版本升级成功");
        }else{
            Log.e(this.getClass().getName(),"数据库版本升级失败");
        }

        sqLiteManager.endTransaction();
    }

    public boolean tableExists(String tableName){
        String querySQL =  "SELECT * FROM sqlite_master WHERE type='table' AND name = ? COLLATE NOCASE";
        List results = sqLiteManager.executeQuery(querySQL,new String[]{tableName});
        if (results != null && results.size() > 0 ){
            return true;
        }
        return false;
    }

    public boolean executeUpdate(String sql){
        return executeUpdate(sql,null);
    }

    public boolean executeUpdate(String sql,SQLParam sqlParam){

        boolean beginTransaction = false;
        if (!sqLiteManager.isInTransaction()){
            beginTransaction = true;
            sqLiteManager.beginTransaction();
        }
        Object[] returnValues = filterSQL(sql,sqlParam.params());

        String filterSQL = (String) returnValues[0];
        String[] filterParams = (String[])returnValues[1];

        boolean success = sqLiteManager.executeUpdate(filterSQL,filterParams);

        if (beginTransaction){
            sqLiteManager.endTransaction();
        }
        return success;
    }

    public SQLResult executeSingleQuery(String sql){
        return executeSingleQuery(sql,SQLParam.createInstance());
    }

    public SQLResult executeSingleQuery(String sql,SQLParam sqlParam){

        List<SQLResult> results = executeQuery(sql,sqlParam);
        if (results!=null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    public List<SQLResult> executeQuery(String sql){
        return executeQuery(sql,SQLParam.createInstance());
    }

    public List<SQLResult> executeQuery(final String sql,final SQLParam sqlParam){

        return inTransactionBlock(new InTransactionWrap() {
            @Override
            public Object executeInTransaction() {

                Object[] returnValues = filterSQL(sql,sqlParam.params());

                String filterSQL = (String) returnValues[0];
                String[] filterParams = (String[])returnValues[1];


                List<SQLResult> results = sqLiteManager.executeQuery(filterSQL,filterParams);
                return results;
            }
        },List.class);
    }


    public boolean executeInTransaction(final TransactionBlock block){
        return inTransactionBlock(new InTransactionWrap() {
            @Override
            public Object executeInTransaction() {
                return block.execute();
            }
        },Boolean.class);
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

    private <T> T inTransactionBlock(InTransactionWrap inTransactionWrap,Class<T> c){
        boolean beginTransaction = false;
        if (!sqLiteManager.isInTransaction()){
            beginTransaction = true;
            sqLiteManager.beginTransaction();
        }

        Object object = inTransactionWrap.executeInTransaction();

        if (beginTransaction){
            sqLiteManager.endTransaction();
        }

        return (T)object;
    }

    interface InTransactionWrap{
        Object executeInTransaction();
    }


}