package core.httpProxy.server.core;

import com.httpProxy.server.request.HttpRequest;
import java.net.Socket;

public interface HttpProxyHandle {
   void handler(Socket var1, HttpRequest var2) throws Exception;
}
