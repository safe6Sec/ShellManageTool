package core.ui.config;

import java.util.HashMap;

public class DatabaseSql {
   public static HashMap<String, String> sqlMap = new HashMap();

   static {
      sqlMap.put("mysql-getAllDatabase", "SHOW DATABASES");
      sqlMap.put("mysql-getTableByDatabase", "SHOW TABLES FROM `{databaseName}`");
      sqlMap.put("mysql-getTableDataByDT", "SELECT * FROM `{databaseName}`.`{tableName}` LIMIT 0,10");
      sqlMap.put("mysql-getCountByDT", "SELECT COUNT(1) FROM `{databaseName}`.`{tableName}`");
      sqlMap.put("sqlserver-getAllDatabase", "SELECT name FROM  master..sysdatabases");
      sqlMap.put("sqlserver-getTableByDatabase", "SELECT name FROM [{databaseName}]..sysobjects  WHERE xtype='U'");
      sqlMap.put("sqlserver-getTableDataByDT", "SELECT TOP 10 * FROM [{databaseName}].dbo.[{tableName}]");
      sqlMap.put("sqlserver-getCountByDT", "SELECT COUNT(1) FROM [{databaseName}].dbo.[{tableName}]");
      sqlMap.put("oracle-getAllDatabase", "SELECT USERNAME FROM ALL_USERS ORDER BY 1");
      sqlMap.put("oracle-getTableByDatabase", "SELECT TABLE_NAME FROM (SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER='%s' ORDER BY 1)");
      sqlMap.put("oracle-getTableDataByDT", "SELECT * FROM \"{databaseName}\".\"{tableName}\" where rownum<=10");
      sqlMap.put("oracle-getCountByDT", "SELECT COUNT(1) FROM \"{databaseName}\".\"{tableName}\"");
      sqlMap.put("postgresql-getAllDatabase", "SELECT datname FROM pg_database where datistemplate='f'");
      sqlMap.put("postgresql-getTableByDatabase", "SELECT table_name FROM information_schema.tables WHERE table_type='BASE TABLE' AND table_schema NOT IN ('pg_catalog', 'information_schema')");
      sqlMap.put("postgresql-getTableDataByDT", "SELECT * FROM \"{tableName}\" limit 10");
      sqlMap.put("postgresql-getCountByDT", "SELECT COUNT(1) FROM \"{tableName}\"");
      sqlMap.put("sqlite-getAllDatabase", "SELECT 'main'");
      sqlMap.put("sqlite-getTableByDatabase", "select tbl_name from sqlite_master where type='table' order by tbl_name");
      sqlMap.put("sqlite-getTableDataByDT", "SELECT * FROM `{tableName}`");
      sqlMap.put("sqlite-getCountByDT", "SELECT COUNT(1) FROM `{tableName}`");
   }
}
