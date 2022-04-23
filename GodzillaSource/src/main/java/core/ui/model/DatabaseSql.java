package core.ui.model;

import java.util.HashMap;

public class DatabaseSql {
    public static HashMap<String, String> sqlMap = new HashMap<>();

    static {
        sqlMap.put("mysql-getAllDatabase", "SHOW DATABASES;");
        sqlMap.put("mysql-getTableByDatabase", "SHOW TABLES FROM %s;");
        sqlMap.put("mysql-getTableDataByDT", "SELECT * FROM %s.%s LIMIT 0,10");
        sqlMap.put("mysql-getCountByDT", "SELECT COUNT(1) FROM %s.%s");
        sqlMap.put("sqlserver-getAllDatabase", "SELECT name FROM  master..sysdatabases");
        sqlMap.put("sqlserver-getTableByDatabase", "SELECT name FROM %s..sysobjects  WHERE xtype='U'");
        sqlMap.put("sqlserver-getTableDataByDT", "SELECT TOP 10 * FROM %s.dbo.%s  ");
        sqlMap.put("sqlserver-getCountByDT", "SELECT COUNT(1) FROM %s.dbo.%s");
    }
}
