package util.http;

import core.ApplicationContext;
import core.shell.ShellEntity;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import util.Log;
import util.functions;

public class Http {
    private CookieManager cookieManager;
    private Proxy proxy;
    private ShellEntity shellContext;
    private URI uri;

    static {
        trustAllHttpsCertificates();
    }

    public Http(ShellEntity shellContext) {
        this.shellContext = shellContext;
        this.proxy = ApplicationContext.getProxy(this.shellContext);
    }

    public HttpResponse SendHttpConn(String urlString, String method, Map<String, String> header, byte[] requestData, int connTimeOut, int readTimeOut, Proxy proxy2) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(urlString).openConnection(proxy2);
            if (urlString.indexOf("https://") != -1) {
                ((HttpsURLConnection) httpConn).setHostnameVerifier(new TrustAnyHostnameVerifier());
            }
            httpConn.setDoInput(true);
            httpConn.setDoOutput(!"GET".equals(method.toUpperCase()));
            if (connTimeOut > 0) {
                httpConn.setConnectTimeout(connTimeOut);
            }
            if (readTimeOut > 0) {
                httpConn.setReadTimeout(readTimeOut);
            }
            httpConn.setRequestMethod(method.toUpperCase());
            addHttpHeader(httpConn, ApplicationContext.getGloballHttpHeaderX());
            addHttpHeader(httpConn, header);
            if (httpConn.getDoOutput()) {
                httpConn.getOutputStream().write(requestData);
            }
            return new HttpResponse(httpConn, this.shellContext);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public HttpResponse sendHttpResponse(Map<String, String> header, byte[] requestData, int connTimeOut, int readTimeOut) {
        int i;
        int i2 = 1;
        //对发送数据进行加密
        byte[] requestData2 = this.shellContext.getCryptionModel().encode(requestData);
        if (this.shellContext.isSendLRReqData()) {
            byte[] leftData = this.shellContext.getReqLeft().getBytes();
            byte[] rightData = this.shellContext.getReqRight().getBytes();
            if (leftData.length > 0) {
                i = leftData.length;
            } else {
                i = 1;
            }
            Object concatArrays = functions.concatArrays(leftData, 0, i - 1, requestData2, 0, requestData2.length - 1);
            int length = (leftData.length + requestData2.length) - 1;
            if (rightData.length > 0) {
                i2 = rightData.length;
            }
            requestData2 = (byte[]) functions.concatArrays(concatArrays, 0, length, rightData, 0, i2 - 1);
        }
        return SendHttpConn(this.shellContext.getUrl(), "POST", header, requestData2, connTimeOut, readTimeOut, this.proxy);
    }

    public HttpResponse sendHttpResponse(byte[] requestData, int connTimeOut, int readTimeOut) {
        return sendHttpResponse(this.shellContext.getHeaders(), requestData, connTimeOut, readTimeOut);
    }

    public HttpResponse sendHttpResponse(byte[] requestData) {
        return sendHttpResponse(requestData, this.shellContext.getConnTimeout(), this.shellContext.getReadTimeout());
    }

    public static void addHttpHeader(HttpURLConnection connection, Map<String, String> headerMap) {
        if (headerMap != null) {
            for (String name : headerMap.keySet()) {
                connection.setRequestProperty(name, headerMap.get(name));
            }
        }
    }

    public synchronized URI getUri() {
        if (this.uri == null) {
            try {
                this.uri = URI.create(this.shellContext.getUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.uri;
    }

    public synchronized CookieManager getCookieManager() {
        CookieManager cookieManager2;
        synchronized (this) {
            if (this.cookieManager == null) {
                this.cookieManager = new CookieManager();
                try {
                    String cookieStr = this.shellContext.getHeaders().get("Cookie");
                    if (cookieStr == null) {
                        cookieStr = this.shellContext.getHeaders().get("cookie");
                    }
                    if (cookieStr != null) {
                        for (String cookieStr2 : cookieStr.split(";")) {
                            String[] cookieAtt = cookieStr2.split("=");
                            if (cookieAtt.length == 2) {
                                this.cookieManager.getCookieStore().add(getUri(), new HttpCookie(cookieAtt[0], cookieAtt[1]));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cookieManager2 = this.cookieManager;
        }
        return cookieManager2;
    }

    private static void trustAllHttpsCertificates() {
        try {
            TrustManager[] trustAllCerts = {null};
            trustAllCerts[0] = new miTM();
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            SSLContext sc2 = SSLContext.getInstance("TLS");
            sc2.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc2.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     
    public static class miTM implements TrustManager, X509TrustManager {
        private miTM() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        @Override 
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }

        @Override 
        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
    }

    public class TrustAnyHostnameVerifier implements HostnameVerifier {
        public TrustAnyHostnameVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
