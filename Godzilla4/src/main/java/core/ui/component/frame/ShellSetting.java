package core.ui.component.frame;

import core.ApplicationContext;
import core.Db;
import core.EasyI18N;
import core.shell.ShellEntity;
import core.ui.MainActivity;
import core.ui.ShellManage;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import core.ui.component.dialog.ChooseGroup;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

public class ShellSetting extends JFrame {
   private final JTabbedPane tabbedPane;
   private final JPanel basicsPanel;
   private final JPanel reqPanel;
   private JTextField urlTextField;
   private JTextField passwordTextField;
   private JTextField secretKeyTextField;
   private JTextField proxyHostTextField;
   private JTextField proxyPortTextField;
   private JTextField connTimeOutTextField;
   private JTextField readTimeOutTextField;
   private JTextField remarkTextField;
   private JTextField groupIdTextField;
   private final RTextArea headersTextArea;
   private final JButton setButton;
   private final JButton testButton;
   private final RTextArea leftTextArea;
   private final RTextArea rightTextArea;
   private JComboBox<String> proxyComboBox;
   private JComboBox<String> cryptionComboBox;
   private JComboBox<String> payloadComboBox;
   private JComboBox<String> encodingComboBox;
   private final Dimension TextFieldDim = new Dimension(200, 23);
   private final Dimension labelDim = new Dimension(150, 23);
   private JLabel urlLabel;
   private JLabel passwordLabel;
   private JLabel secretKeyLabel;
   private JLabel proxyHostLabel;
   private JLabel proxyPortLabel;
   private JLabel connTimeOutLabel;
   private JLabel readTimeOutLabel;
   private JLabel proxyLabel;
   private JLabel remarkLabel;
   private JLabel cryptionLabel;
   private JLabel payloadLabel;
   private JLabel encodingLabel;
   private JLabel groupLabel;
   private ShellEntity shellContext;
   private final String shellId;
   private String error;
   private String currentGroup;

