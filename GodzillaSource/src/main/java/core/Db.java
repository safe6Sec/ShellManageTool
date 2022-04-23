package core;

import core.shell.ShellEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;
import util.Log;
import util.functions;

public class Db {
    private static final String CREATE_PLUGIN_TABLE = "CREATE TABLE plugin (pluginJarFile TEXT NOT NULL,PRIMARY KEY (\"pluginJarFile\"))";
    private static final String CREATE_SETING_TABLE = "CREATE TABLE seting (\"key\" TEXT NOT NULL,\"value\" TEXT NOT NULL,PRIMARY KEY (\"key\"))";
    private static final String CREATE_SHELL_TABLE = "CREATE TABLE \"shell\" ( \"id\" text NOT NULL,  \"url\" TEXT NOT NULL,  \"password\" TEXT NOT NULL,  \"secretKey\" TEXT NOT NULL,  \"payload\" TEXT NOT NULL,  \"cryption\" TEXT NOT NULL,  \"encoding\" TEXT NOT NULL,  \"headers\" TEXT NOT NULL,  \"reqLeft\" TEXT NOT NULL,  \"reqRight\" TEXT NOT NULL,  \"connTimeout\" integer NOT NULL,  \"readTimeout\" integer NOT NULL,  \"proxyType\" TEXT NOT NULL,  \"proxyHost\" TEXT NOT NULL,  \"proxyPort\" TEXT NOT NULL,  \"remark\" TEXT NOT NULL,  \"note\" TEXT NOT NULL,  \"createTime\" TEXT NOT NULL,  \"updateTime\" text NOT NULL,  PRIMARY KEY (\"id\"))";
    private static final String DB_URL = "jdbc:sqlite:data.db";
    private static final String Drivde = "org.sqlite.JDBC";
    private static Connection dbConn;

