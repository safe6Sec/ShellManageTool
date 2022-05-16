package core.ui;

import core.ApplicationContext;
import core.EasyI18N;
import core.annotation.DisplayName;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTabbedPane;
import core.ui.component.ShellBasicsInfo;
import core.ui.component.ShellCopyTab;
import core.ui.component.ShellDatabasePanel;
import core.ui.component.ShellExecCommandPanel;
import core.ui.component.ShellFileManager;
import core.ui.component.ShellNetstat;
import core.ui.component.ShellNote;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import util.Log;
import util.functions;

public class ShellManage extends JFrame {
   private JTabbedPane tabbedPane;
   private ShellEntity shellEntity;
   private ShellExecCommandPanel shellExecCommandPanel;
   private ShellBasicsInfo shellBasicsInfo;
   private ShellFileManager shellFileManager;
   private ShellDatabasePanel shellDatabasePanel;
   private LinkedHashMap<String, Plugin> pluginMap = new LinkedHashMap();
   private LinkedHashMap<String, JPanel> globalComponent = new LinkedHashMap();
   private ArrayList<JPanel> allViews = new ArrayList();
   private Payload payload;
   private ShellCopyTab shellCopyTab;
   private JLabel loadLabel = new JLabel("loading......");
   private static final HashMap<String, String> CN_HASH_MAP = new HashMap();

   public ShellManage(ShellEntity shellEntity) {
      this.shellEntity = shellEntity;
      this.tabbedPane = new RTabbedPane();
      String titleString = String.format("Url:%s Payload:%s Cryption:%s openCache:%s useCache:%s", this.shellEntity.getUrl(), this.shellEntity.getPayload(), this.shellEntity.getCryption(), shellEntity.isUseCache() ? false : ApplicationContext.isOpenCache(), shellEntity.isUseCache());
      this.setTitle(titleString);
      boolean state = this.shellEntity.initShellOpertion();
      if (state) {
         this.init();
      } else {
         this.setTitle("初始化失败");
         GOptionPane.showMessageDialog(this, "初始化失败", "提示", 2);
         this.dispose();
      }

   }

   private void init() {
      this.shellEntity.setFrame(this);
      this.payload = this.shellEntity.getPayloadModule();
      this.add(this.loadLabel);
      functions.setWindowSize(this, 1690, 680);
      this.setLocationRelativeTo(MainActivity.getFrame());
      this.setVisible(true);
      this.setDefaultCloseOperation(2);
      this.initComponent();
   }

   private void initComponent() {
      this.remove(this.loadLabel);
      this.add(this.tabbedPane);
      this.loadGlobalComponent();
      if (!this.shellEntity.isUseCache()) {
         this.loadPlugins();
      }

      this.loadView();
      this.shellCopyTab.scan();
   }

   private void loadView() {
      this.allViews.addAll(this.globalComponent.values());
      Iterator var1 = this.globalComponent.keySet().iterator();

      String key;
      while(var1.hasNext()) {
         key = (String)var1.next();
         JPanel panel = (JPanel)this.globalComponent.get(key);
         EasyI18N.installObject(panel);
         String name = panel.getClass().getSimpleName();
         DisplayName displayName = (DisplayName)panel.getClass().getAnnotation(DisplayName.class);
         if (displayName != null) {
            name = EasyI18N.getI18nString(displayName.DisplayName());
         }

         EasyI18N.installObject(panel);
         this.tabbedPane.addTab(name, (Component)this.globalComponent.get(key));
      }

      var1 = this.pluginMap.keySet().iterator();

      while(var1.hasNext()) {
         key = (String)var1.next();
         Plugin plugin = (Plugin)this.pluginMap.get(key);
         JPanel panel = plugin.getView();
         PluginAnnotation pluginAnnotation = (PluginAnnotation)plugin.getClass().getAnnotation(PluginAnnotation.class);
         if (panel != null) {
            EasyI18N.installObject(plugin);
            EasyI18N.installObject(panel);
            this.tabbedPane.addTab(pluginAnnotation.Name(), panel);
            this.allViews.add(panel);
         }
      }

   }

   public static String getCNName(String name) {
      Iterator var1 = CN_HASH_MAP.keySet().iterator();

      String key;
      do {
         if (!var1.hasNext()) {
            return name;
         }

         key = (String)var1.next();
      } while(!key.toUpperCase().equals(name.toUpperCase()));

      return (String)CN_HASH_MAP.get(key);
   }

