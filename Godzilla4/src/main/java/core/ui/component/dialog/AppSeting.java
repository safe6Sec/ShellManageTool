package core.ui.component.dialog;

import com.formdev.flatlaf.demo.intellijthemes.IJThemeInfo;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import core.ApplicationContext;
import core.Db;
import core.EasyI18N;
import core.ui.MainActivity;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import core.ui.component.SimplePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import util.Log;
import util.OpenC;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

public class AppSeting extends JDialog {
   private final JTabbedPane tabbedPane = new JTabbedPane();
   private JPanel globallHttpHeaderPanel;
   private JPanel setFontPanel;
   private JPanel coreConfigPanel;
   private SimplePanel globalProxyPanel;
   private SimplePanel httpsCertConfigPanel;
   private JComboBox<String> fontNameComboBox;
   private JComboBox<String> fontTypeComboBox;
   private JComboBox<String> fontSizeComboBox;
   private JLabel testFontLabel;
   private JLabel currentFontLabel;
   private JButton updateFontButton;
   private JButton resetFontButton;
   private JLabel fontNameLabel;
   private JLabel fontTypeLabel;
   private JLabel fontSizeLabel;
   private JLabel currentFontLLabel;
   private RTextArea headerTextArea;
   private JButton updateHeaderButton;
   private JLabel godModeLabel;
   private JCheckBox godModeCheckBox;
   private JLabel execCommandModeLabel;
   private JComboBox<String> execCommandModeComboBox;
   private JLabel languageLabel;
   private JComboBox<String> languageComboBox;
   private JCheckBox isOpenCacheCheckBox;
   private JLabel isSuperLogLabel;
   private JLabel isOpenCacheLabel;
   private JCheckBox isSuperLogCheckBox;
   private JLabel superRequestLabel;
   private JButton superRequestButton;
   private JLabel isAutoCloseShellLabel;
   private JCheckBox isAutoCloseShellCheckBox;
   private int currentCoreConfigPanelComponent = 0;
   private JLabel globalProxyTypeLabel;
   private JLabel globalProxyHostLabel;
   private JLabel globalProxyPortLabel;
   private JTextField globalProxyHostTextField;
   private JTextField globalProxyPortTextField;
   private JComboBox<String> globalProxyTypeComboBox;
   private JButton updateGlobalProxyButton;
   private JButton httpsCertConfigExportButton;
   private JButton httpsCertConfigResetButton;
   private SimplePanel bigFilePanel;
   private JLabel bigFileErrorRetryNumLabel;
   private JLabel oneceBigFileUploadByteNumLabel;
   private JLabel oneceBigFileDownloadByteNumLabel;
   private JLabel bigFileSendRequestSleepLabel;
   private JTextField oneceBigFileUploadByteNumTextField;
   private JTextField bigFileErrorRetryNumTextField;
   private JTextField bigFileSendRequestSleepTextField;
   private JTextField oneceBigFileDownloadByteNumTextField;
   private JButton bigFileConfigSaveButton;
   private JSplitPane themesSplitPane;
   private IJThemesPanel themesPanel;
   private JButton updateThemesButton;
   private static final HashMap<String, Class<?>> pluginSeting = new HashMap();

   public AppSeting() {
      super(MainActivity.getFrame(), "AppSeting", true);
      this.initSetFontPanel();
      this.initGloballHttpHeader();
      this.initCoreConfigPanel();
      this.initGlobalProxy();
      this.initHttpsCertConfig();
      this.initBigFilePanel();
      this.initThemesPanel();
      this.tabbedPane.addTab("全局协议头", this.globallHttpHeaderPanel);
      this.tabbedPane.addTab("全局代理", this.globalProxyPanel);
      this.tabbedPane.addTab("代理证书配置", this.httpsCertConfigPanel);
      this.tabbedPane.addTab("字体设置", this.setFontPanel);
      this.tabbedPane.addTab("核心配置", this.coreConfigPanel);
      this.tabbedPane.addTab("大文件配置", this.bigFilePanel);
      this.tabbedPane.addTab("UI配置", this.themesSplitPane);
      pluginSeting.keySet().forEach((k) -> {
         try {
            JPanel panel = (JPanel)((Class)pluginSeting.get(k)).newInstance();
            EasyI18N.installObject(panel);
            this.tabbedPane.addTab(k, panel);
         } catch (InstantiationException var3) {
            var3.printStackTrace();
         } catch (IllegalAccessException var4) {
            var4.printStackTrace();
         }

      });
      this.add(this.tabbedPane);
      automaticBindClick.bindJButtonClick(this, this);
      functions.setWindowSize(this, 1200, 500);
      this.setLocationRelativeTo(MainActivity.getFrame());
      EasyI18N.installObject(this);
      this.setVisible(true);
   }

