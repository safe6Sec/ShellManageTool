package shells.plugins.php;

import core.annotation.PluginAnnotation;
import java.io.InputStream;
import shells.plugins.generic.RealCmd;
import util.Log;
import util.functions;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "RealCmd",
   DisplayName = "虚拟终端"
)
public class PRealCmd extends RealCmd {
   private static final String CLASS_NAME = "plugin.RealCmd";

   public byte[] readPlugin() {
      byte[] data = null;

      try {
         InputStream inputStream = this.getClass().getResourceAsStream("assets/realCmd.php");
         data = functions.readInputStream(inputStream);
         inputStream.close();
      } catch (Exception var3) {
         Log.error((Throwable)var3);
      }

      return data;
   }

   public boolean isTryStart() {
      return true;
   }

   public String getClassName() {
      return "plugin.RealCmd";
   }
}
