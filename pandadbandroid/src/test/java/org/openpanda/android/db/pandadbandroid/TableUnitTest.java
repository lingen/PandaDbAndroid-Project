package org.openpanda.android.db.pandadbandroid;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 2016/9/30.
 */

public class TableUnitTest {

    @Test
    public void testCreateTableWithoutPrimayColumnsAndWithoutIndexColumns(){

        String tableName = "users_";

        final Column nameColumn = new Column("name_",ColumnType.ColumnText,true);

        final Column ageColumn = new Column("age_",ColumnType.ColumnInt,true);

        final Column weightColumn = new Column("weight_",ColumnType.ColumnReal,true);

        final Column dataColumn = new Column("data_",ColumnType.ColumnBlob,true);

        List<Column> columnList = new ArrayList(){
            {
                add(nameColumn);
                add(ageColumn);
                add(weightColumn);
                add(dataColumn);
            }
        };

        Table table = new Table(tableName,columnList);

        System.out.print(table.createTableSQL());
    }

    @Test
    public void testCreateTableWithPrimayColumnsAndWithoutIndexColumns(){
        String tableName = "users_";

        final Column idColumn = new Column("id_",ColumnType.ColumnInt);

        final Column nameColumn = new Column("name_",ColumnType.ColumnText,true);

        final Column ageColumn = new Column("age_",ColumnType.ColumnInt,true);

        final Column weightColumn = new Column("weight_",ColumnType.ColumnReal,true);

        final Column dataColumn = new Column("data_",ColumnType.ColumnBlob,true);

        List<Column> primaysList = new ArrayList(){
            {
                add(idColumn);
            }
        };

        List<Column> columnList = new ArrayList(){
            {
                add(idColumn);
                add(nameColumn);
                add(ageColumn);
                add(weightColumn);
                add(dataColumn);
            }
        };

        Table table = new Table(tableName,columnList,primaysList);

        System.out.print(table.createTableSQL());

    }

    @Test
    public void testCreateTableWithPrimayAndWithIndexColumns(){
        String tableName = "users_";


        final Column idColumn = new Column("id_",ColumnType.ColumnInt);

        final Column nameColumn = new Column("name_",ColumnType.ColumnText,true);

        final Column ageColumn = new Column("age_",ColumnType.ColumnInt,true);

        final Column weightColumn = new Column("weight_",ColumnType.ColumnReal,true);

        final Column dataColumn = new Column("data_",ColumnType.ColumnBlob,true);

        List<Column> primaysList = new ArrayList(){
            {
                add(idColumn);
            }
        };

        List<Column> columnList = new ArrayList(){
            {
                add(idColumn);
                add(nameColumn);
                add(ageColumn);
                add(weightColumn);
                add(dataColumn);
            }
        };

        List<Column> indexColumns = new ArrayList(){
            {
                add(nameColumn);
            }
        };

        Table table = new Table(tableName,columnList,primaysList,indexColumns);

        System.out.println(table.createTableSQL());

        System.out.println(table.createIndexSQL());

    }
}
