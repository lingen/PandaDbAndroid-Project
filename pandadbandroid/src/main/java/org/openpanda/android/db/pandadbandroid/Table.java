package org.openpanda.android.db.pandadbandroid;
import java.util.List;


/**
 * Created by lingen on 2016/9/28.
 */

public class Table {

    public static String OPF_ID = "OPF_ID_";


    private String tableName;

    private List<Column> columns;

    private List<Column> primaryColumns;

    private List<Column> indexColumns;


    public Table(String tableName,List<Column> columns,List<Column> primaryColumns,List<Column> indexColumns){
        this.tableName = tableName;

        this.columns = columns;

        this.primaryColumns = primaryColumns;

        this.indexColumns = indexColumns;
    }

    public Table(String tableName,List<Column> columns,List<Column> primaryColumns){
        this(tableName,columns,primaryColumns,null);
    }

    public Table(String tableName,List<Column> columns){
        this(tableName,columns,null,null);
    }


    public String createTableSQL(){
        StringBuffer sqls = new StringBuffer();
        sqls.append("create table if not exists ");
        sqls.append(this.tableName);
        sqls.append(" (");

        for (Column column:columns) {
            sqls.append(column.columnCreateSQL());
        }

        sqls.append(primaryKeySQL());

        sqls.append(" );");
        return sqls.toString();
    }

    public String createIndexSQL(){
        StringBuffer sqls = new StringBuffer();

        if (indexColumns != null && indexColumns.size() > 0 ){
            for (Column column:indexColumns){
                sqls.append("CREATE INDEX index_" + column.getName() + " ON " +this.tableName + "("+column.getName()+")");
            }
        }

        return sqls.toString();
    }

    private String primaryKeySQL() {
        StringBuffer sqls = new StringBuffer();

        if (primaryColumns != null && primaryColumns.size() > 0){
            sqls.append("PRIMARY KEY(");


            for(int i=0;i<primaryColumns.size();i++){
                Column column = primaryColumns.get(i);
                sqls.append(column.getName());
                if (i != primaryColumns.size() -1 ){
                    sqls.append(",");
                }
            }
            sqls.append(")");
        }else{
            sqls.append(OPF_ID + " integer PRIMARY KEY autoincrement");
        }

        return sqls.toString();
    }
}
