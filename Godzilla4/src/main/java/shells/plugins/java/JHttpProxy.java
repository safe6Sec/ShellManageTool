package shells.plugins.java;

import core.annotation.PluginAnnotation;
import core.imp.Plugin;
import core.shell.ShellEntity;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JPanel;
import shells.plugins.generic.HttpProxy;
import util.functions;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "HttpProxy",
   DisplayName = "Http代理"
)
public class JHttpProxy extends HttpProxy implements Plugin {
   private static final String CLASS_NAME = "plugin.HttpRequest";

   public byte[] readPlugin() throws IOException {
      InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.classs", "HttpRequest"));
      byte[] data = functions.readInputStream(inputStream);
      inputStream.close();
      return data;
   }

   public String getClassName() {
      return "plugin.HttpRequest";
   }

   public void init(ShellEntity shellEntity) {
      super.init(shellEntity);
   }

   public JPanel getView() {
      return super.getView();
   }
}
