package core.ui.component.model;

import core.Encoding;

public class DbInfo {
   private String dbType = "";
   private String dbHost = "";
   private int dbPort = 0;
   private String dbUserName = "";
   private String dbPassword = "";
   private Encoding charset;

   public DbInfo(Encoding dbEncoding) {
      this.charset = dbEncoding;
   }

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

   public void setDbType(String dbType) {
      this.dbType = dbType;
   }

   public void setDbHost(String dbHost) {
      this.dbHost = dbHost;
   }

   public void setDbPort(int dbPort) {
      this.dbPort = dbPort;
   }

   public void setDbUserName(String dbUserName) {
      this.dbUserName = dbUserName;
   }

   public void setDbPassword(String dbPassword) {
      this.dbPassword = dbPassword;
   }

   public Encoding getCharset() {
      return this.charset;
   }

   public void setCharset(String charsetString) {
      this.charset.setCharsetString(charsetString);
   }

   public String toString() {
      return "DbInfo{dbType='" + this.dbType + '\'' + ", dbHost='" + this.dbHost + '\'' + ", dbPort=" + this.dbPort + ", dbUserName='" + this.dbUserName + '\'' + ", dbPassword='" + this.dbPassword + '\'' + '}';
   }
}
