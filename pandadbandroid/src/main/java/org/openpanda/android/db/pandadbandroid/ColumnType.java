package org.openpanda.android.db.pandadbandroid;

/**
 * Created by lingen on 2016/9/28.
 * 列类型
 */

public enum ColumnType {
    /**
     * 代表一个TEXT列
     */
    ColumnText {
        @Override
        String columnString() {
            return "TEXT";
        }
    },

    /**
     * 代表一个INT列
     */
    ColumnInt {
        @Override
        String columnString() {
            return "INT";
        }
    },

    /**
     * 代表一个Blob列
     */
    ColumnBlob {
        @Override
        String columnString() {
            return "BLOB";
        }
    },

    /**
     * 代表一个Real列
     */
    ColumnReal {
        @Override
        String columnString() {
            return "REAL";
        }
    };


    abstract String columnString();
}
