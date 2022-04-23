//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package core.ui.component.dialog;

import com.formdev.flatlaf.demo.intellijthemes.IJThemeInfo;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import core.ApplicationContext;
import core.Db;
import core.ui.MainActivity;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import core.ui.component.SimplePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class AppSeting extends JDialog {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel globallHttpHeaderPanel;
    private JPanel setFontPanel;
    private JPanel coreConfigPanel;
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
    private JLabel isTipJLabel;
    private JCheckBox isTipCheckBox;
    private RTextArea headerTextArea;
    private JButton updateHeaderButton;
    private JLabel godModeLabel;
    private JCheckBox godModeCheckBox;
    private JSplitPane themesSplitPane;
    private IJThemesPanel themesPanel;
    private JButton updateThemesButton;
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
    private static final HashMap<String, Class<?>> pluginSeting = new HashMap();

    public AppSeting() {
        super(MainActivity.getFrame(), "AppSeting", true);
        this.initSetFontPanel();
        this.initGloballHttpHeader();
        this.initCoreConfigPanel();
        this.initThemesPanel();
        this.initBigFilePanel();
        this.tabbedPane.addTab("全局协议头", this.globallHttpHeaderPanel);
        this.tabbedPane.addTab("字体设置", this.setFontPanel);
        this.tabbedPane.addTab("核心配置", this.coreConfigPanel);
        this.tabbedPane.addTab("UI配置", this.themesSplitPane);
        this.tabbedPane.addTab("大文件配置", this.bigFilePanel);


//        for (String key : pluginSeting.keySet()) {
//
//            this.tabbedPane.addTab(key, (JPanel)(Object)pluginSeting.get(key));
//
//        }

            pluginSeting.keySet().forEach(k -> {
            try {
                this.tabbedPane.addTab(k, (JPanel)((Class)pluginSeting.get(k)).newInstance());
            } catch (InstantiationException var3) {
                var3.printStackTrace();
            } catch (IllegalAccessException var4) {
                var4.printStackTrace();
            }

        });
        this.add(this.tabbedPane);
        automaticBindClick.bindJButtonClick(this, this);
        functions.setWindowSize(this, 650, 500);
        this.setLocationRelativeTo(MainActivity.getFrame());
        this.setVisible(true);
    }

    void initSetFontPanel() {
        Font currentFont = ApplicationContext.getFont();
        this.setFontPanel = new JPanel(new GridBagLayout());
        this.fontNameComboBox = new JComboBox(getAllFontName());
        this.fontTypeComboBox = new JComboBox(getAllFontType());
        this.fontSizeComboBox = new JComboBox(getAllFontSize());
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
            this.fontTypeComboBox.setSelectedItem(this.getFontType(currentFont.getStyle()));
            this.fontSizeComboBox.setSelectedItem(Integer.toString(currentFont.getSize()));
            this.testFontLabel.setFont(currentFont);
        }

    }

    public static void registerPluginSeting(String tabName, Class<?> panelClass) {
        pluginSeting.put(tabName, panelClass);
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
        splitPane.setResizeWeight(0.9D);
        this.globallHttpHeaderPanel.add(splitPane);
    }

    void initCoreConfigPanel() {
        this.coreConfigPanel = new JPanel(new GridBagLayout());
        this.godModeLabel = new JLabel("运行模式: ");
        this.godModeCheckBox = new JCheckBox("上帝模式", ApplicationContext.isGodMode());
        this.isTipJLabel = new JLabel("提示语");
        this.isTipCheckBox = new JCheckBox("开启", functions.toBoolean(Db.getSetingValue("AppIsTip")));
        GBC gbcLGodMode = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
        GBC gbcGodMode = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLIsTip = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
        GBC gbcIsTip = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
        this.coreConfigPanel.add(this.godModeLabel, gbcLGodMode);
        this.coreConfigPanel.add(this.godModeCheckBox, gbcGodMode);
        this.coreConfigPanel.add(this.isTipJLabel, gbcLIsTip);
        this.coreConfigPanel.add(this.isTipCheckBox, gbcIsTip);
        this.isTipCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Db.updateSetingKV("AppIsTip", Boolean.toString(AppSeting.this.isTipCheckBox.isSelected()))) {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改成功!", "提示", 1);
                } else {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改失败!", "提示", 2);
                }

            }
        });
        this.godModeCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ApplicationContext.setGodMode(AppSeting.this.godModeCheckBox.isSelected())) {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改成功!", "提示", 1);
                } else {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改失败!", "提示", 2);
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
        this.bigFilePanel.addX(new Component[]{this.bigFileErrorRetryNumLabel, this.bigFileErrorRetryNumTextField});
        this.bigFilePanel.addX(new Component[]{this.bigFileSendRequestSleepLabel, this.bigFileSendRequestSleepTextField});
        this.bigFilePanel.addX(new Component[]{this.oneceBigFileDownloadByteNumLabel, this.oneceBigFileDownloadByteNumTextField});
        this.bigFilePanel.addX(new Component[]{this.oneceBigFileUploadByteNumLabel, this.oneceBigFileUploadByteNumTextField});
        this.bigFilePanel.addX(new Component[]{this.bigFileConfigSaveButton});
    }

    public void initThemesPanel() {
        this.themesPanel = new IJThemesPanel();
        this.updateThemesButton = new JButton("修改");
        this.themesSplitPane = new JSplitPane(0);
        this.themesSplitPane.setBottomComponent(this.updateThemesButton);
        this.themesSplitPane.setTopComponent(this.themesPanel);
        this.themesSplitPane.setResizeWeight(0.99D);
    }

    public Font getSelectFont() {
        try {
            String fontName = (String)this.fontNameComboBox.getSelectedItem();
            String fontType = (String)this.fontTypeComboBox.getSelectedItem();
            int fontSize = Integer.parseInt((String)this.fontSizeComboBox.getSelectedItem());
            Font font = new Font(fontName, Font.class.getDeclaredField(fontType).getInt((Object)null), fontSize);
            return font;
        } catch (Exception var5) {
            Log.error(var5);
            return null;
        }
    }

    public String getFontType(int type) {
        try {
            Field[] fields = Font.class.getDeclaredFields();
            Field[] var3 = fields;
            int var4 = fields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                if (field.getType().isAssignableFrom(Integer.TYPE) && field.getName().indexOf("_") == -1 && field.getModifiers() == 25 && field.getInt((Object)null) == type) {
                    return field.getName();
                }
            }

            return null;
        } catch (Exception var7) {
            Log.error(var7);
            return null;
        }
    }

    public static String[] getAllFontName() {
        ArrayList<String> arrayList = new ArrayList();
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = e.getAllFonts();
        Font[] var3 = fonts;
        int var4 = fonts.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Font font = var3[var5];
            arrayList.add(font.getFontName());
        }

        return (String[])arrayList.toArray(new String[0]);
    }

    public static String[] getAllFontType() {
        ArrayList<String> arrayList = new ArrayList();
        Field[] fields = Font.class.getDeclaredFields();
        Field[] var2 = fields;
        int var3 = fields.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Field field = var2[var4];
            if (field.getType().isAssignableFrom(Integer.TYPE) && field.getName().indexOf("_") == -1 && field.getModifiers() == 25) {
                arrayList.add(field.getName());
            }
        }

        return (String[])arrayList.toArray(new String[0]);
    }

    public static String[] getAllFontSize() {
        ArrayList<String> arrayList = new ArrayList();

        for(int i = 8; i < 48; ++i) {
            arrayList.add(Integer.toString(i));
        }

        return (String[])arrayList.toArray(new String[0]);
    }

    private void resetFontButtonClick(ActionEvent actionEvent) {
        ApplicationContext.resetFont();
        JOptionPane.showMessageDialog(this, "重置成功! 重启程序生效!", "提示", 1);
    }

    private void updateHeaderButtonClick(ActionEvent actionEvent) {
        String header = this.headerTextArea.getText();
        if (ApplicationContext.updateGloballHttpHeader(header)) {
            JOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
        }

    }

    private void updateThemesButtonClick(ActionEvent actionEvent) {
        IJThemeInfo ijThemeInfo = this.themesPanel.getSelect();
        if (ijThemeInfo != null && ApplicationContext.saveUi(ijThemeInfo)) {
            JOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
        }

    }

    private void bigFileConfigSaveButtonClick(ActionEvent actionEvent) throws Exception {
        Db.updateSetingKV("oneceBigFileDownloadByteNum", this.oneceBigFileDownloadByteNumTextField.getText().trim());
        Db.updateSetingKV("oneceBigFileUploadByteNum", this.oneceBigFileUploadByteNumTextField.getText().trim());
        Db.updateSetingKV("bigFileErrorRetryNum", String.valueOf(this.bigFileErrorRetryNumTextField.getText().trim()));
        Db.updateSetingKV("bigFileSendRequestSleep", String.valueOf(this.bigFileSendRequestSleepTextField.getText().trim()));
        JOptionPane.showMessageDialog(this, "Succes!", "提示", 1);
    }
}