    static {
        try {
            Class.forName(Drivde);
            dbConn = DriverManager.getConnection(DB_URL);
            if (!tableExists("shell")) {
                dbConn.createStatement().execute(CREATE_SHELL_TABLE);
            }
            if (!tableExists("plugin")) {
                dbConn.createStatement().execute(CREATE_PLUGIN_TABLE);
            }
            if (!tableExists("seting")) {
                dbConn.createStatement().execute(CREATE_SETING_TABLE);
            }
            dbConn.setAutoCommit(true);
            functions.addShutdownHook(Db.class, null);
            if (getSetingValue("AppIsTip") == null) {
                updateSetingKV("AppIsTip", Boolean.toString(true));
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public static boolean tableExists(String tableName) {
        boolean ret = false;
        try {
            PreparedStatement preparedStatement = getPreparedStatement("SELECT COUNT(1) as result FROM sqlite_master WHERE name=?");
            preparedStatement.setString(1, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (resultSet.getInt("result") == 1) {
                ret = true;
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            Log.error(e);
        }
        return ret;
    }

    public static synchronized Vector<Vector<String>> getAllShell() {
        Vector<Vector<String>> rows;
        synchronized (Db.class) {
            rows = new Vector<>();
            try {
                Statement statement = getStatement();
                ResultSet resultSet = statement.executeQuery("SELECT id,url,payload,cryption,encoding,proxyType,remark,createTime,updateTime FROM shell");
                Vector<String> columns = getAllcolumn(resultSet.getMetaData());
                rows.add(columns);
                while (resultSet.next()) {
                    Vector<String> rowVector = new Vector<>();
                    for (int i = 0; i < columns.size(); i++) {
                        rowVector.add(resultSet.getString(i + 1));
                    }
                    rows.add(rowVector);
                }
                resultSet.close();
                statement.close();
            } catch (Exception e) {
                Log.error(e);
                rows = null;
            }
        }
        return rows;
    }

    public static synchronized ShellEntity getOneShell(String id) {
        ShellEntity context;
        synchronized (Db.class) {
            try {
                PreparedStatement preparedStatement = getPreparedStatement("SELECT id,url,password,secretKey,payload,cryption,encoding,headers,reqLeft,reqRight,connTimeout,readTimeout,proxyType,proxyHost,proxyPort,remark FROM SHELL WHERE id = ?");
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    context = new ShellEntity();
                    context.setId(resultSet.getString("id"));
                    context.setUrl(resultSet.getString("url"));
                    context.setPassword(resultSet.getString("password"));
                    context.setPayload(resultSet.getString("payload"));
                    context.setSecretKey(resultSet.getString("secretKey"));
                    context.setCryption(resultSet.getString("cryption"));
                    context.setEncoding(resultSet.getString("encoding"));
                    context.setRemark(resultSet.getString("remark"));
                    context.setHeader(resultSet.getString("headers"));
                    context.setReqLeft(resultSet.getString("reqLeft"));
                    context.setReqRight(resultSet.getString("reqRight"));
                    context.setConnTimeout(resultSet.getInt("connTimeout"));
                    context.setReadTimeout(resultSet.getInt("readTimeout"));
                    context.setProxyType(resultSet.getString("proxyType"));
                    context.setProxyHost(resultSet.getString("proxyHost"));
                    context.setProxyPort(resultSet.getInt("proxyPort"));
                    resultSet.close();
                    preparedStatement.close();
                } else {
                    context = null;
                }
            } catch (Exception e) {
                Log.error(e);
                context = null;
            }
        }
        return context;
    }

    public static synchronized int addShell(ShellEntity shellContext) {
        int affectNum;
        synchronized (Db.class) {
            String uuid = UUID.randomUUID().toString();
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            PreparedStatement preparedStatement = getPreparedStatement("INSERT INTO \"shell\"(\"id\", \"url\", \"password\", \"secretKey\", \"payload\", \"cryption\", \"encoding\", \"headers\", \"reqLeft\", \"reqRight\", \"connTimeout\", \"readTimeout\", \"proxyType\", \"proxyHost\", \"proxyPort\", \"remark\", \"note\", \"createTime\", \"updateTime\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try {
                preparedStatement.setString(1, uuid);
                preparedStatement.setString(2, shellContext.getUrl());
                preparedStatement.setString(3, shellContext.getPassword());
                preparedStatement.setString(4, shellContext.getSecretKey());
                preparedStatement.setString(5, shellContext.getPayload());
                preparedStatement.setString(6, shellContext.getCryption());
                preparedStatement.setString(7, shellContext.getEncoding());
                preparedStatement.setString(8, shellContext.getHeaderS());
                preparedStatement.setString(9, shellContext.getReqLeft());
                preparedStatement.setString(10, shellContext.getReqRight());
                preparedStatement.setInt(11, shellContext.getConnTimeout());
                preparedStatement.setInt(12, shellContext.getReadTimeout());
                preparedStatement.setString(13, shellContext.getProxyType());
                preparedStatement.setString(14, shellContext.getProxyHost());
                preparedStatement.setInt(15, shellContext.getProxyPort());
                preparedStatement.setString(16, shellContext.getRemark());
                preparedStatement.setString(17, "");
                preparedStatement.setString(18, createTime);
                preparedStatement.setString(19, createTime);
                affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                Log.error(e);
                affectNum = 0;
            }
        }
        return affectNum;
    }

    public static synchronized int updateShell(ShellEntity shellContext) {
        int affectNum;
        synchronized (Db.class) {
            String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            PreparedStatement preparedStatement = getPreparedStatement("UPDATE \"shell\" SET \"url\" = ?, \"password\" = ?, \"secretKey\" = ?, \"payload\" = ?, \"cryption\" = ?, \"encoding\" = ?, \"headers\" = ?, \"reqLeft\" = ?, \"reqRight\" = ?, \"connTimeout\" = ?, \"readTimeout\" = ?, \"proxyType\" = ?, \"proxyHost\" = ?, \"proxyPort\" = ?, \"remark\" = ?, \"updateTime\" = ? WHERE id = ?");
            try {
                preparedStatement.setString(1, shellContext.getUrl());
                preparedStatement.setString(2, shellContext.getPassword());
                preparedStatement.setString(3, shellContext.getSecretKey());
                preparedStatement.setString(4, shellContext.getPayload());
                preparedStatement.setString(5, shellContext.getCryption());
                preparedStatement.setString(6, shellContext.getEncoding());
                preparedStatement.setString(7, shellContext.getHeaderS());
                preparedStatement.setString(8, shellContext.getReqLeft());
                preparedStatement.setString(9, shellContext.getReqRight());
                preparedStatement.setInt(10, shellContext.getConnTimeout());
                preparedStatement.setInt(11, shellContext.getReadTimeout());
                preparedStatement.setString(12, shellContext.getProxyType());
                preparedStatement.setString(13, shellContext.getProxyHost());
                preparedStatement.setInt(14, shellContext.getProxyPort());
                preparedStatement.setString(15, shellContext.getRemark());
                preparedStatement.setString(16, updateTime);
                preparedStatement.setString(17, shellContext.getId());
                affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                Log.error(e);
                affectNum = 0;
            }
        }
        return affectNum;
    }

    public static synchronized int removeShell(String id) {
        int affectNum;
        synchronized (Db.class) {
            PreparedStatement preparedStatement = getPreparedStatement("DELETE FROM shell WHERE \"id\"= ?");
            try {
                preparedStatement.setString(1, id);
                affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
                affectNum = 0;
            }
        }
        return affectNum;
    }

    public static synchronized int updateShellNote(String id, String note) {
        int affectNum;
        synchronized (Db.class) {
            String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            PreparedStatement preparedStatement = getPreparedStatement("UPDATE \"shell\" SET \"note\" = ?, \"updateTime\" = ? WHERE id = ?");
            try {
                preparedStatement.setString(1, note);
                preparedStatement.setString(2, updateTime);
                preparedStatement.setString(3, id);
                affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                Log.error(e);
                affectNum = 0;
            }
        }
        return affectNum;
    }

    public static synchronized String getShellNote(String id) {
        String note;
        synchronized (Db.class) {
            try {
                PreparedStatement preparedStatement = getPreparedStatement("SELECT note FROM shell WHERE id = ?");
                preparedStatement.setString(1, id);
                note = preparedStatement.executeQuery().getString("note");
                preparedStatement.close();
            } catch (Exception e) {
                Log.error(e);
                note = null;
            }
        }
        return note;
    }

    public static String[] getAllPlugin() {
        ArrayList pluginArrayList = new ArrayList();
        try {
            Statement statement = getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT pluginJarFile FROM plugin");
            while (resultSet.next()) {
                pluginArrayList.add(resultSet.getString("pluginJarFile"));
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            Log.error(e);
        }
        return (String[]) pluginArrayList.toArray(new String[0]);
    }

    public static synchronized int removePlugin(String jarFile) {
        int affectNum;
        synchronized (Db.class) {
            PreparedStatement preparedStatement = getPreparedStatement("DELETE FROM plugin WHERE pluginJarFile=?");
            try {
                preparedStatement.setString(1, jarFile);
                affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
                affectNum = 0;
            }
        }
        return affectNum;
    }

    public static synchronized int addPlugin(String jarFile) {
        int affectNum;
        synchronized (Db.class) {
            PreparedStatement preparedStatement = getPreparedStatement("INSERT INTO plugin (pluginJarFile) VALUES (?)");
            try {
                preparedStatement.setString(1, jarFile);
                affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
                affectNum = 0;
            }
        }
        return affectNum;
    }

    public static synchronized boolean addSetingKV(String key, String value) {
        boolean z = true;
        synchronized (Db.class) {
            if (existsSetingKey(key)) {
                z = updateSetingKV(key, value);
            } else {
                PreparedStatement preparedStatement = getPreparedStatement("INSERT INTO seting (\"key\", \"value\") VALUES (?, ?)");
                try {
                    preparedStatement.setString(1, key);
                    preparedStatement.setString(2, value);
                    int affectNum = preparedStatement.executeUpdate();
                    preparedStatement.close();
                    if (affectNum <= 0) {
                        z = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        return z;
    }

    public static synchronized boolean updateSetingKV(String key, String value) {
        boolean z = true;
        synchronized (Db.class) {
            if (existsSetingKey(key)) {
                PreparedStatement preparedStatement = getPreparedStatement("UPDATE seting set value=? WHERE key=?");
                try {
                    preparedStatement.setString(1, value);
                    preparedStatement.setString(2, key);
                    int affectNum = preparedStatement.executeUpdate();
                    preparedStatement.close();
                    if (affectNum <= 0) {
                        z = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    z = false;
                }
            } else {
                z = addSetingKV(key, value);
            }
        }
        return z;
    }

    public static synchronized boolean removeSetingK(String key) {
        boolean z = true;
        synchronized (Db.class) {
            PreparedStatement preparedStatement = getPreparedStatement("DELETE FROM seting WHERE key=?");
            try {
                preparedStatement.setString(1, key);
                int affectNum = preparedStatement.executeUpdate();
                preparedStatement.close();
                if (affectNum <= 0) {
                    z = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                z = false;
            }
        }
        return z;
    }

    public static String getSetingValue(String key) {
        String value;
        try {
            PreparedStatement preparedStatement = getPreparedStatement("SELECT value FROM seting WHERE key=?");
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getString("value");
            } else {
                value = null;
            }
            resultSet.close();
            preparedStatement.close();
            return value;
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static String getSetingValue(String key, String defaultValue) {
        String valueString = getSetingValue(key);
        if (valueString != null) {
            return valueString;
        }
        updateSetingKV(key, String.valueOf(defaultValue));
        return defaultValue;
    }

    public static boolean getSetingBooleanValue(String key) {
        return getSetingBooleanValue(key, false);
    }

    public static boolean getSetingBooleanValue(String key, boolean defaultValue) {
        String valueString = getSetingValue(key);
        if (valueString != null) {
            try {
                return Boolean.valueOf(valueString).booleanValue();
            } catch (Exception e) {
                Log.error(e);
                updateSetingKV(key, String.valueOf(defaultValue));
                return defaultValue;
            }
        } else {
            updateSetingKV(key, String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    public static int getSetingIntValue(String key) {
        return getSetingIntValue(key, -1);
    }

    public static int getSetingIntValue(String key, int defaultValue) {
        String valueString = getSetingValue(key);
        if (valueString != null) {
            try {
                return Integer.valueOf(valueString).intValue();
            } catch (Exception e) {
                Log.error(e);
                updateSetingKV(key, String.valueOf(defaultValue));
                return defaultValue;
            }
        } else {
            updateSetingKV(key, String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    public static boolean existsSetingKey(String key) {
        try {
            PreparedStatement preparedStatement = getPreparedStatement("SELECT COUNT(1) as c FROM seting WHERE key=?");
            preparedStatement.setString(1, key);
            int c = preparedStatement.executeQuery().getInt("c");
            preparedStatement.close();
            return c > 0;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }

    public static PreparedStatement getPreparedStatement(String sql) {
        if (dbConn == null) {
            return null;
        }
        try {
            return dbConn.prepareStatement(sql);
        } catch (SQLException e) {
            Log.error(e);
            return null;
        }
    }

    public static Statement getStatement() {
        if (dbConn == null) {
            return null;
        }
        try {
            return dbConn.createStatement();
        } catch (SQLException e) {
            Log.error(e);
            return null;
        }
    }

    private static Vector<String> getAllcolumn(ResultSetMetaData metaData) {
        if (metaData == null) {
            return null;
        }
        Vector<String> columns = new Vector<>();
        try {
            int columnNum = metaData.getColumnCount();
            for (int i = 0; i < columnNum; i++) {
                columns.add(metaData.getColumnName(i + 1));
            }
            return columns;
        } catch (Exception e) {
            Log.error(e);
            return columns;
        }
    }

    public static void Tclose() {
        try {
            if (dbConn != null && !dbConn.isClosed()) {
                dbConn.close();
            }
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
