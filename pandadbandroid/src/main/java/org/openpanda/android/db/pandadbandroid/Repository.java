package org.openpanda.android.db.pandadbandroid;

import android.content.Context;
import android.util.Log;

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

        List<Map<String, Object>> queryVersionList = sqLiteManager.executeQuery(QUERY_CURRENT_VERSION);

        int currentVersion = (int) queryVersionList.get(0).get("value_");

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

    public boolean executeUpdate(String sql,String[] params){

        boolean beginTransaction = false;
        if (!sqLiteManager.isInTransaction()){
            beginTransaction = true;
            sqLiteManager.beginTransaction();
        }

        boolean success = sqLiteManager.executeUpdate(sql,params);

        if (beginTransaction){
            sqLiteManager.endTransaction();
        }
        return success;
    }

    public Map<String,Object> executeSingleQuery(String sql){
        return executeSingleQuery(sql,null);
    }

    public Map<String,Object> executeSingleQuery(String sql,String[] params){
        List<Map<String,Object>> results = executeQuery(sql,params);
        if (results!=null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    public List<Map<String,Object>> executeQuery(String sql){
        return executeQuery(sql,null);
    }

    public List<Map<String,Object>> executeQuery(final String sql,final String[] params){

        return inTransactionBlock(new InTransactionWrap() {
            @Override
            public Object executeInTransaction() {
                List<Map<String,Object>> results = sqLiteManager.executeQuery(sql,params);
                return results;
            }
        },List.class);
    }


    public boolean executeInTransaction(final RepositoryBlock block){
        return inTransactionBlock(new InTransactionWrap() {
            @Override
            public Object executeInTransaction() {
                return block.execute();
            }
        },Boolean.class);
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