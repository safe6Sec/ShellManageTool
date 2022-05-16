package util.http;


import core.ApplicationContext;
import core.httpProxy.server.response.HttpResponseHeader;
import core.httpProxy.server.response.HttpResponseStatus;
import core.shell.ShellEntity;
import core.ui.component.dialog.HttpProgressBar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpResponse {
   private byte[] result;
   private ShellEntity shellEntity;
   private Map<String, List<String>> headerMap;
   private String message;
   private int responseCode;

   public HttpResponse(HttpResponseStatus httpResponseStatus) {
      this.responseCode=httpResponseStatus.code();
   }

   public byte[] getResult() {
      return this.result;
   }

   public Map<String, List<String>> getHeaderMap() {
      return this.headerMap;
   }

   public void setResult(byte[] result) {
      this.result = result;
   }

   public void setHeaderMap(Map<String, List<String>> headerMap) {
      this.headerMap = headerMap;
   }

   public HttpResponse(HttpURLConnection http, ShellEntity shellEntity) throws IOException {
      this.shellEntity = shellEntity;
      this.handleHeader(http.getHeaderFields());
      this.ReadAllData(this.getInputStream(http));
   }

   protected void handleHeader(Map<String, List<String>> map) {
      this.headerMap = map;

      try {
         this.message = (String)((List)map.get((Object)null)).get(0);
         Http http = this.shellEntity.getHttp();
         http.getCookieManager().put(http.getUri(), map);
         http.getCookieManager().getCookieStore().get(http.getUri());
         List<HttpCookie> cookies = http.getCookieManager().getCookieStore().get(http.getUri());
         StringBuilder sb = new StringBuilder();
         cookies.forEach((cookie) -> {
            sb.append(String.format(" %s=%s;", cookie.getName(), cookie.getValue()));
         });
         if (sb.length() > 0) {
            this.shellEntity.getHeaders().put("Cookie", sb.toString().trim());
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   protected InputStream getInputStream(HttpURLConnection httpURLConnection) throws IOException {
      InputStream inputStream = httpURLConnection.getErrorStream();
      return inputStream != null ? inputStream : httpURLConnection.getInputStream();
   }

   protected void ReadAllData(InputStream inputStream) throws IOException {
      int maxLen = 0;

      try {
         if (this.headerMap.get("Content-Length") != null && ((List)this.headerMap.get("Content-Length")).size() > 0) {
            maxLen = Integer.parseInt((String)((List)this.headerMap.get("Content-Length")).get(0));
            this.result = this.ReadKnownNumData(inputStream, maxLen);
         } else {
            this.result = this.ReadUnknownNumData(inputStream);
         }
      } catch (NumberFormatException var4) {
         this.result = this.ReadUnknownNumData(inputStream);
      }

      this.result = this.shellEntity.getCryptionModule().decode(this.result);
   }

   protected byte[] ReadKnownNumData(InputStream inputStream, int num) throws IOException {
      if (num <= 0) {
         return num == 0 ? this.ReadUnknownNumData(inputStream) : null;
      } else {
         byte[] temp = new byte[5120];
         int readOneNum = 0;
         int readNum = 0;
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         Boolean isShowBar = (Boolean)ApplicationContext.isShowHttpProgressBar.get();
         if (isShowBar != null && isShowBar) {
            HttpProgressBar httpProgressBar = new HttpProgressBar("download threadId:" + Thread.currentThread().getId(), num);

            while((readOneNum = inputStream.read(temp)) != -1) {
               bos.write(temp, 0, readOneNum);
               readNum += readOneNum;
               httpProgressBar.setValue(readNum);
            }
         } else {
            while((readOneNum = inputStream.read(temp)) != -1) {
               bos.write(temp, 0, readOneNum);
            }
         }

         return bos.toByteArray();
      }
   }

   protected byte[] ReadUnknownNumData(InputStream inputStream) throws IOException {
      byte[] temp = new byte[5120];
      int readOneNum = 0;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      while((readOneNum = inputStream.read(temp)) != -1) {
         bos.write(temp, 0, readOneNum);
      }

      return bos.toByteArray();
   }

   public HttpResponse parseHttpResponse() throws IOException {
      HttpResponse httpResponse = new HttpResponse(new HttpResponseStatus(this.responseCode));
      httpResponse.setResponseData(this.result);
      HttpResponseHeader responseHeader = httpResponse.getHttpResponseHeader();
      Iterator<String> headerKeys = this.headerMap.keySet().iterator();

      while(headerKeys.hasNext()) {
         String keyString = (String)headerKeys.next();
         List<String> headList = (List)this.headerMap.get(keyString);
         if (headList != null) {
            headList.parallelStream().forEach((v) -> {
               responseHeader.addHeader(keyString, v);
            });
         }
      }

      return httpResponse;
   }

   private HttpResponseHeader getHttpResponseHeader() {
      HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
      Map<String, List<String>> headerMap1 = this.getHeaderMap();
      // TODO: 2022/5/16
      return null;
   }

   private void setResponseData(byte[] result) {
      this.result = result;
   }
}
