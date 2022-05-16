package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.DataView;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "PWebShellScan",
   DisplayName = "WebShellScan"
)
public class PWebShellScan implements Plugin {
   private static final String CLASS_NAME = "WebShellScan";
   private static final Vector COLUMNS_VECTOR = new Vector(new CopyOnWriteArrayList(new String[]{"File", "Line", "SuspiciousCode"}));
   private final JPanel panel = new JPanel(new BorderLayout());
   private final DataView dataView;
   private final JButton scanButton = new JButton("scan");
   private final JLabel scanPathLabel = new JLabel("scanPath :");
   private final JTextField scanPathTextField;
   private boolean loadState;
   private final JSplitPane portScanSplitPane;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public PWebShellScan() {
      this.dataView = new DataView((Vector)null, COLUMNS_VECTOR, -1, -1);
      this.scanPathTextField = new JTextField(30);
      this.portScanSplitPane = new JSplitPane();
      this.portScanSplitPane.setOrientation(0);
      this.portScanSplitPane.setDividerSize(0);
      JPanel topPanel = new JPanel();
      topPanel.add(this.scanPathLabel);
      topPanel.add(this.scanPathTextField);
      topPanel.add(this.scanButton);
      this.portScanSplitPane.setTopComponent(topPanel);
      this.portScanSplitPane.setBottomComponent(new JScrollPane(this.dataView));
      this.panel.add(this.portScanSplitPane);
   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.php", "WebShellScan"));
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.loadState = this.payload.include("WebShellScan", data)) {
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

   private void scanButtonClick(ActionEvent actionEvent) {
      this.load();
      String scanPath = this.scanPathTextField.getText().trim();
      ReqParameter reqParamete = new ReqParameter();
      reqParamete.add("scanPath", scanPath);
      byte[] result = this.payload.evalFunc("WebShellScan", "run", reqParamete);
      String resultString = this.encoding.Decoding(result);
      this.formatResult(resultString);
   }

   private void formatResult(String resultString) {
      String[] lines = resultString.split("\n");
      String[] infos = null;
      Vector rowsVector = new Vector();
      String[] var5 = lines;
      int var6 = lines.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String line = var5[var7];
         infos = line.split("\t");
         if (infos.length < 3) {
            Log.error(line);
         } else {
            Vector oneRowVector = new Vector();
            boolean st = false;
            oneRowVector.add(functions.base64DecodeToString(infos[0]));
            oneRowVector.add(functions.base64DecodeToString(infos[1]));
            oneRowVector.add(functions.base64DecodeToString(infos[2]));
            Iterator var11 = rowsVector.iterator();

            while(var11.hasNext()) {
               Object object = var11.next();
               if (object.equals(oneRowVector)) {
                  st = true;
                  break;
               }
            }

            if (!st) {
               rowsVector.add(oneRowVector);
            }
         }
      }

      this.dataView.AddRows(rowsVector);
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      this.scanPathTextField.setText(this.payload.currentDir());
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }
}
