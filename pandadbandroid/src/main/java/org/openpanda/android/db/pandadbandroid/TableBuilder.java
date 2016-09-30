package org.openpanda.android.db.pandadbandroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 2016/9/30.
 */

public class TableBuilder {

    private  String tableName;

    private List<Column> columns = new ArrayList<>();

    private List<Column> primayKeyColumns = new ArrayList<>();

    private List<Column> indexColumns = new ArrayList<>();

    private TableBuilder(String tableName){
        this.tableName = tableName;
    }

    public static TableBuilder createInstance(String tableName){
        TableBuilder tableBuilder = new TableBuilder(tableName);
        return tableBuilder;
    }

    public TableBuilder column(String columnName,ColumnType type,Boolean nullable){
        Column column = new Column(columnName,type,nullable);
        columns.add(column);
        return this;
    }

    public TableBuilder primaryColumn(String columnName,ColumnType type){
        Column column = new Column(columnName,type,false);
        columns.add(column);
        primayKeyColumns.add(column);
        return this;
    }

    public TableBuilder indexColumn(String columnName,ColumnType type,Boolean nullable){
        Column column = new Column(columnName,type,nullable);
        columns.add(column);
        indexColumns.add(column);
        return this;
    }

    public TableBuilder textColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnText,true);
        columns.add(column);
        return this;
    }

    public TableBuilder textNotNullColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnText,false);
        columns.add(column);
        return this;
    }

    public TableBuilder intColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnInt,true);
        columns.add(column);
        return this;
    }

    public TableBuilder intNotNullColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnInt,false);
        columns.add(column);
        return this;
    }

    public TableBuilder realColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnReal,true);
        columns.add(column);
        return this;
    }

    public TableBuilder realNotNullColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnReal,false);
        columns.add(column);
        return this;
    }

    public TableBuilder blobColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnBlob,true);
        columns.add(column);
        return this;
    }

    public TableBuilder blobNotNullColumn(String columnName){
        Column column = new Column(columnName,ColumnType.ColumnBlob,false);
        columns.add(column);
        return this;
    }

    public Table builder(){
        Table table = new Table(tableName,columns,primayKeyColumns,indexColumns);
        return table;
    }
}

