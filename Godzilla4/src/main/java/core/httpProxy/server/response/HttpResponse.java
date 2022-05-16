package core.httpProxy.server.response;

import com.httpProxy.server.ByteUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class HttpResponse {
   private HttpResponseStatus httpResponseStatus;
   private HttpResponseHeader httpResponseHeader;
   private byte[] responseData;

   public HttpResponse(HttpResponseStatus httpResponseStatus, HttpResponseHeader httpResponseHeader, byte[] responseData) {
      this.httpResponseStatus = httpResponseStatus;
      this.httpResponseHeader = httpResponseHeader;
      this.responseData = responseData;
   }

   public HttpResponse(HttpResponseStatus httpResponseStatus, HttpResponseHeader httpResponseHeader, String responseData) {
      this(httpResponseStatus, httpResponseHeader, responseData.getBytes());
   }

   public HttpResponse(HttpResponseStatus httpResponseStatus) {
      this(httpResponseStatus, new HttpResponseHeader(), new byte[0]);
   }

   public HttpResponseStatus getHttpResponseStatus() {
      return this.httpResponseStatus;
   }

   public void setHttpResponseStatus(HttpResponseStatus httpResponseStatus) {
      this.httpResponseStatus = httpResponseStatus;
   }

   public HttpResponseHeader getHttpResponseHeader() {
      return this.httpResponseHeader;
   }

   public void setHttpResponseHeader(HttpResponseHeader httpResponseHeader) {
      this.httpResponseHeader = httpResponseHeader;
   }

   public byte[] getResponseData() {
      return this.responseData;
   }

   public void setResponseData(byte[] responseData) {
      this.responseData = responseData;
   }

   public byte[] encode() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] ex = "\r\n".getBytes();

      try {
         outputStream.write("HTTP/1.1".getBytes());
         outputStream.write(32);
         outputStream.write(String.valueOf(this.httpResponseStatus.code()).getBytes());
         outputStream.write(32);
         outputStream.write(this.httpResponseStatus.getReasonPhrase().getBytes());
         outputStream.write(ex);
         if (this.httpResponseHeader == null) {
            this.httpResponseHeader = new HttpResponseHeader();
         }

         if (this.responseData != null) {
            this.httpResponseHeader.setHeader("Content-Length", String.valueOf(this.responseData.length));
         }

         if (this.httpResponseHeader != null) {
            Iterator<String[]> headers = this.httpResponseHeader.getHeaders().iterator();
            String[] kv = null;

            while(headers.hasNext()) {
               kv = (String[])headers.next();
               outputStream.write(kv[0].getBytes());
               outputStream.write(58);
               outputStream.write(32);
               outputStream.write(kv[1].getBytes());
               outputStream.write(ex);
            }
         }

         outputStream.write(ex);
         if (this.responseData != null) {
            outputStream.write(this.responseData);
         }
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }

      return outputStream.toByteArray();
   }

   public static HttpResponse decode(byte[] responseData) {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(responseData);
      return decode((InputStream)inputStream);
   }

   public static HttpResponse decode(InputStream inputStream) {
      HttpResponse httpResponse = new HttpResponse((HttpResponseStatus)null);
      String line = new String(ByteUtil.readNextLine(inputStream));
      int index = line.indexOf(" ");
      line = line.substring(index + 1);
      index = line.indexOf(" ");
      httpResponse.httpResponseStatus = new HttpResponseStatus(Integer.parseInt(line.substring(0, index)), line.substring(index + 1));
      httpResponse.httpResponseHeader = new HttpResponseHeader();
      String[] ext = null;

      while(!"".equals(line = new String(ByteUtil.readNextLine(inputStream)))) {
         try {
            index = line.indexOf(":");
            if (index != -1) {
               httpResponse.httpResponseHeader.addHeader(line.substring(0, index), line.substring(index + 1));
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      try {
         httpResponse.responseData = ByteUtil.readInputStream(inputStream);
         httpResponse.httpResponseHeader.setHeader("Content-Length", String.valueOf(httpResponse.responseData.length));
      } catch (Exception var6) {
      }

      return httpResponse;
   }

   public String toString() {
      return "HttpResponse{httpResponseStatus=" + this.httpResponseStatus + ", httpResponseHeader=" + this.httpResponseHeader + ", responseData=" + Arrays.toString(this.responseData) + '}';
   }
}