   void initGlobalProxy() {
      this.globalProxyPanel = new SimplePanel();
      this.updateGlobalProxyButton = new JButton("保存更新");
      this.globalProxyHostLabel = new JLabel("代理主机");
      this.globalProxyPortLabel = new JLabel("代理端口");
      this.globalProxyTypeLabel = new JLabel("代理类型");
      this.globalProxyHostTextField = new JTextField(Db.tryGetSetingValue("globalProxyHost", "127.0.0.1"), 10);
      this.globalProxyPortTextField = new JTextField(Db.tryGetSetingValue("globalProxyPort", "8888"), 7);
      this.globalProxyTypeComboBox = new JComboBox(ApplicationContext.getAllProxy());
      this.globalProxyTypeComboBox.setSelectedItem(Db.tryGetSetingValue("globalProxyType", "NO_PROXY"));
      this.globalProxyTypeComboBox.removeItem("GLOBAL_PROXY");
      this.globalProxyPanel.setSetup(-270);
      this.globalProxyPanel.addX(this.globalProxyTypeLabel, this.globalProxyTypeComboBox);
      this.globalProxyPanel.addX(this.globalProxyHostLabel, this.globalProxyHostTextField);
      this.globalProxyPanel.addX(this.globalProxyPortLabel, this.globalProxyPortTextField);
      this.globalProxyPanel.addX(this.updateGlobalProxyButton);
   }

