package org.openpanda.android.db.pandadbandroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingen on 2016/9/30.
 */

public class SQLiteManager {

    private int DB_VERSION = 1;

    private SQLiteOpenHelper sqLiteOpenHelper;

    private String dbName;

    private boolean inTransaction;

    private Context context;

    private SQLiteManager(String dbName, Context context) {

        this.dbName = dbName;
        this.context = context;

        this.sqLiteOpenHelper = new SQLiteOpenHelper(context, dbName, null, DB_VERSION) {

            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                System.out.println("Android系统自带的创建SQL,忽略");
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
                System.out.println("Android系统自带的升级机制,忽略");

            }
        };
    }


    public void beginTransaction(){
        sqLiteOpenHelper.getWritableDatabase().beginTransaction();
        inTransaction = true;
    }

    public void endTransaction(){
        sqLiteOpenHelper.getWritableDatabase().setTransactionSuccessful();
        sqLiteOpenHelper.getWritableDatabase().endTransaction();
        inTransaction = false;
    }

    public void rollbackTransaction(){
        sqLiteOpenHelper.getWritableDatabase().endTransaction();
        inTransaction = false;
    }

    public static SQLiteManager createInstance(Context context,String dbName) {
        SQLiteManager sqLiteManager = new SQLiteManager(dbName, context);
        return sqLiteManager;
    }


    public boolean executeUpdate(String sql) {
        try {
            this.sqLiteOpenHelper.getWritableDatabase().execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean executeUpdate(String sql, Object[] params) {
        try {
            this.sqLiteOpenHelper.getWritableDatabase().execSQL(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public List<Map<String, Object>> executeQuery(String sql) {
        return executeQuery(sql,null);
    }

    public List<Map<String, Object>> executeQuery(String sql,String[] params) {

        try{
            Cursor cursor = sqLiteOpenHelper.getReadableDatabase().rawQuery(sql, params);

            List<Map<String, Object>> results = new ArrayList<>();

            while (cursor.moveToNext()) {
                Map<String, Object> result = new HashMap<>();
                String[] names = cursor.getColumnNames();
                for (String name : names) {
                    int index = cursor.getColumnIndex(name);
                    int type = cursor.getType(index);

                    switch (type) {
                        case Cursor.FIELD_TYPE_BLOB: {
                            result.put(name, cursor.getBlob(index));
                            break;
                        }
                        case Cursor.FIELD_TYPE_FLOAT: {
                            result.put(name, cursor.getFloat(index));
                            break;
                        }
                        case Cursor.FIELD_TYPE_INTEGER: {
                            result.put(name, cursor.getInt(index));
                            break;
                        }
                        case Cursor.FIELD_TYPE_STRING: {
                            result.put(name, cursor.getString(index));
                            break;
                        }
                        default: {
                            result.put(name, cursor.getString(index));
                            break;
                        }
                    }
                }
                results.add(result);
            }
            return results;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }


    }

    public boolean isInTransaction() {
        return inTransaction;
    }
}