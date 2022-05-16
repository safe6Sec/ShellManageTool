package core.ui;

import core.ApplicationConfig;
import core.ApplicationContext;
import core.Db;
import core.EasyI18N;
import core.shell.ShellEntity;
import core.ui.component.DataView;
import core.ui.component.ShellGroup;
import core.ui.component.dialog.AppSeting;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.dialog.GenerateShellLoder;
import core.ui.component.dialog.PluginManage;
import core.ui.component.frame.LiveScan;
import core.ui.component.frame.ShellSetting;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class MainActivity extends JFrame {
   private static MainActivity mainActivityFrame;
   private static JMenuBar menuBar;
   private JMenu targetMenu;
   private JMenu aboutMenu;
   private JMenu attackMenu;
   private JMenu configMenu;
   private static JMenu pluginMenu;
   private DataView shellView;
   private JScrollPane shellViewScrollPane;
   private static JPopupMenu shellViewPopupMenu;
   private Vector<String> columnVector;
   private JSplitPane splitPane;
   private ShellGroup shellGroupTree;
   private String currentGroup;
   private JLabel statusLabel;

   private static void initStatic() {
      menuBar = new JMenuBar();
      pluginMenu = new JMenu("插件");
      shellViewPopupMenu = new JPopupMenu();
   }

   public MainActivity() {
      ApplicationContext.init();
      this.initVariable();
      EasyI18N.installObject(this);
   }

   private void initVariable() {
      this.setTitle(EasyI18N.getI18nString("哥斯拉   V%s by: BeichenDream Github:https://github.com/BeichenDream/Godzilla", "4.01"));
      this.setLayout(new BorderLayout(2, 2));
      this.currentGroup = "/";
      this.statusLabel = new JLabel("status");
      Vector<Vector<String>> rows = Db.getAllShell();
      this.columnVector = (Vector)rows.get(0);
      rows.remove(0);
      this.shellView = new DataView((Vector)null, this.columnVector, -1, -1);
      this.refreshShellView();
      this.shellView.setSelectionMode(2);
      this.splitPane = new JSplitPane(1);
      this.shellGroupTree = new ShellGroup();
      this.splitPane.setLeftComponent(new JScrollPane(this.shellGroupTree));
      this.splitPane.setRightComponent(this.shellViewScrollPane = new JScrollPane(this.shellView));
      this.add(this.splitPane);
      this.add(this.statusLabel, "South");
      this.targetMenu = new JMenu("目标");
      JMenuItem addShellMenuItem = new JMenuItem("添加");
      addShellMenuItem.setActionCommand("addShell");
      this.targetMenu.add(addShellMenuItem);
      this.attackMenu = new JMenu("管理");
      JMenuItem shellLiveScanMenuItem = new JMenuItem("存活扫描");
      shellLiveScanMenuItem.setActionCommand("shellLiveScan");
      JMenuItem generateShellMenuItem = new JMenuItem("生成");
      generateShellMenuItem.setActionCommand("generateShell");
      this.attackMenu.add(generateShellMenuItem);
      this.attackMenu.add(shellLiveScanMenuItem);
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
      automaticBindClick.bindMenuItemClick(this.targetMenu, (Map)null, this);
      automaticBindClick.bindMenuItemClick(this.attackMenu, (Map)null, this);
      automaticBindClick.bindMenuItemClick(this.configMenu, (Map)null, this);
      automaticBindClick.bindMenuItemClick(this.aboutMenu, (Map)null, this);
      this.shellGroupTree.setActionDbclick((e) -> {
         this.currentGroup = this.shellGroupTree.GetSelectFile().trim();
         this.refreshShellView();
      });
      menuBar.add(this.targetMenu);
      menuBar.add(this.attackMenu);
      menuBar.add(this.configMenu);
      menuBar.add(this.aboutMenu);
      menuBar.add(pluginMenu);
      this.setJMenuBar(menuBar);
      JMenuItem copyselectItem = new JMenuItem("复制选中");
      copyselectItem.setActionCommand("copyShellViewSelected");
      JMenuItem interactMenuItem = new JMenuItem("进入");
      interactMenuItem.setActionCommand("interact");
      JMenuItem interactCacheMenuItem = new JMenuItem("进入缓存");
      interactCacheMenuItem.setActionCommand("interactCache");
      JMenuItem removeShell = new JMenuItem("移除");
      removeShell.setActionCommand("removeShell");
      JMenuItem editShell = new JMenuItem("编辑");
      editShell.setActionCommand("editShell");
      JMenuItem refreshShell = new JMenuItem("刷新");
      refreshShell.setActionCommand("refreshShellView");
      shellViewPopupMenu.add(interactMenuItem);
      shellViewPopupMenu.add(interactCacheMenuItem);
      shellViewPopupMenu.add(copyselectItem);
      shellViewPopupMenu.add(removeShell);
      shellViewPopupMenu.add(editShell);
      shellViewPopupMenu.add(refreshShell);
      this.shellView.setRightClickMenu(shellViewPopupMenu);
      automaticBindClick.bindMenuItemClick(shellViewPopupMenu, (Map)null, this);
      this.addEasterEgg();
      functions.setWindowSize(this, 1500, 600);
      this.setLocationRelativeTo((Component)null);
      this.setVisible(true);
      this.setDefaultCloseOperation(3);
   }

   private void addEasterEgg() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
         public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == 112 && ApplicationContext.easterEgg) {
               ApplicationContext.easterEgg = false;
               GOptionPane.showMessageDialog(MainActivity.getFrame(), EasyI18N.getI18nString("Hacker技术学的再好, 却无法入侵你的心,\n服务器入侵的再多,对你只有Guest,\n是我的DDOS造成了你的拒绝服务？\n还是我的WebShell再次被你查杀？\n你总有防火墙\n我始终停不掉\n想提权\n无奈JSP+MYSQL成为我们的障碍\n找不到你的注入点\n扫不出你的空口令\n所有对我的回应都用3DES加密\n你总是自定义文件格式\n我永远找不到你的入口点\n忽略所有异常\n却还是跟踪不到你的注册码\n是你太过完美,还是我太菜?\n虽然我们是不同的对象,都有隐私的一面,\n但我相信总有一天我会找到你的接口,把我的最真给你看!\n因为我是你的指针,在茫茫内存的堆栈中, 永远指向你那片天空,不孜不倦!\n我愿做你的内联,供你无限次的调用,直到海枯石烂!\n我愿做你的引用,和你同进退共生死,一起经受考验!\n只是我不愿苦苦地调试你的心情,最终沦为你的友元!\n如今我们已被MFC封装--事事变迁!\n如今我们已向COM走去--可想当年!\n没任何奢求,只愿做你最后的System!\n渗透玩的再强,我也不能提权进你的心\n免杀玩的再狠,我也过不了你的主防御\n外挂写的再叼,我也不能操控你对我的爱\n编程玩的再好,我也不能写出完美的爱情\n纵使我多么的不可一世,也不是你的System\n提权了再多的服务器，却永远成不了你的Root\n**But...... **\n那怕你的心再强大，我有0day在手\n主动防御再牛，我有R0\n击败你只是时间问题, 就算能操控，你的心早已经不属于我\n已被千人DownLoad\n完美的爱情写出来能怎样，终究会像游戏一样结束\n不是你的System也罢，但我有Guest用户，早晚提权进入你的管理员组\n\n也许，像你说的那样，我们是不同世界的人，因为我是乞丐而不是骑士\n人变了，是因为心跟着生活在变\n人生有梦，各自精彩\n燕雀安知鸿鹄之志!"), "提示", -1);
               return true;
            } else {
               return false;
            }
         }
      });
   }

   private void addShellMenuItemClick(ActionEvent e) {
      new ShellSetting((String)null, this.currentGroup);
      this.refreshShellView();
   }

   private void generateShellMenuItemClick(ActionEvent e) {
      new GenerateShellLoder();
   }

   private void shellLiveScanMenuItemClick(ActionEvent e) {
      new LiveScan(this.currentGroup);
   }

   private void pluginConfigMenuItemClick(ActionEvent e) {
      new PluginManage();
   }

   private void appConfigMenuItemClick(ActionEvent e) {
      new AppSeting();
   }

   private void aboutMenuItemClick(ActionEvent e) {
      GOptionPane.showMessageDialog(getFrame(), EasyI18N.getI18nString("由BeichenDream强力驱动\nMail:beichendream@gmail.com"), "About", -1);
   }

   private void copyShellViewSelectedMenuItemClick(ActionEvent e) {
      int columnIndex = this.shellView.getSelectedColumn();
      if (columnIndex != -1) {
         Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), this.shellView.getSelectedColumn());
         if (o != null) {
            String value = (String)o;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), (ClipboardOwner)null);
            GOptionPane.showMessageDialog(getMainActivityFrame(), "复制成功", "提示", 1);
         } else {
            GOptionPane.showMessageDialog(getMainActivityFrame(), "选中列是空的", "提示", 2);
         }
      } else {
         GOptionPane.showMessageDialog(getMainActivityFrame(), "未选中列", "提示", 2);
      }

   }

   private void removeShellMenuItemClick(ActionEvent e) {
      String[] shellIds = this.getSlectedShellId();
      if (shellIds.length > 0) {
         int n = GOptionPane.showConfirmDialog(getMainActivityFrame(), String.format(EasyI18N.getI18nString("确定删除id在 %s 的shell吗?"), Arrays.toString(shellIds)), "警告", 0);
         if (n == 0) {
            for(int i = 0; i < shellIds.length; ++i) {
               String shellId = shellIds[i];
               String shshellInfo = Db.getOneShell(shellId).toString();
               Log.log("removeShell status:%s  -> %s", Db.removeShell(shellId) > 0, shshellInfo);
            }

            GOptionPane.showMessageDialog(getMainActivityFrame(), "删除成功", "提示", 1);
            this.refreshShellView();
         } else if (n == 1) {
            GOptionPane.showMessageDialog(getMainActivityFrame(), "已取消");
         }
      }

   }

   private String[] getSlectedShellId() {
      int[] rows = this.shellView.getSelectedRows();
      String[] shellIds = new String[rows.length];

      for(int i = 0; i < shellIds.length; ++i) {
         shellIds[i] = (String)this.shellView.getValueAt(rows[i], 0);
      }

      return shellIds;
   }

   private void editShellMenuItemClick(ActionEvent e) {
      String[] shellIds = this.getSlectedShellId();
      if (shellIds.length > 0) {
         for(int i = 0; i < shellIds.length; ++i) {
            String shellId = shellIds[i];
            new ShellSetting(shellId, this.currentGroup);
         }
      }

   }

   private void interactMenuItemClick(ActionEvent e) {
      try {
         String shellId = (String)this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
         new ShellManage(Db.getOneShell(shellId));
      } catch (Throwable var5) {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         PrintStream printStream = new PrintStream(stream);
         var5.printStackTrace(printStream);
         printStream.flush();
         printStream.close();
         GOptionPane.showMessageDialog(getMainActivityFrame(), new String(stream.toByteArray()));
      }

   }

   private void interactCacheMenuItemClick(ActionEvent e) {
      String shellId = (String)this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);

      try {
         if ((new File(String.format("%s/%s/cache.db", "GodzillaCache", shellId))).isFile()) {
            ShellEntity shellEntity = Db.getOneShell(shellId);
            shellEntity.setUseCache(true);
            new ShellManage(shellEntity);
         } else {
            GOptionPane.showMessageDialog(getMainActivityFrame(), "缓存文件不存在");
         }
      } catch (Throwable var6) {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         PrintStream printStream = new PrintStream(stream);
         var6.printStackTrace(printStream);
         printStream.flush();
         printStream.close();
         GOptionPane.showMessageDialog(getMainActivityFrame(), new String(stream.toByteArray()));
      }

   }

   public void refreshShellView() {
      Vector<Vector<String>> rowsVector = null;
      if (this.currentGroup.equals("/")) {
         rowsVector = Db.getAllShell();
      } else {
         rowsVector = Db.getAllShell(this.currentGroup);
      }

      rowsVector.remove(0);
      this.shellView.AddRows(rowsVector);
      this.shellView.getModel().fireTableDataChanged();
      this.statusLabel.setText(String.format(EasyI18N.getI18nString("共有%d组 所有成员数:%d 当前组是:%s 当前组成员数:%d "), Db.getAllGroup().size(), Db.getAllShell().size() - 1, this.currentGroup, rowsVector.size()));
   }

   private void refreshShellViewMenuItemClick(ActionEvent e) {
      this.refreshShellView();
   }

   public MainActivity getJFrame() {
      return this;
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
      } catch (Exception var2) {
         Log.error((Throwable)var2);
      }

      initStatic();
      ApplicationConfig.invoke();
      MainActivity activity = new MainActivity();
      mainActivityFrame = activity.getJFrame();
   }
}
