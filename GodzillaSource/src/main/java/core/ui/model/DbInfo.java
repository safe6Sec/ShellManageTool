package core.ui.model;

public class DbInfo {
    private String dbHost = new String();
    private String dbPassword = new String();
    private int dbPort = 0;
    private String dbType = new String();
    private String dbUserName = new String();

    public String getDbType() {
        return this.dbType;
    }

    public String getDbHost() {
        return this.dbHost;
    }

    public int getDbPort() {
        return this.dbPort;
    }

    public String getDbUserName() {
        return this.dbUserName;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

    public void setDbType(String dbType2) {
        this.dbType = dbType2;
    }

    public void setDbHost(String dbHost2) {
        this.dbHost = dbHost2;
    }

    public void setDbPort(int dbPort2) {
        this.dbPort = dbPort2;
    }

    public void setDbUserName(String dbUserName2) {
        this.dbUserName = dbUserName2;
    }

    public void setDbPassword(String dbPassword2) {
        this.dbPassword = dbPassword2;
    }

    public String toString() {
        return "DbInfo [dbType=" + this.dbType + ", dbHost=" + this.dbHost + ", dbPort=" + this.dbPort + ", dbUserName=" + this.dbUserName + ", dbPassword=" + this.dbPassword + "]";
    }
}
