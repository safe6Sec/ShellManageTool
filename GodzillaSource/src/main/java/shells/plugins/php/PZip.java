package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(
    payloadName = "PhpDynamicPayload",
    Name = "PZip"
)

public class PZip implements Plugin {
    private static final String CLASS_NAME = "PZip";
    private JLabel compressDestFileLabel;
    private JTextField compressDestFileTextField;
    private JLabel compressSrcDirLabel;
    private JTextField compressSrcDirTextField;
    private Encoding encoding;
    private boolean loadState;
    private JPanel panel = new JPanel(new GridBagLayout());
    private Payload payload;
    private ShellEntity shellEntity;
    private JButton unZipButton;
    private JButton zipButton;

    public PZip() {
        GBC gbcLCompressSrcDir = new GBC(0, 0).setInsets(5, -40, 0, 0);
        GBC gbcCompressSrcDir = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLCompressDestFileLabel = new GBC(0, 1).setInsets(5, -40, 0, 0);
        GBC gbcCompressDestFile = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcZipButton = new GBC(0, 2).setInsets(5, -20, 0, 0);
        GBC gbcUnZipButton = new GBC(0, 2, 5, 1).setInsets(5, 20, 0, 0);
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
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(String.format("shell/php/assets/%s.php", CLASS_NAME));
                byte[] binCode = functions.readInputStream(inputStream);
                inputStream.close();
                this.loadState = this.payload.include(CLASS_NAME, binCode);
                if (this.loadState) {
                    Log.log("Load success", new Object[0]);
                } else {
                    Log.log("Load fail", new Object[0]);
                }
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    private void zipButtonClick(ActionEvent actionEvent) {
        load();
        if (this.compressDestFileTextField.getText().trim().length() <= 0 || this.compressSrcDirTextField.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "请检查是否填写完整", "提示", 1);
            return;
        }
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("compressFile", this.compressDestFileTextField.getText().trim());
        reqParameter.add("compressDir", this.compressSrcDirTextField.getText().trim());
        JOptionPane.showMessageDialog(this.shellEntity.getFrame(), this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "zip", reqParameter)), "提示", 1);
    }

    private void unZipButtonClick(ActionEvent actionEvent) {
        load();
        if (this.compressDestFileTextField.getText().trim().length() <= 0 || this.compressSrcDirTextField.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "请检查是否填写完整", "提示", 1);
            return;
        }
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("compressFile", this.compressDestFileTextField.getText().trim());
        reqParameter.add("compressDir", this.compressSrcDirTextField.getText().trim());
        JOptionPane.showMessageDialog(this.shellEntity.getFrame(), this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "unZip", reqParameter)), "提示", 1);
    }

    @Override 
    public void init(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        this.compressSrcDirTextField.setText(this.payload.currentDir());
        this.compressDestFileTextField.setText(this.payload.currentDir() + functions.getLastFileName(this.payload.currentDir()) + ".zip");
        automaticBindClick.bindJButtonClick(this, this);
    }

    @Override 
    public JPanel getView() {
        return this.panel;
    }
}
