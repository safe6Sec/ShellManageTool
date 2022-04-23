package core.ui.component.dialog;

import core.ApplicationContext;
import core.Db;
import core.shell.ShellEntity;
import core.ui.MainActivity;
import core.ui.ShellManage;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import javassist.bytecode.Opcode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class ShellSetting extends JDialog {
    private Dimension TextFieldDim = new Dimension((int) Opcode.GOTO_W, 23);
    private JPanel basicsPanel;
    private JLabel connTimeOutLabel;
    private JTextField connTimeOutTextField;
    private JComboBox<String> cryptionComboBox;
    private JLabel cryptionLabel;
    private JComboBox<String> encodingComboBox;
    private JLabel encodingLabel;
    private String error;
    private RTextArea headersTextArea;
    private Dimension labelDim = new Dimension((int) Opcode.FCMPG, 23);
    private RTextArea leftTextArea;
    private JLabel passwordLabel;
    private JTextField passwordTextField;
    private JComboBox<String> payloadComboBox;
    private JLabel payloadLabel;
    private JComboBox<String> proxyComboBox;
    private JLabel proxyHostLabel;
    private JTextField proxyHostTextField;
    private JLabel proxyLabel;
    private JLabel proxyPortLabel;
    private JTextField proxyPortTextField;
    private JLabel readTimeOutLabel;
    private JTextField readTimeOutTextField;
    private JLabel remarkLabel;
    private JTextField remarkTextField;
    private JPanel reqPanel;
    private RTextArea rightTextArea;
    private JLabel secretKeyLabel;
    private JTextField secretKeyTextField;
    private JButton setButton;
    private ShellEntity shellContext;
    private String shellId;
    private JTabbedPane tabbedPane;
    private JButton testButton;
    private JLabel urlLabel;
    private JTextField urlTextField;

    public ShellSetting(String id) {
        super(MainActivity.getFrame(), "Shell Setting", true);
        this.shellId = id;
        initLabel();
        initTextField();
        initComboBox();
        Container c = getContentPane();
        this.tabbedPane = new JTabbedPane();
        this.basicsPanel = new JPanel();
        this.reqPanel = new JPanel();
        this.basicsPanel.setLayout(new GridBagLayout());
        GBC gbcLUrl = new GBC(0, 0).setInsets(5, -40, 0, 0);
        GBC gbcUrl = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLPassword = new GBC(0, 1).setInsets(5, -40, 0, 0);
        GBC gbcPassword = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLSecretKey = new GBC(0, 2).setInsets(5, -40, 0, 0);
        GBC gbcSecretKey = new GBC(1, 2, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLConnTimeOut = new GBC(0, 3).setInsets(5, -40, 0, 0);
        GBC gbcConnTimeOut = new GBC(1, 3, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLReadTimeOut = new GBC(0, 4).setInsets(5, -40, 0, 0);
        GBC gbcReadTimeOut = new GBC(1, 4, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLProxyHost = new GBC(0, 5).setInsets(5, -40, 0, 0);
        GBC gbcProxyHost = new GBC(1, 5, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLProxyPort = new GBC(0, 6).setInsets(5, -40, 0, 0);
        GBC gbcProxyPort = new GBC(1, 6, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLRemark = new GBC(0, 7).setInsets(5, -40, 0, 0);
        GBC gbcRemark = new GBC(1, 7, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLProxy = new GBC(0, 8).setInsets(5, -40, 0, 0);
        GBC gbcProxy = new GBC(1, 8, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLEncoding = new GBC(0, 9).setInsets(5, -40, 0, 0);
        GBC gbcEncoding = new GBC(1, 9, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLPayload = new GBC(0, 10).setInsets(5, -40, 0, 0);
        GBC gbcPayload = new GBC(1, 10, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLCryption = new GBC(0, 11).setInsets(5, -40, 0, 0);
        GBC gbcCryption = new GBC(1, 11, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcSet = new GBC(0, 12, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcTest = new GBC(1, 12, 3, 1).setInsets(5, 20, 0, 0);
        this.setButton = new JButton((this.shellId == null || this.shellId.trim().length() <= 0) ? "添加" : "修改");
        this.testButton = new JButton("测试连接");
        this.basicsPanel.add(this.urlLabel, gbcLUrl);
        this.basicsPanel.add(this.urlTextField, gbcUrl);
        this.basicsPanel.add(this.passwordLabel, gbcLPassword);
        this.basicsPanel.add(this.passwordTextField, gbcPassword);
        this.basicsPanel.add(this.secretKeyLabel, gbcLSecretKey);
        this.basicsPanel.add(this.secretKeyTextField, gbcSecretKey);
        this.basicsPanel.add(this.connTimeOutLabel, gbcLConnTimeOut);
        this.basicsPanel.add(this.connTimeOutTextField, gbcConnTimeOut);
        this.basicsPanel.add(this.readTimeOutLabel, gbcLReadTimeOut);
        this.basicsPanel.add(this.readTimeOutTextField, gbcReadTimeOut);
        this.basicsPanel.add(this.proxyHostLabel, gbcLProxyHost);
        this.basicsPanel.add(this.proxyHostTextField, gbcProxyHost);
        this.basicsPanel.add(this.proxyPortLabel, gbcLProxyPort);
        this.basicsPanel.add(this.proxyPortTextField, gbcProxyPort);
        this.basicsPanel.add(this.remarkLabel, gbcLRemark);
        this.basicsPanel.add(this.remarkTextField, gbcRemark);
        this.basicsPanel.add(this.proxyLabel, gbcLProxy);
        this.basicsPanel.add(this.proxyComboBox, gbcProxy);
        this.basicsPanel.add(this.encodingLabel, gbcLEncoding);
        this.basicsPanel.add(this.encodingComboBox, gbcEncoding);
        this.basicsPanel.add(this.payloadLabel, gbcLPayload);
        this.basicsPanel.add(this.payloadComboBox, gbcPayload);
        this.basicsPanel.add(this.cryptionLabel, gbcLCryption);
        this.basicsPanel.add(this.cryptionComboBox, gbcCryption);
        this.basicsPanel.add(this.setButton, gbcSet);
        this.basicsPanel.add(this.testButton, gbcTest);
        this.headersTextArea = new RTextArea();
        this.rightTextArea = new RTextArea();
        this.leftTextArea = new RTextArea();
        this.headersTextArea.setRows(6);
        this.rightTextArea.setRows(3);
        this.leftTextArea.setRows(3);
        this.headersTextArea.setBorder(new TitledBorder("协议头"));
        this.rightTextArea.setBorder(new TitledBorder("右边追加数据"));
        this.leftTextArea.setBorder(new TitledBorder("左边追加数据"));
        JSplitPane reqSplitPane = new JSplitPane();
        JSplitPane lrSplitPane = new JSplitPane();
        lrSplitPane.setOrientation(0);
        reqSplitPane.setOrientation(0);
        lrSplitPane.setTopComponent(new JScrollPane(this.leftTextArea));
        lrSplitPane.setBottomComponent(new JScrollPane(this.rightTextArea));
        reqSplitPane.setTopComponent(new JScrollPane(this.headersTextArea));
        reqSplitPane.setDividerLocation(0.2d);
        reqSplitPane.setBottomComponent(lrSplitPane);
        this.reqPanel.setLayout(new BorderLayout(1, 1));
        this.reqPanel.add(reqSplitPane);
        addToComboBox(this.proxyComboBox, ApplicationContext.getAllProxy());
        addToComboBox(this.encodingComboBox, ApplicationContext.getAllEncodingTypes());
        addToComboBox(this.payloadComboBox, ApplicationContext.getAllPayload());
        this.payloadComboBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent paramActionEvent) {
                ShellSetting.this.cryptionComboBox.removeAllItems();
                ShellSetting.this.addToComboBox(ShellSetting.this.cryptionComboBox, ApplicationContext.getAllCryption((String) ShellSetting.this.payloadComboBox.getSelectedItem()));
            }
        });
        this.tabbedPane.addTab("基础配置", this.basicsPanel);
        this.tabbedPane.addTab("请求配置", this.reqPanel);
        c.add(this.tabbedPane);
        functions.fireActionEventByJComboBox(this.payloadComboBox);
        initShellContent();
        automaticBindClick.bindJButtonClick(this, this);
        functions.setWindowSize(this, 490, 520);
        setLocationRelativeTo(MainActivity.getFrame());
        setDefaultCloseOperation(2);
        setVisible(true);
    }

    private void initShellContent() {
        if (this.shellId == null || this.shellId.trim().length() <= 0) {
            initAddShellValue();
        } else {
            initUpdateShellValue(this.shellId);
        }
    }

    private void initLabel() {
        Field[] fields = getClass().getDeclaredFields();
        String endString = "Label".toUpperCase();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(JLabel.class)) {
                String labelString = field.getName().toUpperCase();
                if (labelString.endsWith(endString) && labelString.length() > endString.length()) {
                    field.setAccessible(true);
                    try {
                        JLabel label = new JLabel(ShellManage.getCNName(labelString.substring(0, labelString.length() - endString.length())));
                        label.setPreferredSize(this.labelDim);
                        field.set(this, label);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initTextField() {
        Field[] fields = getClass().getDeclaredFields();
        String endString = "TextField".toUpperCase();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(JTextField.class)) {
                String labelString = field.getName().toUpperCase();
                if (labelString.endsWith(endString) && labelString.length() > endString.length()) {
                    field.setAccessible(true);
                    try {
                        JTextField textField = new JTextField();
                        textField.setPreferredSize(this.TextFieldDim);
                        field.set(this, textField);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initComboBox() {
        Field[] fields = getClass().getDeclaredFields();
        String endString = "ComboBox".toUpperCase();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(JComboBox.class)) {
                String labelString = field.getName().toUpperCase();
                if (labelString.endsWith(endString) && labelString.length() > endString.length()) {
                    field.setAccessible(true);
                    try {
                        field.set(this, new JComboBox());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

     
     
    private void addToComboBox(JComboBox<String> comboBox, String[] data) {
        for (String str : data) {
            comboBox.addItem(str);
        }
    }

    private void initAddShellValue() {
        this.shellContext = new ShellEntity();
        this.urlTextField.setText("http://127.0.0.1/shell.jsp");
        this.passwordTextField.setText("pass");
        this.secretKeyTextField.setText("key");
        this.proxyHostTextField.setText("127.0.0.1");
        this.proxyPortTextField.setText("8888");
        this.connTimeOutTextField.setText("60000");
        this.readTimeOutTextField.setText("60000");
        this.remarkTextField.setText("备注");
        this.headersTextArea.setText("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\nAccept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n");
        this.leftTextArea.setText("");
        this.rightTextArea.setText("");
    }

    private void initUpdateShellValue(String id) {
        this.shellContext = Db.getOneShell(id);
        this.urlTextField.setText(this.shellContext.getUrl());
        this.passwordTextField.setText(this.shellContext.getPassword());
        this.secretKeyTextField.setText(this.shellContext.getSecretKey());
        this.proxyHostTextField.setText(this.shellContext.getProxyHost());
        this.proxyPortTextField.setText(Integer.toString(this.shellContext.getProxyPort()));
        this.connTimeOutTextField.setText(Integer.toString(this.shellContext.getConnTimeout()));
        this.readTimeOutTextField.setText(Integer.toString(this.shellContext.getReadTimeout()));
        this.remarkTextField.setText(this.shellContext.getRemark());
        this.headersTextArea.setText(this.shellContext.getHeaderS());
        this.leftTextArea.setText(this.shellContext.getReqLeft());
        this.rightTextArea.setText(this.shellContext.getReqRight());
        this.proxyComboBox.setSelectedItem(this.shellContext.getProxyType());
        this.encodingComboBox.setSelectedItem(this.shellContext.getEncoding());
        this.payloadComboBox.setSelectedItem(this.shellContext.getPayload());
        this.cryptionComboBox.setSelectedItem(this.shellContext.getCryption());
    }

    private void testButtonClick(ActionEvent actionEvent) {
        if (!updateTempShellEntity()) {
            JOptionPane.showMessageDialog(this, this.error, "提示", 2);
            this.error = null;
        } else if (!this.shellContext.initShellOpertion()) {
            JOptionPane.showMessageDialog(this, "initShellOpertion Fail", "提示", 2);
        } else if (this.shellContext.getPayloadModel().test()) {
            JOptionPane.showMessageDialog(this, "Success!", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this, "Payload Test Fail", "提示", 2);
        }
    }

    private void setButtonClick(ActionEvent actionEvent) {
        if (!updateTempShellEntity()) {
            JOptionPane.showMessageDialog(this, this.error, "提示", 2);
            this.error = null;
        } else if (this.shellId == null || this.shellId.trim().length() <= 0) {
            if (Db.addShell(this.shellContext) > 0) {
                JOptionPane.showMessageDialog(this, "添加成功", "提示", 1);
                dispose();
                return;
            }
            JOptionPane.showMessageDialog(this, "添加失败", "提示", 2);
        } else if (Db.updateShell(this.shellContext) > 0) {
            JOptionPane.showMessageDialog(this, "修改成功", "提示", 1);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "修改失败", "提示", 2);
        }
    }

    private boolean updateTempShellEntity() {
        String url = this.urlTextField.getText();
        String password = this.passwordTextField.getText();
        String secretKey = this.secretKeyTextField.getText();
        String payload = (String) this.payloadComboBox.getSelectedItem();
        String cryption = (String) this.cryptionComboBox.getSelectedItem();
        String encoding = (String) this.encodingComboBox.getSelectedItem();
        String headers = this.headersTextArea.getText();
        String reqLeft = this.leftTextArea.getText();
        String reqRight = this.rightTextArea.getText();
        String proxyType = (String) this.proxyComboBox.getSelectedItem();
        String proxyHost = this.proxyHostTextField.getText();
        String remark = this.remarkTextField.getText();
        try {
            int proxyPort = Integer.parseInt(this.proxyPortTextField.getText());
            int connTimeout = Integer.parseInt(this.connTimeOutTextField.getText());
            int readTimeout = Integer.parseInt(this.readTimeOutTextField.getText());
            if (url == null || url.trim().length() <= 0 || password == null || password.trim().length() <= 0 || secretKey == null || secretKey.trim().length() <= 0 || payload == null || payload.trim().length() <= 0 || cryption == null || cryption.trim().length() <= 0 || encoding == null || encoding.trim().length() <= 0) {
                this.error = "请检查  url password secretKey payload cryption encoding 是否填写完整";
                return false;
            }
            ShellEntity shellEntity = this.shellContext;
            if (url == null) {
                url = "";
            }
            shellEntity.setUrl(url);
            ShellEntity shellEntity2 = this.shellContext;
            if (password == null) {
                password = "";
            }
            shellEntity2.setPassword(password);
            ShellEntity shellEntity3 = this.shellContext;
            if (secretKey == null) {
                secretKey = "";
            }
            shellEntity3.setSecretKey(secretKey);
            ShellEntity shellEntity4 = this.shellContext;
            if (payload == null) {
                payload = "";
            }
            shellEntity4.setPayload(payload);
            ShellEntity shellEntity5 = this.shellContext;
            if (cryption == null) {
                cryption = "";
            }
            shellEntity5.setCryption(cryption);
            ShellEntity shellEntity6 = this.shellContext;
            if (encoding == null) {
                encoding = "";
            }
            shellEntity6.setEncoding(encoding);
            ShellEntity shellEntity7 = this.shellContext;
            if (headers == null) {
                headers = "";
            }
            shellEntity7.setHeader(headers);
            ShellEntity shellEntity8 = this.shellContext;
            if (reqLeft == null) {
                reqLeft = "";
            }
            shellEntity8.setReqLeft(reqLeft);
            ShellEntity shellEntity9 = this.shellContext;
            if (reqRight == null) {
                reqRight = "";
            }
            shellEntity9.setReqRight(reqRight);
            this.shellContext.setConnTimeout(connTimeout);
            this.shellContext.setReadTimeout(readTimeout);
            ShellEntity shellEntity10 = this.shellContext;
            if (proxyType == null) {
                proxyType = "";
            }
            shellEntity10.setProxyType(proxyType);
            ShellEntity shellEntity11 = this.shellContext;
            if (proxyHost == null) {
                proxyHost = "";
            }
            shellEntity11.setProxyHost(proxyHost);
            this.shellContext.setProxyPort(proxyPort);
            ShellEntity shellEntity12 = this.shellContext;
            if (remark == null) {
                remark = "";
            }
            shellEntity12.setRemark(remark);
            return true;
        } catch (Exception e) {
            Log.error(e);
            this.error = e.getMessage();
            return false;
        }
    }
}
