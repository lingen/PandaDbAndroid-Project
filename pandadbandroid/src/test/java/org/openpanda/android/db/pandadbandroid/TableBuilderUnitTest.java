package org.openpanda.android.db.pandadbandroid;

import org.junit.Test;

/**
 * Created by lingen on 2016/9/30.
 */

public class TableBuilderUnitTest {

    @Test
    public void testCreateTableWithoutPrimayColumnsAndWithoutIndexColumns() {

        Table table = TableBuilder.createInstance("user_")
                .textColumn("name_")
                .intColumn("age_")
                .realColumn("weight_")
                .blobColumn("data_")
                .builder();

        System.out.print(table.createTableSQL());

    }

    @Test
    public void testCreateTableWithPrimayColumnsAndWithoutIndexColumns() {

        Table table = TableBuilder.createInstance("user_")
                .primaryColumn("id_", ColumnType.ColumnInt)
                .textColumn("name_")
                .intColumn("age_")
                .realColumn("weight_")
                .blobColumn("data_")
                .builder();

        System.out.print(table.createTableSQL());

    }


    @Test
    public void testCreateTableWithPrimayAndWithIndexColumns() {
        Table table = TableBuilder.createInstance("user_")
                .primaryColumn("id_", ColumnType.ColumnInt)
                .indexColumn("name_",ColumnType.ColumnText,false)
                .intColumn("age_")
                .realColumn("weight_")
                .blobColumn("data_")
                .builder();

        System.out.print(table.createTableSQL());
    }

}
