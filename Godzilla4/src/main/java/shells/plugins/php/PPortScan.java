package shells.plugins.php;

import core.annotation.PluginAnnotation;
import java.io.IOException;
import java.io.InputStream;
import shells.plugins.generic.PortScan;
import util.functions;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "PortScan",
   DisplayName = "端口扫描"
)
public class PPortScan extends PortScan {
   private static final String CLASS_NAME = "PortScan";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.php", "PortScan"));
      byte[] data = functions.readInputStream(inputStream);
      inputStream.close();
      return data;
   }

   public String getClassName() {
      return "PortScan";
   }
}
