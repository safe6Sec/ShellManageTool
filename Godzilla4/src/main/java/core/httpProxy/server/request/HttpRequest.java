package core.httpProxy.server.request;

import com.httpProxy.server.ByteUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class HttpRequest {
   private String uri;
   private String url;
   private String method;
   private String host;
   private int port = -1;
   private HttpRequestHeader httpRequestHeader = new HttpRequestHeader();
   private byte[] requestData;
   private String httpVersion;
   private boolean isHttps;

   public HttpRequest() {
      this.getHttpRequestHeader().setHeader("Accept", "*/*");
      this.method = "GET";
      this.httpVersion = "HTTP/1.1";
   }

   public String getUri() {
      return this.uri;
   }

   public HttpRequest setUri(String uri) {
      this.uri = uri;
      return this;
   }

   public String getUrl() {
      return this.url;
   }

   public HttpRequest setUrl(String url) {
      if (url.startsWith("https://")) {
         this.isHttps = true;
         this.port = 443;
      } else if (url.startsWith("http://")) {
         this.port = 80;
      } else {
         this.port = -1;
      }

      int index = url.lastIndexOf(":");
      int findex = url.indexOf(":");
      String thost;
      String tempStr;
      String tport;
      if (index > 5) {
         thost = url.substring(findex + 3, index);
         tempStr = url.substring(index + 1);
         tport = tempStr.substring(0, tempStr.indexOf("/"));
         String turi = tempStr.substring(tempStr.indexOf("/"));
         this.setHost(thost);
         this.port = Integer.parseInt(tport);
         this.uri = turi;
      } else {
         thost = url.substring(findex + 3);
         tempStr = thost.substring(0, thost.indexOf("/"));
         tport = thost.substring(thost.indexOf("/"));
         this.setHost(tempStr);
         this.uri = tport;
      }

      this.url = url;
      return this;
   }

   public String getMethod() {
      return this.method;
   }

   public HttpRequest setMethod(String method) {
      this.method = method;
      return this;
   }

   public String getHost() {
      return this.host;
   }

   public HttpRequest setHost(String host) {
      this.host = host;
      this.getHttpRequestHeader().setHeader("Host", host);
      return this;
   }

   public int getPort() {
      return this.port;
   }

   public HttpRequest setPort(int port) {
      this.port = port;
      return this;
   }

   public HttpRequestHeader getHttpRequestHeader() {
      return this.httpRequestHeader;
   }

   public HttpRequest setHttpRequestHeader(HttpRequestHeader httpRequestHeader) {
      this.httpRequestHeader = httpRequestHeader;
      return this;
   }

   public byte[] getRequestData() {
      return this.requestData;
   }

   public HttpRequest setRequestData(byte[] requestData) {
      this.requestData = requestData;
      return this;
   }

   public String getHttpVersion() {
      return this.httpVersion;
   }

   public HttpRequest setHttpVersion(String httpVersion) {
      this.httpVersion = httpVersion;
      return this;
   }

   public boolean isHttps() {
      return this.isHttps;
   }

   public HttpRequest setHttps(boolean https) {
      this.isHttps = https;
      return this;
   }

   public static HttpRequest Decode(byte[] metadata) {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(metadata);
      return Decode((InputStream)inputStream);
   }

   public static HttpRequest Decode(InputStream inputStream) {
      return Decode(inputStream, new HttpRequest());
   }

   public static HttpRequest Decode(InputStream inputStream, HttpRequest httpRequest) {
      httpRequest.httpRequestHeader = new HttpRequestHeader();
      String line = new String(ByteUtil.readNextLine(inputStream));
      String[] ext = line.split(" ");
      int index = true;
      httpRequest.method = ext[0];
      httpRequest.uri = ext[1];
      httpRequest.httpVersion = ext[2];
      if ("CONNECT".equals(httpRequest.method.toUpperCase())) {
         httpRequest.isHttps = true;
      }

      int index;
      while(!"".equals(line = new String(ByteUtil.readNextLine(inputStream)))) {
         try {
            index = line.indexOf(":");
            if (index != -1) {
               httpRequest.httpRequestHeader.addHeader(line.substring(0, index), line.substring(index + 1));
            }
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

      httpRequest.host = httpRequest.httpRequestHeader.getHeader("Host");
      if ((index = httpRequest.host.indexOf(":")) != -1) {
         httpRequest.host = httpRequest.host.substring(0, index);
      }

      String tport;
      if (httpRequest.isHttps) {
         if ((index = httpRequest.uri.lastIndexOf(":")) != -1 && httpRequest.port == -1) {
            tport = httpRequest.uri.substring(index + 1);
            index = tport.indexOf("/");
            if (index != -1) {
               httpRequest.port = Integer.parseInt(tport.substring(0, index));
            } else {
               try {
                  httpRequest.port = Integer.parseInt(tport);
               } catch (Exception var8) {
                  var8.printStackTrace();
               }
            }
         }

         if (!"CONNECT".equals(httpRequest.method.toUpperCase())) {
            httpRequest.url = "https://" + httpRequest.host + (httpRequest.port == 443 ? "" : ":" + String.valueOf(httpRequest.port)) + httpRequest.uri;
         } else {
            httpRequest.url = String.format("https://%s/", httpRequest.host + (httpRequest.port == 443 ? "" : ":" + String.valueOf(httpRequest.port)));
         }
      } else {
         httpRequest.url = httpRequest.uri;

         try {
            index = httpRequest.url.lastIndexOf(":");
            if (index != -1 && httpRequest.url.indexOf(":") != index) {
               tport = httpRequest.url.substring(index + 1);
               index = tport.indexOf("/");
               httpRequest.port = Integer.parseInt(tport.substring(0, index));
            } else {
               httpRequest.port = 80;
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }
      }

      try {
         httpRequest.requestData = ByteUtil.readInputStream(inputStream);
         httpRequest.httpRequestHeader.setHeader("Content-Length", String.valueOf(httpRequest.requestData.length));
      } catch (Exception var7) {
      }

      httpRequest.httpRequestHeader.removeHeader("Proxy-Connection");
      return httpRequest;
   }

   public String toString() {
      return "HttpRequest{uri='" + this.uri + '\'' + ", url='" + this.url + '\'' + ", method='" + this.method + '\'' + ", host='" + this.host + '\'' + ", port=" + this.port + ", httpRequestHeader=" + this.httpRequestHeader + ", requestData=" + Arrays.toString(this.requestData) + ", httpVersion='" + this.httpVersion + '\'' + ", isHttps=" + this.isHttps + '}';
   }
}
