package core.ui;

import core.ApplicationConfig;
import core.ApplicationContext;
import core.Db;
import core.ui.component.DataView;
import core.ui.component.dialog.AppSeting;
import core.ui.component.dialog.GenerateShellLoder;
import core.ui.component.dialog.PluginManage;
import core.ui.component.dialog.ShellSetting;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class MainActivity extends JFrame {
    private static MainActivity mainActivityFrame;
    private static JMenuBar menuBar;
    private static JMenu pluginMenu;
    private static JPopupMenu shellViewPopupMenu;
    private JMenu aboutMenu;
    private JMenu attackMenu;
    private Vector<String> columnVector;
    private JMenu configMenu;
    private DataView shellView;
    private JScrollPane shellViewScrollPane;
    private JMenu targetMenu;

    private static void initStatic() {
        menuBar = new JMenuBar();
        pluginMenu = new JMenu("插件");
        shellViewPopupMenu = new JPopupMenu();
    }

    public MainActivity() {
        ApplicationContext.init();
        try {
            ApplicationConfig.invoke();
//            if (functions.toBoolean(Db.getSetingValue("AppIsTip"))) {
//                JOptionPane.showMessageDialog(getFrame(), "1.程序仅限服务器管理，切勿用于非法用途，非法使用造成的一切后果由自己承担，与本作者无关\n2.由于用户滥用造成的一切后果与作者无关。\n3.用户请自觉遵守当地法律法规，出现一切后果本项目作者概不负责\n4.本软件不得用于商业用途，仅做学习交流", "如果您使用本软件默认同意以下协议条款.如果您不同意本协议的条款 ,否则请立即关闭并删除本软件.", 2);
//            }
        } catch (Exception e) {
            Log.error(e);
        }
        initVariable();
    }

    private void initVariable() {
        setTitle(String.format("哥斯拉\t V%s by: BeichenDream Github:https://github.com/BeichenDream/Godzilla", ApplicationContext.VERSION));
        setLayout(new BorderLayout(1, 1));
        Vector<Vector<String>> rows = Db.getAllShell();
        this.columnVector = rows.get(0);
        rows.remove(0);
        this.shellView = new DataView(null, this.columnVector, -1, -1);
        this.shellView.AddRows(rows);
        JScrollPane jScrollPane = new JScrollPane(this.shellView);
        this.shellViewScrollPane = jScrollPane;
        add(jScrollPane);
        this.targetMenu = new JMenu("目标");
        JMenuItem addShellMenuItem = new JMenuItem("添加");
        addShellMenuItem.setActionCommand("addShell");
        this.targetMenu.add(addShellMenuItem);
        this.attackMenu = new JMenu("管理");
        JMenuItem generateShellMenuItem = new JMenuItem("生成");
        generateShellMenuItem.setActionCommand("generateShell");
        this.attackMenu.add(generateShellMenuItem);
        this.configMenu = new JMenu("配置");
        JMenuItem pluginConfigMenuItem = new JMenuItem("插件配置");
        pluginConfigMenuItem.setActionCommand("pluginConfig");
        JMenuItem appConfigMenuItem = new JMenuItem("程序配置");
        appConfigMenuItem.setActionCommand("appConfig");
        this.configMenu.add(appConfigMenuItem);
        this.configMenu.add(pluginConfigMenuItem);
        this.aboutMenu = new JMenu("关于");
        JMenuItem aboutMenuItem = new JMenuItem("关于");
        aboutMenuItem.setActionCommand("about");
        this.aboutMenu.add(aboutMenuItem);
        automaticBindClick.bindMenuItemClick(this.targetMenu, null, this);
        automaticBindClick.bindMenuItemClick(this.attackMenu, null, this);
        automaticBindClick.bindMenuItemClick(this.configMenu, null, this);
        automaticBindClick.bindMenuItemClick(this.aboutMenu, null, this);
        menuBar.add(this.targetMenu);
        menuBar.add(this.attackMenu);
        menuBar.add(this.configMenu);
        menuBar.add(this.aboutMenu);
        menuBar.add(pluginMenu);
        setJMenuBar(menuBar);
        JMenuItem copyselectItem = new JMenuItem("复制选中");
        copyselectItem.setActionCommand("copyShellViewSelected");
        JMenuItem interactMenuItem = new JMenuItem("进入");
        interactMenuItem.setActionCommand("interact");
        JMenuItem removeShell = new JMenuItem("移除");
        removeShell.setActionCommand("removeShell");
        JMenuItem editShell = new JMenuItem("编辑");
        editShell.setActionCommand("editShell");
        JMenuItem copyShellMenuItem = new JMenuItem("复制当前shell");
        copyShellMenuItem.setActionCommand("copyShell");
        JMenuItem refreshShell = new JMenuItem("刷新");
        refreshShell.setActionCommand("refreshShellView");
        shellViewPopupMenu.add(interactMenuItem);
        shellViewPopupMenu.add(copyselectItem);
        shellViewPopupMenu.add(removeShell);
        shellViewPopupMenu.add(editShell);
        shellViewPopupMenu.add(copyShellMenuItem);
        shellViewPopupMenu.add(refreshShell);
        this.shellView.setRightClickMenu(shellViewPopupMenu);
        automaticBindClick.bindMenuItemClick(shellViewPopupMenu, null, this);
        addEasterEgg();
        functions.setWindowSize(this, 1500, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(3);
    }

    private void addEasterEgg() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
             

            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() != 112 || !ApplicationContext.easterEgg) {
                    return false;
                }
                ApplicationContext.easterEgg = false;
                JOptionPane.showMessageDialog(MainActivity.getFrame(), "Hacker技术学的再好, 却无法入侵你的心,\n服务器入侵的再多,对你只有Guest,\n是我的DDOS造成了你的拒绝服务？\n还是我的WebShell再次被你查杀？\n你总有防火墙\n我始终停不掉\n想提权\n无奈JSP+MYSQL成为我们的障碍\n找不到你的注入点\n扫不出你的空口令\n所有对我的回应都用3DES加密\n你总是自定义文件格式\n我永远找不到你的入口点\n忽略所有异常\n却还是跟踪不到你的注册码\n是你太过完美,还是我太菜?\n虽然我们是不同的对象,都有隐私的一面,\n但我相信总有一天我会找到你的接口,把我的最真给你看!\n因为我是你的指针,在茫茫内存的堆栈中, 永远指向你那片天空,不孜不倦!\n我愿做你的内联,供你无限次的调用,直到海枯石烂!\n我愿做你的引用,和你同进退共生死,一起经受考验!\n只是我不愿苦苦地调试你的心情,最终沦为你的友元!\n如今我们已被MFC封装--事事变迁!\n如今我们已向COM走去--可想当年!\n没任何奢求,只愿做你最后的System!\n渗透玩的再强,我也不能提权进你的心\n免杀玩的再狠,我也过不了你的主防御\n外挂写的再叼,我也不能操控你对我的爱\n编程玩的再好,我也不能写出完美的爱情\n纵使我多么的不可一世,也不是你的System\n提权了再多的服务器，却永远成不了你的Root\n**But...... **\n那怕你的心再强大，我有0day在手\n主动防御再牛，我有R0\n击败你只是时间问题, 就算能操控，你的心早已经不属于我\n已被千人DownLoad\n完美的爱情写出来能怎样，终究会像游戏一样结束\n不是你的System也罢，但我有Guest用户，早晚提权进入你的管理员组\n\n也许，像你说的那样，我们是不同世界的人，因为我是乞丐而不是骑士\n人变了，是因为心跟着生活在变\n人生有梦，各自精彩\n燕雀安知鸿鹄之志!", "提示", -1);
                return true;
            }
        });
    }

    private void addShellMenuItemClick(ActionEvent e) {
        new ShellSetting(null);
        refreshShellView();
    }

    private void generateShellMenuItemClick(ActionEvent e) {
        new GenerateShellLoder();
    }

    private void pluginConfigMenuItemClick(ActionEvent e) {
        new PluginManage();
    }

    private void appConfigMenuItemClick(ActionEvent e) {
        new AppSeting();
    }

    private void aboutMenuItemClick(ActionEvent e) {
        JOptionPane.showMessageDialog(getFrame(), "下一代Webshell技术\n\t由BeichenDream强力驱动\n邮箱:beichendream@gmail.com", "About", -1);
        functions.openBrowseUrl(ApplicationConfig.GIT);
    }

    private void copyShellViewSelectedMenuItemClick(ActionEvent e) {
        if (this.shellView.getSelectedColumn() != -1) {
            Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), this.shellView.getSelectedColumn());
            if (o != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection((String) o), (ClipboardOwner) null);
                JOptionPane.showMessageDialog(getMainActivityFrame(), "复制成功", "提示", 1);
                return;
            }
            JOptionPane.showMessageDialog(getMainActivityFrame(), "选中列是空的", "提示", 2);
            return;
        }
        JOptionPane.showMessageDialog(getMainActivityFrame(), "未选中列", "提示", 2);
    }

    private void removeShellMenuItemClick(ActionEvent e) {
        Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
        if (o != null && o.getClass().isAssignableFrom(String.class)) {
            String shellId = (String) o;
            int n = JOptionPane.showConfirmDialog((Component) null, String.format("确定删除id为 %s 的shell吗?", shellId), "警告", 0);
            if (n == 0) {
                if (Db.removeShell(shellId) > 0) {
                    JOptionPane.showMessageDialog(getMainActivityFrame(), "删除成功", "提示", 1);
                    refreshShellView();
                    return;
                }
                JOptionPane.showMessageDialog(getMainActivityFrame(), "删除失败", "提示", 2);
            } else if (n == 1) {
                JOptionPane.showMessageDialog(new JFrame(), "已取消");
            }
        }
    }

    private void editShellMenuItemClick(ActionEvent e) {
        Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
        if (o != null && o.getClass().isAssignableFrom(String.class)) {
            new ShellSetting((String) o);
            refreshShellView();
        }
    }

    private void interactMenuItemClick(ActionEvent e) {
        new ShellManage((String) this.shellView.getValueAt(this.shellView.getSelectedRow(), 0));
    }

    private void copyShellMenuItemClick(ActionEvent e) {
        String shellId = (String) this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
        if (shellId != null) {
            Db.addShell(Db.getOneShell(shellId));
            JOptionPane.showMessageDialog(this, "复制成功");
            refreshShellView();
            return;
        }
        JOptionPane.showMessageDialog(this, "已取消");
    }

    public void refreshShellView() {
        Vector<Vector<String>> rowsVector = Db.getAllShell();
        rowsVector.remove(0);
        this.shellView.AddRows(rowsVector);
        this.shellView.getModel().fireTableDataChanged();
    }

    private void refreshShellViewMenuItemClick(ActionEvent e) {
        refreshShellView();
    }

    public MainActivity getJFrame() {
        return mainActivityFrame;
    }

    public static MainActivity getFrame() {
        return mainActivityFrame;
    }

    public static JMenuItem registerPluginJMenuItem(JMenuItem menuItem) {
        return pluginMenu.add(menuItem);
    }

    public static void registerPluginPopMenu(PopupMenu popupMenu) {
        pluginMenu.add(popupMenu);
    }

    public static JMenu registerJMenu(JMenu menu) {
        return menuBar.add(menu);
    }

    public static JMenuItem registerShellViewJMenuItem(JMenuItem menuItem) {
        return shellViewPopupMenu.add(menuItem);
    }

    public static void registerShellViewPopupMenu(PopupMenu popupMenu) {
        shellViewPopupMenu.add(popupMenu);
    }

    public static MainActivity getMainActivityFrame() {
        return mainActivityFrame;
    }

    public static void main(String[] args) {
        try {
            ApplicationContext.initUi();
        } catch (Exception e) {
            Log.error(e);
        }
        initStatic();
        mainActivityFrame = new MainActivity();
    }
}
