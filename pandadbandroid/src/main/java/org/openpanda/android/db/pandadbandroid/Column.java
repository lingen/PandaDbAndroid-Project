package org.openpanda.android.db.pandadbandroid;

import java.io.Serializable;

/**
 * Created by lingen on 2016/9/28.
 * 代表一列
 */

public class Column extends Object implements Serializable{


    private static String NOT_NULL = "not null ";


    /**
     * 数据库名
     */
    private String name;


    /**
     * 是否可以为空
     */
    private boolean nullable = true;


    /**
     * 列类型
     */
    private ColumnType type;


    /**
     * 传入一个列名,默认生成一个TEXT 可以为空的列类型
     * @param name
     */
    public Column(String name){
        this(name,ColumnType.ColumnText,true);
    }

    /**
     * 传入一个列名及一个列类型,允许为空
     * @param name
     * @param type
     */
    public Column(String name,ColumnType type){
        this(name,type,true);
    }

    /**
     * 传入列名,列类型,以及是否可以为空的定义
     * @param name
     * @param type
     * @param nullable
     */
    public Column(String name,ColumnType type,boolean nullable){
        this.name = name;
        this.type = type;
        this.nullable = nullable;
    }


    public String columnCreateSQL(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(name+" ");
        stringBuffer.append(type.columnString());
        if (!nullable){
            stringBuffer.append(NOT_NULL);
        }
        stringBuffer.append(" , " );
        return stringBuffer.toString();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }
}
