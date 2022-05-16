package core.httpProxy.server;

import com.httpProxy.server.response.HttpResponse;
import com.httpProxy.server.response.HttpResponseHeader;
import java.util.HashMap;
import java.util.List;

public class HttpCookie {
   private HashMap<String, String> cookieMap = new HashMap();

   public String addCookiie(String key, String value) {
      return (String)this.cookieMap.put(key, value);
   }

   public String getCookie(String key) {
      return (String)this.cookieMap.get(key);
   }

   public String removeCookie(String key) {
      return (String)this.cookieMap.remove(key);
   }

   public static HttpCookie parse(HttpResponse httpResponse) {
      return parse(httpResponse.getHttpResponseHeader());
   }

   public static HttpCookie parse(HttpResponseHeader httpResponseHeader) {
      HttpCookie httpCookie = new HttpCookie();
      List<String[]> headers = httpResponseHeader.getHeaders();
      headers.forEach((v) -> {
         if (v.length >= 2 && "set-cookie".equals(v[0].toLowerCase())) {
            String[] _cookie = v[1].split(";")[0].split("=");
            httpCookie.addCookiie(_cookie[0], _cookie[1]);
         }

      });
      return httpCookie;
   }
}
