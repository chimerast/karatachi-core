package org.karatachi.db;

public class SqlUtils {
    /**
     * Integer配列をSQLのWHERE ~ IN (...);の形に変換
     */
    public static String toSqlArray(Number... args) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Number arg : args) {
            if (arg != null) {
                builder.append(arg);
            } else {
                builder.append("null");
            }
            builder.append(",");
        }
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(")");
        return builder.toString();
    }
}
