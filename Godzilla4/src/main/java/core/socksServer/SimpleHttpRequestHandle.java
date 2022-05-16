package core.socksServer;

import core.httpProxy.server.request.HttpRequest;
import core.httpProxy.server.response.HttpResponse;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.httpProxy.server.response.HttpResponseStatus;
import util.functions;

public class SimpleHttpRequestHandle implements HttpRequestHandle {
   public HttpResponse sendHttpRequest(HttpRequest httpRequest) {
      HttpResponse ret = null;

      try {
         HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(httpRequest.getUrl())).openConnection();
         httpURLConnection.setRequestMethod(httpRequest.getMethod());
         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         List<String[]> headers = httpRequest.getHttpRequestHeader().getHeaders();

         for(int i = 0; i < headers.size(); ++i) {
            String[] hk = (String[])headers.get(i);
            httpURLConnection.setRequestProperty(hk[0], hk[1]);
         }

         httpURLConnection.getOutputStream().write(httpRequest.getRequestData());
         httpURLConnection.getOutputStream().flush();
         HttpResponse httpResponse = new HttpResponse(new HttpResponseStatus(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage()));
         Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
         Iterator<String> iterator = headerFields.keySet().iterator();

         while(true) {
            String next;
            do {
               if (!iterator.hasNext()) {
                  httpResponse.setResponseData(functions.readInputStream(httpURLConnection.getInputStream()));
                  ret = httpResponse;
                  return ret;
               }

               next = (String)iterator.next();
            } while(next == null);

            List<String> values = (List)headerFields.get(next);

            for(int i = 0; i < values.size(); ++i) {
               String v = (String)values.get(i);
               httpResponse.getHttpResponseHeader().addHeader(next, v);
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
         return ret;
      }
   }
}
