package org.openpanda.android.db.pandadbandroid_project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpanda.android.db.pandadbandroid.Repository;
import org.openpanda.android.db.pandadbandroid.SQLParam;
import org.openpanda.android.db.pandadbandroid.SQLiteManager;
import org.openpanda.android.db.pandadbandroid.Table;
import org.openpanda.android.db.pandadbandroid.TableBuilder;
import org.openpanda.android.db.pandadbandroid.TableCreate;
import org.openpanda.android.db.pandadbandroid.TableUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();


        assertEquals("org.openpanda.android.db.pandadbandroid_project", appContext.getPackageName());
    }


    @Test
    public void test1() {
        Context appContext = InstrumentationRegistry.getTargetContext();


        SQLiteManager sqLiteManager = SQLiteManager.createInstance(appContext,"abc");

        List<Map<String, Object>> results =  sqLiteManager.executeQuery("select * from user");

        System.out.println(results);

    }

    @Test
    public void testRepository(){
        Context appContext = InstrumentationRegistry.getTargetContext();


        TableCreate tableCreate = new TableCreate() {
            @Override
            public Table createTable() {
                Table table = TableBuilder.createInstance("user_").textColumn("name_")
                        .intColumn("age_")
                        .realColumn("weight_")
                        .blobColumn("data_")
                        .builder();

                return table;
            }
        };


        List<TableCreate> tableCreates = new ArrayList<>();
        tableCreates.add(tableCreate);

        //升级
        TableUpdate tableUpdate = new TableUpdate() {
            @Override
            public String updateTable(int from, int to) {
                if (from == 1 && to == 2){
                    System.out.println("AAA");
                    Log.e("AAA","你调用我了啊");
                }
                return null;
            }
        };

        final Repository repository = Repository.createInstance(appContext,"abc",2, tableCreates,tableUpdate);

        boolean tableExists = repository.tableExists("user_");

        assertTrue(tableExists);


        //插入测试
        String sql = "insert into user_ (name_,age_,weight_,data_) values (?,?,?,?)";

        SQLParam sqlParam = SQLParam.createInstance()
                .addString("lingen")
                .addString("123")
                .addString("12.12")
                .addString("123");


        boolean success = repository.executeUpdate(sql,sqlParam);

        assertTrue(success);



//        //批量插入
//        repository.executeInTransaction(new TransactionBlock() {
//            @Override
//            public boolean execute() {
//                long begin = System.currentTimeMillis();
//                for (int i =0 ;i<10;i++){
//                    insertOne(repository);
//                }
//                long end = (System.currentTimeMillis() - begin);
//                Log.e("TIME",String.valueOf(end));
//                return true;
//            }
//        });

        //带参数的查询
        //查询测试
        List<Map<String,Object>> results = repository.executeQuery("select * from user_");
        assertTrue(results.size() > 0);

        sqlParam = SQLParam.createInstance().addStringArray(new String[]{"lingen1","lingen"});

        List<Map<String,Object>> queryReusts = repository.executeQuery("select * from user_ where name_ in (?)",sqlParam);

        assertTrue(queryReusts.size() > 0);
    }

    private void insertOne(Repository repository){
        String sql = "insert into user_ (name_,age_,weight_,data_) values (?,?,?,?)";


        SQLParam sqlParam = SQLParam.createInstance().addString("lingen1")
                .addString("123")
                .addString("123.12")
                .addString("123");

        repository.executeUpdate(sql,sqlParam);
    }
}



