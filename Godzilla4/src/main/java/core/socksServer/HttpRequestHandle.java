package core.socksServer;

import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.response.HttpResponse;

public interface HttpRequestHandle {
   HttpResponse sendHttpRequest(HttpRequest var1);
}