   private void loadGlobalComponent() {
      this.shellCopyTab = new ShellCopyTab(this.shellEntity);
      this.globalComponent.put("BasicsInfo", this.shellBasicsInfo = new ShellBasicsInfo(this.shellEntity));
      this.globalComponent.put("ExecCommand", this.shellExecCommandPanel = new ShellExecCommandPanel(this.shellEntity));
      this.globalComponent.put("FileManage", this.shellFileManager = new ShellFileManager(this.shellEntity));
      this.globalComponent.put("DatabaseManage", this.shellDatabasePanel = new ShellDatabasePanel(this.shellEntity));
      this.globalComponent.put("Note", new ShellNote(this.shellEntity));
      this.globalComponent.put("Netstat", new ShellNetstat(this.shellEntity));
      this.globalComponent.put("CopyTab", this.shellCopyTab);
   }

   private String getPluginName(Plugin p) {
      PluginAnnotation pluginAnnotation = (PluginAnnotation)p.getClass().getAnnotation(PluginAnnotation.class);
      return pluginAnnotation.Name();
   }

   public Plugin createPlugin(String pluginName) {
      try {
         Plugin plugin = (Plugin)this.pluginMap.get(pluginName);
         if (plugin != null) {
            plugin = (Plugin)plugin.getClass().newInstance();
            plugin.init(this.shellEntity);
            plugin.getView();
            return plugin;
         }
      } catch (Exception var3) {
         Log.error((Throwable)var3);
      }

      return null;
   }

   public ShellFileManager getShellFileManager() {
      return this.shellFileManager;
   }

   private void loadPlugins() {
      Plugin[] plugins = ApplicationContext.getAllPlugin(this.shellEntity.getPayload());

      Plugin plugin;
      int i;
      for(i = 0; i < plugins.length; ++i) {
         try {
            plugin = plugins[i];
            this.pluginMap.put(this.getPluginName(plugin), plugin);
         } catch (Exception var6) {
            Log.error((Throwable)var6);
         }
      }

      for(i = 0; i < plugins.length; ++i) {
         try {
            plugin = plugins[i];
            plugin.init(this.shellEntity);
         } catch (Exception var5) {
            Log.error((Throwable)var5);
         }
      }

   }

   public Plugin getPlugin(String pluginName) {
      return (Plugin)this.pluginMap.get(pluginName);
   }

   public void dispose() {
      try {
         this.tabbedPane.disable();
         Iterator var1 = this.allViews.iterator();

         while(var1.hasNext()) {
            JPanel jPanel = (JPanel)var1.next();
            if (jPanel.isEnabled()) {
               jPanel.disable();
            }
         }
      } catch (Exception var4) {
         Log.error((Throwable)var4);
      }

      this.close();
      if (this.payload != null && ApplicationContext.isOpenC("isAutoCloseShell")) {
         try {
            Log.log(String.format("CloseShellState: %s\tShellId: %s\tShellHash: %s", this.shellEntity.getPayloadModule().close(), this.shellEntity.getId(), this.shellEntity.hashCode()));
         } catch (Exception var3) {
            Log.error((Throwable)var3);
         }
      }

      super.dispose();
      System.gc();
   }

   public void close() {
      this.pluginMap.keySet().forEach((key) -> {
         Plugin plugin = (Plugin)this.pluginMap.get(key);

         try {
            Method method = functions.getMethodByClass(plugin.getClass(), "closePlugin", (Class[])null);
            if (method != null) {
               method.invoke(plugin, (Object[])null);
            }
         } catch (Exception var4) {
            Log.error((Throwable)var4);
         }

      });
      this.globalComponent.keySet().forEach((key) -> {
         JPanel plugin = (JPanel)this.globalComponent.get(key);

         try {
            Method method = functions.getMethodByClass(plugin.getClass(), "closePlugin", (Class[])null);
            if (method != null) {
               method.invoke(plugin, (Object[])null);
            }
         } catch (Exception var4) {
            Log.error((Throwable)var4);
         }

      });
      this.pluginMap.clear();
      this.globalComponent.clear();
   }

   public LinkedHashMap<String, Plugin> getPluginMap() {
      return this.pluginMap;
   }

   public LinkedHashMap<String, JPanel> getGlobalComponent() {
      return this.globalComponent;
   }

   public JTabbedPane getTabbedPane() {
      return this.tabbedPane;
   }

   static {
      CN_HASH_MAP.put("payload", "有效载荷");
      CN_HASH_MAP.put("secretKey", "密钥");
      CN_HASH_MAP.put("password", "密码");
      CN_HASH_MAP.put("cryption", "加密器");
      CN_HASH_MAP.put("PROXYHOST", "代理主机");
      CN_HASH_MAP.put("PROXYPORT", "代理端口");
      CN_HASH_MAP.put("CONNTIMEOUT", "连接超时");
      CN_HASH_MAP.put("READTIMEOUT", "读取超时");
      CN_HASH_MAP.put("PROXY", "代理类型");
      CN_HASH_MAP.put("REMARK", "备注");
      CN_HASH_MAP.put("ENCODING", "编码");
   }
}
