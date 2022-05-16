package core.httpProxy.server.request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HttpRequestHeader {
   ArrayList<String[]> headers = new ArrayList();

   public HttpRequestHeader addHeader(String name, String value) {
      String[] header = new String[]{name == null ? "" : name.trim(), value == null ? "" : value.trim()};
      if (name != null) {
         this.headers.add(header);
      }

      return this;
   }

   public HttpRequestHeader setHeader(String name, String value) {
      Iterator<String[]> headers = this.getHeaders().iterator();

      String[] kv;
      for(kv = null; headers.hasNext(); kv = null) {
         kv = (String[])headers.next();
         if (kv[0].equals(name)) {
            break;
         }
      }

      if (kv != null) {
         this.getHeaders().remove(kv);
      }

      this.addHeader(name, value);
      return this;
   }

   public String getHeader(String key) {
      Iterator<String[]> headers = this.getHeaders().iterator();

      String[] kv;
      for(kv = null; headers.hasNext(); kv = null) {
         kv = (String[])headers.next();
         if (kv[0].equals(key)) {
            break;
         }
      }

      return kv != null ? kv[1] : null;
   }

   public HttpRequestHeader removeHeader(String name) {
      List<String[]> removeList = new ArrayList();
      Iterator<String[]> headers = this.getHeaders().iterator();

      for(String[] kv = null; headers.hasNext(); kv = null) {
         kv = (String[])headers.next();
         if (kv[0].equals(name)) {
            removeList.add(kv);
         }
      }

      for(int i = 0; i < removeList.size(); ++i) {
         String[] s = (String[])removeList.get(i);
         this.getHeaders().remove(s);
      }

      return this;
   }

   public HttpRequestHeader setContentType(String value) {
      return this.setHeader("Content-Type", value);
   }

   public String decode() {
      StringBuilder stringBuilder = new StringBuilder();
      Iterator iterator = this.headers.iterator();

      while(iterator.hasNext()) {
         String[] ex = (String[])((String[])iterator.next());
         stringBuilder.append(ex[0]);
         stringBuilder.append(": ");
         stringBuilder.append(ex[1]);
         stringBuilder.append("\r\n");
      }

      return stringBuilder.toString();
   }

   public List<String[]> getHeaders() {
      return this.headers;
   }

   public String toString() {
      return "HttpResponseHeader{headers=" + this.headers + '}';
   }
}