   void initSetFontPanel() {
      Font currentFont = ApplicationContext.getFont();
      this.setFontPanel = new JPanel(new GridBagLayout());
      this.fontNameComboBox = new JComboBox(UiFunction.getAllFontName());
      this.fontTypeComboBox = new JComboBox(UiFunction.getAllFontType());
      this.fontSizeComboBox = new JComboBox(UiFunction.getAllFontSize());
      this.testFontLabel = new JLabel("你好\tHello");
      this.currentFontLabel = new JLabel(functions.toString(currentFont));
      this.currentFontLLabel = new JLabel("当前字体 : ");
      this.updateFontButton = new JButton("修改");
      this.resetFontButton = new JButton("重置");
      this.fontNameLabel = new JLabel("字体:    ");
      this.fontTypeLabel = new JLabel("字体类型 : ");
      this.fontSizeLabel = new JLabel("字体大小 : ");
      GBC gbcLFontName = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
      GBC gbcFontName = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLFontType = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
      GBC gbcFontType = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLFontSize = (new GBC(0, 2)).setInsets(5, -40, 0, 0);
      GBC gbcFontSize = (new GBC(1, 2, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcLCurrentFont = (new GBC(0, 3)).setInsets(5, -40, 0, 0);
      GBC gbcCurrentFont = (new GBC(1, 3, 3, 1)).setInsets(5, 20, 0, 0);
      GBC gbcTestFont = new GBC(0, 4);
      GBC gbcUpdateFont = (new GBC(2, 5)).setInsets(5, -40, 0, 0);
      GBC gbcResetFont = (new GBC(1, 5, 3, 1)).setInsets(5, 20, 0, 0);
      this.setFontPanel.add(this.fontNameLabel, gbcLFontName);
      this.setFontPanel.add(this.fontNameComboBox, gbcFontName);
      this.setFontPanel.add(this.fontTypeLabel, gbcLFontType);
      this.setFontPanel.add(this.fontTypeComboBox, gbcFontType);
      this.setFontPanel.add(this.fontSizeLabel, gbcLFontSize);
      this.setFontPanel.add(this.fontSizeComboBox, gbcFontSize);
      this.setFontPanel.add(this.currentFontLLabel, gbcLCurrentFont);
      this.setFontPanel.add(this.currentFontLabel, gbcCurrentFont);
      this.setFontPanel.add(this.testFontLabel, gbcTestFont);
      this.setFontPanel.add(this.updateFontButton, gbcUpdateFont);
      this.setFontPanel.add(this.resetFontButton, gbcResetFont);
      this.fontNameComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent paramActionEvent) {
            AppSeting.this.testFontLabel.setFont(AppSeting.this.getSelectFont());
         }
      });
      this.fontTypeComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent paramActionEvent) {
            AppSeting.this.testFontLabel.setFont(AppSeting.this.getSelectFont());
         }
      });
      this.fontSizeComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent paramActionEvent) {
            AppSeting.this.testFontLabel.setFont(AppSeting.this.getSelectFont());
         }
      });
      if (currentFont != null) {
         this.fontNameComboBox.setSelectedItem(currentFont.getName());
         this.fontTypeComboBox.setSelectedItem(UiFunction.getFontType(currentFont));
         this.fontSizeComboBox.setSelectedItem(Integer.toString(currentFont.getSize()));
         this.testFontLabel.setFont(currentFont);
      }

   }

   void initHttpsCertConfig() {
      this.httpsCertConfigPanel = new SimplePanel();
      this.httpsCertConfigPanel.setSetup(200);
      this.httpsCertConfigExportButton = new JButton("导出证书");
      this.httpsCertConfigResetButton = new JButton("重置证书");
      this.httpsCertConfigPanel.addX(this.httpsCertConfigExportButton, this.httpsCertConfigResetButton);
   }

   void initGloballHttpHeader() {
      this.globallHttpHeaderPanel = new JPanel(new BorderLayout(1, 1));
      this.headerTextArea = new RTextArea();
      this.updateHeaderButton = new JButton("修改");
      this.headerTextArea.setText(ApplicationContext.getGloballHttpHeader());
      Dimension dimension = new Dimension();
      dimension.height = 30;
      JSplitPane splitPane = new JSplitPane();
      splitPane.setOrientation(0);
      JPanel bottomPanel = new JPanel();
      splitPane.setTopComponent(new JScrollPane(this.headerTextArea));
      bottomPanel.add(this.updateHeaderButton);
      bottomPanel.setMaximumSize(dimension);
      bottomPanel.setMinimumSize(dimension);
      splitPane.setBottomComponent(bottomPanel);
      splitPane.setResizeWeight(0.9);
      this.globallHttpHeaderPanel.add(splitPane);
   }

   public void initThemesPanel() {
      this.themesPanel = new IJThemesPanel();
      this.updateThemesButton = new JButton("修改");
      this.themesSplitPane = new JSplitPane(0);
      this.themesSplitPane.setBottomComponent(this.updateThemesButton);
      this.themesSplitPane.setTopComponent(this.themesPanel);
      this.themesSplitPane.setResizeWeight(0.99);
   }

   void addCoreConfigPanelComponent(JLabel label, Component component) {
      GBC gbcl = (new GBC(0, this.currentCoreConfigPanelComponent)).setInsets(5, -40, 0, 0);
      GBC gbc = (new GBC(1, this.currentCoreConfigPanelComponent, 3, 1)).setInsets(5, 20, 0, 0);
      this.coreConfigPanel.add(label, gbcl);
      this.coreConfigPanel.add(component, gbc);
      ++this.currentCoreConfigPanelComponent;
   }

   void initCoreConfigPanel() {
      this.coreConfigPanel = new JPanel(new GridBagLayout());
      this.godModeLabel = new JLabel("运行模式: ");
      this.godModeCheckBox = new JCheckBox("上帝模式", ApplicationContext.isGodMode());
      this.execCommandModeLabel = new JLabel("命令执行模式: ");
      this.execCommandModeComboBox = new JComboBox(new String[]{"EASY", "KNIFE", "NO_MODE"});
      this.languageLabel = new JLabel("语言");
      this.languageComboBox = new JComboBox(new String[]{"en", "zh"});
      this.isOpenCacheLabel = new JLabel("开启缓存");
      this.isOpenCacheCheckBox = new JCheckBox("开启", ApplicationContext.isOpenCache());
      this.isSuperLogLabel = new JLabel("详细日志: ");
      this.isSuperLogCheckBox = new JCheckBox("开启", ApplicationContext.isOpenC("isSuperLog"));
      this.isAutoCloseShellLabel = new JLabel("自动关闭Shell");
      this.isAutoCloseShellCheckBox = new JCheckBox("开启", ApplicationContext.isOpenC("isAutoCloseShell"));
      this.superRequestLabel = new JLabel("请求参数配置: ");
      this.superRequestButton = new JButton("config");
      this.addCoreConfigPanelComponent(this.godModeLabel, this.godModeCheckBox);
      this.addCoreConfigPanelComponent(this.execCommandModeLabel, this.execCommandModeComboBox);
      this.addCoreConfigPanelComponent(this.languageLabel, this.languageComboBox);
      this.addCoreConfigPanelComponent(this.isSuperLogLabel, this.isSuperLogCheckBox);
      this.addCoreConfigPanelComponent(this.isOpenCacheLabel, this.isOpenCacheCheckBox);
      this.addCoreConfigPanelComponent(this.isAutoCloseShellLabel, this.isAutoCloseShellCheckBox);
      this.addCoreConfigPanelComponent(this.superRequestLabel, this.superRequestButton);
      this.isSuperLogCheckBox.addActionListener(new OpenC("isSuperLog", this.isSuperLogCheckBox));
      this.isAutoCloseShellCheckBox.addActionListener(new OpenC("isAutoCloseShell", this.isAutoCloseShellCheckBox));
      this.execCommandModeComboBox.setSelectedItem(Db.getSetingValue("EXEC_COMMAND_MODE", "EASY"));
      this.languageComboBox.setSelectedItem(Db.getSetingValue("language", "zh".equalsIgnoreCase(Locale.getDefault().getLanguage()) ? "zh" : "en"));
      this.godModeCheckBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (ApplicationContext.setGodMode(AppSeting.this.godModeCheckBox.isSelected())) {
               GOptionPane.showMessageDialog((Component)null, "修改成功!", "提示", 1);
            } else {
               GOptionPane.showMessageDialog((Component)null, "修改失败!", "提示", 2);
            }

         }
      });
      this.execCommandModeComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Db.updateSetingKV("EXEC_COMMAND_MODE", AppSeting.this.execCommandModeComboBox.getSelectedItem().toString())) {
               GOptionPane.showMessageDialog((Component)null, "修改成功!", "提示", 1);
            } else {
               GOptionPane.showMessageDialog((Component)null, "修改失败!", "提示", 2);
            }

         }
      });
      this.languageComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Db.updateSetingKV("language", AppSeting.this.languageComboBox.getSelectedItem().toString())) {
               GOptionPane.showMessageDialog((Component)null, "修改成功!", "提示", 1);
            } else {
               GOptionPane.showMessageDialog((Component)null, "修改失败!", "提示", 2);
            }

         }
      });
      this.isOpenCacheCheckBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (ApplicationContext.setOpenCache(AppSeting.this.isOpenCacheCheckBox.isSelected())) {
               GOptionPane.showMessageDialog((Component)null, "修改成功!", "提示", 1);
            } else {
               GOptionPane.showMessageDialog((Component)null, "修改失败!", "提示", 2);
            }

         }
      });
   }

   void initBigFilePanel() {
      this.bigFilePanel = new SimplePanel();
      this.bigFileErrorRetryNumLabel = new JLabel("错误重试最大次数: ");
      this.bigFileSendRequestSleepLabel = new JLabel("请求抖动延时(ms)");
      this.oneceBigFileDownloadByteNumLabel = new JLabel("下载单次读取字节: ");
      this.oneceBigFileUploadByteNumLabel = new JLabel("上传单次读取字节: ");
      this.oneceBigFileDownloadByteNumTextField = new JTextField(String.valueOf(Db.getSetingIntValue("oneceBigFileDownloadByteNum", 1048576)), 10);
      this.oneceBigFileUploadByteNumTextField = new JTextField(String.valueOf(Db.getSetingIntValue("oneceBigFileUploadByteNum", 1048576)), 10);
      this.bigFileErrorRetryNumTextField = new JTextField(String.valueOf(Db.getSetingIntValue("bigFileErrorRetryNum", 10)));
      this.bigFileSendRequestSleepTextField = new JTextField(String.valueOf(Db.getSetingIntValue("bigFileSendRequestSleep", 521)));
      this.bigFileConfigSaveButton = new JButton("保存配置");
      this.bigFilePanel.setSetup(-270);
      this.bigFilePanel.addX(this.bigFileErrorRetryNumLabel, this.bigFileErrorRetryNumTextField);
      this.bigFilePanel.addX(this.bigFileSendRequestSleepLabel, this.bigFileSendRequestSleepTextField);
      this.bigFilePanel.addX(this.oneceBigFileDownloadByteNumLabel, this.oneceBigFileDownloadByteNumTextField);
      this.bigFilePanel.addX(this.oneceBigFileUploadByteNumLabel, this.oneceBigFileUploadByteNumTextField);
      this.bigFilePanel.addX(this.bigFileConfigSaveButton);
   }

   public Font getSelectFont() {
      try {
         String fontName = (String)this.fontNameComboBox.getSelectedItem();
         String fontType = (String)this.fontTypeComboBox.getSelectedItem();
         int fontSize = Integer.parseInt((String)this.fontSizeComboBox.getSelectedItem());
         Font font = new Font(fontName, UiFunction.getFontType(fontType), fontSize);
         return font;
      } catch (Exception var5) {
         Log.error((Throwable)var5);
         return null;
      }
   }

   private void updateFontButtonClick(ActionEvent actionEvent) {
      ApplicationContext.setFont(this.getSelectFont());
      GOptionPane.showMessageDialog(this, "修改成功! 重启程序生效!", "提示", 1);
   }

   private void resetFontButtonClick(ActionEvent actionEvent) {
      ApplicationContext.resetFont();
      GOptionPane.showMessageDialog(this, "重置成功! 重启程序生效!", "提示", 1);
   }

   private void updateHeaderButtonClick(ActionEvent actionEvent) {
      String header = this.headerTextArea.getText();
      if (ApplicationContext.updateGloballHttpHeader(header)) {
         GOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
      } else {
         GOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
      }

   }

   private void superRequestButtonClick(ActionEvent actionEvent) {
      new ShellSuperRequest();
   }

   private void updateGlobalProxyButtonClick(ActionEvent actionEvent) {
      try {
         String globalProxyHostString = this.globalProxyHostTextField.getText().trim();
         String globalProxyPortString = this.globalProxyPortTextField.getText().trim();
         String globalProxyTypeString = this.globalProxyTypeComboBox.getSelectedItem().toString().trim();
         Db.updateSetingKV("globalProxyType", globalProxyTypeString);
         Db.updateSetingKV("globalProxyHost", globalProxyHostString);
         Db.updateSetingKV("globalProxyPort", globalProxyPortString);
         GOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
      } catch (Exception var5) {
         GOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
      }

   }

   private void httpsCertConfigExportButtonClick(ActionEvent actionEvent) throws Exception {
      byte[] cert = ApplicationContext.getHttpsCert().getEncoded();
      GFileChooser chooser = new GFileChooser();
      chooser.setFileSelectionMode(0);
      boolean flag = 0 == chooser.showDialog(new JLabel(), "选择");
      File selectdFile = chooser.getSelectedFile();
      if (flag && selectdFile != null) {
         if (!selectdFile.getName().endsWith(".crt")) {
            selectdFile = new File(selectdFile.getCanonicalPath() + ".crt");
         }

         FileOutputStream fileOutputStream = new FileOutputStream(selectdFile);
         fileOutputStream.write(cert);
         fileOutputStream.flush();
         fileOutputStream.close();
         GOptionPane.showMessageDialog(this, String.format("Succes! cert >> %s", selectdFile.getCanonicalPath()), "提示", 1);
      } else {
         GOptionPane.showMessageDialog(this, "未选中文件路径", "提示", 2);
      }

   }

   private void httpsCertConfigResetButtonClick(ActionEvent actionEvent) throws Exception {
      try {
         ApplicationContext.genHttpsConfig();
         GOptionPane.showMessageDialog(this, "Succes!", "提示", 1);
      } catch (Exception var3) {
         GOptionPane.showMessageDialog(this, var3.getMessage(), "提示", 2);
      }

   }

   private void bigFileConfigSaveButtonClick(ActionEvent actionEvent) throws Exception {
      Db.updateSetingKV("oneceBigFileDownloadByteNum", this.oneceBigFileDownloadByteNumTextField.getText().trim());
      Db.updateSetingKV("oneceBigFileUploadByteNum", this.oneceBigFileUploadByteNumTextField.getText().trim());
      Db.updateSetingKV("bigFileErrorRetryNum", String.valueOf(this.bigFileErrorRetryNumTextField.getText().trim()));
      Db.updateSetingKV("bigFileSendRequestSleep", String.valueOf(this.bigFileSendRequestSleepTextField.getText().trim()));
      GOptionPane.showMessageDialog(this, "Succes!", "提示", 1);
   }

   private void updateThemesButtonClick(ActionEvent actionEvent) {
      IJThemeInfo ijThemeInfo = this.themesPanel.getSelect();
      if (ijThemeInfo != null && ApplicationContext.saveUi(ijThemeInfo)) {
         GOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
      } else {
         GOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
      }

   }

   public static void registerPluginSeting(String tabName, Class<?> panelClass) {
      pluginSeting.put(tabName, panelClass);
   }
}