   public ShellSetting(String id, String defaultGroup) {
      super("Shell Setting");
      this.shellId = id;
      this.currentGroup = defaultGroup;
      this.initLabel();
      this.initTextField();
      this.initComboBox();
      Container c = this.getContentPane();
      this.tabbedPane = new JTabbedPane();
      this.basicsPanel = new JPanel();
      this.reqPanel = new JPanel();
      this.basicsPanel.setLayout(new GridBagLayout());
      GBC gbcLUrl = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
      GBC gbcUrl = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLPassword = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
      GBC gbcPassword = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLSecretKey = (new GBC(0, 2)).setInsets(5, -40, 0, 0);
      GBC gbcSecretKey = (new GBC(1, 2, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLConnTimeOut = (new GBC(0, 3)).setInsets(5, -40, 0, 0);
      GBC gbcConnTimeOut = (new GBC(1, 3, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLReadTimeOut = (new GBC(0, 4)).setInsets(5, -40, 0, 0);
      GBC gbcReadTimeOut = (new GBC(1, 4, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLProxyHost = (new GBC(0, 5)).setInsets(5, -40, 0, 0);
      GBC gbcProxyHost = (new GBC(1, 5, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLProxyPort = (new GBC(0, 6)).setInsets(5, -40, 0, 0);
      GBC gbcProxyPort = (new GBC(1, 6, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLRemark = (new GBC(0, 7)).setInsets(5, -40, 0, 0);
      GBC gbcRemark = (new GBC(1, 7, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLGroup = (new GBC(0, 8)).setInsets(5, -40, 0, 0);
      GBC gbcGroup = (new GBC(1, 8, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLProxy = (new GBC(0, 9)).setInsets(5, -40, 0, 0);
      GBC gbcProxy = (new GBC(1, 9, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLEncoding = (new GBC(0, 10)).setInsets(5, -40, 0, 0);
      GBC gbcEncoding = (new GBC(1, 10, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLPayload = (new GBC(0, 11)).setInsets(5, -40, 0, 0);
      GBC gbcPayload = (new GBC(1, 11, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLCryption = (new GBC(0, 12)).setInsets(5, -40, 0, 0);
      GBC gbcCryption = (new GBC(1, 12, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcSet = (new GBC(0, 13, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcTest = (new GBC(1, 13, 3, 1)).setInsets(5, 20, 0, 0);
      this.setButton = new JButton(this.shellId != null && this.shellId.trim().length() > 0 ? "修改" : "添加");
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
      this.basicsPanel.add(this.groupLabel, gbcLGroup);
      this.basicsPanel.add(this.groupIdTextField, gbcGroup);
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
      reqSplitPane.setDividerLocation(0.2);
      reqSplitPane.setBottomComponent(lrSplitPane);
      this.reqPanel.setLayout(new BorderLayout(1, 1));
      this.reqPanel.add(reqSplitPane);
      this.addToComboBox(this.proxyComboBox, ApplicationContext.getAllProxy());
      this.addToComboBox(this.encodingComboBox, ApplicationContext.getAllEncodingTypes());
      this.addToComboBox(this.payloadComboBox, ApplicationContext.getAllPayload());
      this.payloadComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent paramActionEvent) {
            String seleteItemString = (String)ShellSetting.this.payloadComboBox.getSelectedItem();
            ShellSetting.this.cryptionComboBox.removeAllItems();
            ShellSetting.this.addToComboBox(ShellSetting.this.cryptionComboBox, ApplicationContext.getAllCryption(seleteItemString));
         }
      });
      this.groupIdTextField.setEditable(false);
      this.groupIdTextField.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            String group = (new ChooseGroup(UiFunction.getParentWindow(ShellSetting.this.groupIdTextField), ShellSetting.this.groupIdTextField.getText())).getChooseGroup();
            if (group != null) {
               ShellSetting.this.groupIdTextField.setText(group);
            } else {
               Log.log("取消选择......");
            }

         }
      });
      this.tabbedPane.addTab("基础配置", this.basicsPanel);
      this.tabbedPane.addTab("请求配置", this.reqPanel);
      c.add(this.tabbedPane);
      functions.fireActionEventByJComboBox(this.payloadComboBox);
      this.initShellContent();
      automaticBindClick.bindJButtonClick(this, this);
      functions.setWindowSize(this, 490, 520);
      this.setLocationRelativeTo(MainActivity.getFrame());
      this.setDefaultCloseOperation(2);
      EasyI18N.installObject(this);
      this.setVisible(true);
   }

   private void initShellContent() {
      if (this.shellId != null && this.shellId.trim().length() > 0) {
         this.initUpdateShellValue(this.shellId);
      } else {
         this.initAddShellValue();
      }

      this.groupIdTextField.setText(this.currentGroup);
   }

   private void initLabel() {
      Field[] fields = this.getClass().getDeclaredFields();
      String endString = "Label".toUpperCase();

      for(int i = 0; i < fields.length; ++i) {
         Field field = fields[i];
         if (field.getType().isAssignableFrom(JLabel.class)) {
            String labelString = field.getName().toUpperCase();
            if (labelString.endsWith(endString) && labelString.length() > endString.length()) {
               field.setAccessible(true);

               try {
                  JLabel label = new JLabel(ShellManage.getCNName(labelString.substring(0, labelString.length() - endString.length())));
                  label.setPreferredSize(this.labelDim);
                  field.set(this, label);
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }
         }
      }

   }

   private void initTextField() {
      Field[] fields = this.getClass().getDeclaredFields();
      String endString = "TextField".toUpperCase();

      for(int i = 0; i < fields.length; ++i) {
         Field field = fields[i];
         if (field.getType().isAssignableFrom(JTextField.class)) {
            String labelString = field.getName().toUpperCase();
            if (labelString.endsWith(endString) && labelString.length() > endString.length()) {
               field.setAccessible(true);

               try {
                  JTextField textField = new JTextField();
                  textField.setPreferredSize(this.TextFieldDim);
                  field.set(this, textField);
               } catch (Exception var8) {
                  var8.printStackTrace();
               }
            }
         }
      }

   }

   private void initComboBox() {
      Field[] fields = this.getClass().getDeclaredFields();
      String endString = "ComboBox".toUpperCase();

      for(int i = 0; i < fields.length; ++i) {
         Field field = fields[i];
         if (field.getType().isAssignableFrom(JComboBox.class)) {
            String labelString = field.getName().toUpperCase();
            if (labelString.endsWith(endString) && labelString.length() > endString.length()) {
               field.setAccessible(true);

               try {
                  field.set(this, new JComboBox());
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }
         }
      }

   }

   private void addToComboBox(JComboBox<String> comboBox, String[] data) {
      for(int i = 0; i < data.length; ++i) {
         comboBox.addItem(data[i]);
      }

   }

   private void initAddShellValue() {
      this.shellContext = new ShellEntity();
      this.urlTextField.setText("http://127.0.0.1/shell.jsp");
      this.passwordTextField.setText("pass");
      this.secretKeyTextField.setText("key");
      this.proxyHostTextField.setText("127.0.0.1");
      this.proxyPortTextField.setText("8888");
      this.connTimeOutTextField.setText("3000");
      this.readTimeOutTextField.setText("60000");
      this.remarkTextField.setText(EasyI18N.getI18nString("备注"));
      this.headersTextArea.setText("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\nAccept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n");
      this.leftTextArea.setText("");
      this.rightTextArea.setText("");
      if (this.currentGroup == null) {
         this.currentGroup = "/";
      }

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
      if (this.shellId != null && this.currentGroup == null) {
         this.currentGroup = this.shellContext.getGroup();
      }

   }

   private void testButtonClick(ActionEvent actionEvent) {
      if (this.updateTempShellEntity()) {
         if (this.shellContext.initShellOpertion()) {
            GOptionPane.showMessageDialog(this, "Success!", "提示", 1);
            Log.log(String.format("CloseShellState: %s\tShellId: %s\tShellHash: %s", this.shellContext.getPayloadModule().close(), this.shellContext.getId(), this.shellContext.hashCode()));
         } else {
            GOptionPane.showMessageDialog(this, "initShellOpertion Fail", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog(this, this.error, "提示", 2);
         this.error = null;
      }

   }

   private void setButtonClick(ActionEvent actionEvent) {
      this.currentGroup = this.groupIdTextField.getText().trim();
      if (this.updateTempShellEntity()) {
         if (this.shellId != null && this.shellId.trim().length() > 0) {
            if (Db.updateShell(this.shellContext) > 0) {
               this.shellContext.setGroup(this.currentGroup);
               GOptionPane.showMessageDialog(this, "修改成功", "提示", 1);
               this.dispose();
            } else {
               GOptionPane.showMessageDialog(this, "修改失败", "提示", 2);
            }
         } else if (Db.addShell(this.shellContext) > 0) {
            this.shellContext.setGroup(this.currentGroup);
            GOptionPane.showMessageDialog(this, "添加成功", "提示", 1);
            this.dispose();
         } else {
            GOptionPane.showMessageDialog(this, "添加失败", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog(this, this.error, "提示", 2);
         this.error = null;
      }

   }

   private boolean updateTempShellEntity() {
      String url = this.urlTextField.getText();
      String password = this.passwordTextField.getText();
      String secretKey = this.secretKeyTextField.getText();
      String payload = (String)this.payloadComboBox.getSelectedItem();
      String cryption = (String)this.cryptionComboBox.getSelectedItem();
      String encoding = (String)this.encodingComboBox.getSelectedItem();
      String headers = this.headersTextArea.getText();
      String reqLeft = this.leftTextArea.getText();
      String reqRight = this.rightTextArea.getText();
      String proxyType = (String)this.proxyComboBox.getSelectedItem();
      String proxyHost = this.proxyHostTextField.getText();
      String remark = this.remarkTextField.getText();
      int proxyPort = true;
      int connTimeout = true;
      int readTimeout = true;

      int proxyPort;
      int connTimeout;
      int readTimeout;
      try {
         proxyPort = Integer.parseInt(this.proxyPortTextField.getText());
         connTimeout = Integer.parseInt(this.connTimeOutTextField.getText());
         readTimeout = Integer.parseInt(this.readTimeOutTextField.getText());
      } catch (Exception var17) {
         Log.error((Throwable)var17);
         this.error = var17.getMessage();
         return false;
      }

      if (url != null && url.trim().length() > 0 && password != null && password.trim().length() > 0 && secretKey != null && secretKey.trim().length() > 0 && payload != null && payload.trim().length() > 0 && cryption != null && cryption.trim().length() > 0 && encoding != null && encoding.trim().length() > 0) {
         this.shellContext.setUrl(url == null ? "" : url);
         this.shellContext.setPassword(password == null ? "" : password);
         this.shellContext.setSecretKey(secretKey == null ? "" : secretKey);
         this.shellContext.setPayload(payload == null ? "" : payload);
         this.shellContext.setCryption(cryption == null ? "" : cryption);
         this.shellContext.setEncoding(encoding == null ? "" : encoding);
         this.shellContext.setHeader(headers == null ? "" : headers);
         this.shellContext.setReqLeft(reqLeft == null ? "" : reqLeft);
         this.shellContext.setReqRight(reqRight == null ? "" : reqRight);
         this.shellContext.setConnTimeout(connTimeout);
         this.shellContext.setReadTimeout(readTimeout);
         this.shellContext.setProxyType(proxyType == null ? "" : proxyType);
         this.shellContext.setProxyHost(proxyHost == null ? "" : proxyHost);
         this.shellContext.setProxyPort(proxyPort);
         this.shellContext.setRemark(remark == null ? "" : remark);
         return true;
      } else {
         this.error = "请检查  url password secretKey payload cryption encoding 是否填写完整";
         return false;
      }
   }

   public void dispose() {
      super.dispose();
      MainActivity.getMainActivityFrame().refreshShellView();
   }
}
