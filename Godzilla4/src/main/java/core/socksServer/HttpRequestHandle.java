package core.socksServer;


import core.httpProxy.server.request.HttpRequest;
import core.httpProxy.server.response.HttpResponse;

public interface HttpRequestHandle {
   HttpResponse sendHttpRequest(HttpRequest var1);
}
