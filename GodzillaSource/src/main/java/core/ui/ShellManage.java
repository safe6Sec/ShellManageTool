package core.ui;

import core.ApplicationContext;
import core.Db;
import core.annotation.PluginAnnotation;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.ShellBasicsInfo;
import core.ui.component.ShellDatabasePanel;
import core.ui.component.ShellExecCommandPanel;
import core.ui.component.ShellFileManager;
import core.ui.component.ShellNetstat;
import core.ui.component.ShellNote;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import util.Log;
import util.functions;

public class ShellManage extends JFrame {
    private static final HashMap<String, String> CN_HASH_MAP = new HashMap<>();
    private HashMap<String, Plugin> pluginMap = new HashMap<>();
    private ShellBasicsInfo shellBasicsInfo;
    private ShellDatabasePanel shellDatabasePanel;
    private ShellEntity shellEntity;
    private ShellExecCommandPanel shellExecCommandPanel;
    private ShellFileManager shellFileManager;
    private ShellNetstat shellNetstat;
    private JTabbedPane tabbedPane;

    static {
        CN_HASH_MAP.put("MemoryShell", "内存SHELL");
        CN_HASH_MAP.put("JRealCmd", "虚拟终端");
        CN_HASH_MAP.put("CRealCmd", "虚拟终端");
        CN_HASH_MAP.put("Screen", "屏幕截图");
        CN_HASH_MAP.put("CShapDynamicPayload", "ShellCode加载");
        CN_HASH_MAP.put("PZipE", "Zip管理");
        CN_HASH_MAP.put("CZipE", "Zip管理");
        CN_HASH_MAP.put("JZipE", "Zip管理");
        CN_HASH_MAP.put("P_Eval_Code", "代码执行");
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

    public ShellManage(String shellId) {
        this.shellEntity = Db.getOneShell(shellId);
        this.tabbedPane = new JTabbedPane();
        setTitle(String.format("Url:%s 有效载荷:%s 加密器:%s", this.shellEntity.getUrl(), this.shellEntity.getPayload(), this.shellEntity.getCryption()));
        //初始化，发送payload。检查是否能连上
        if (this.shellEntity.initShellOpertion()) {
            init();
            return;
        }
        setTitle("初始化失败");
        JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "初始化失败", "提示", 2);
        dispose();
    }

    private void init() {
        this.shellEntity.setFrame(this);
        initComponent();
    }

    private void initComponent() {
        JTabbedPane jTabbedPane = this.tabbedPane;
        //发送获取基本信息的包
        ShellBasicsInfo shellBasicsInfo2 = new ShellBasicsInfo(this.shellEntity);
        this.shellBasicsInfo = shellBasicsInfo2;
        jTabbedPane.addTab("基础信息", shellBasicsInfo2);
        JTabbedPane jTabbedPane2 = this.tabbedPane;
        ShellExecCommandPanel shellExecCommandPanel2 = new ShellExecCommandPanel(this.shellEntity);
        this.shellExecCommandPanel = shellExecCommandPanel2;
        jTabbedPane2.addTab("命令执行", shellExecCommandPanel2);
        JTabbedPane jTabbedPane3 = this.tabbedPane;
        ShellFileManager shellFileManager2 = new ShellFileManager(this.shellEntity);
        this.shellFileManager = shellFileManager2;
        jTabbedPane3.addTab("文件管理", shellFileManager2);
        JTabbedPane jTabbedPane4 = this.tabbedPane;
        ShellDatabasePanel shellDatabasePanel2 = new ShellDatabasePanel(this.shellEntity);
        this.shellDatabasePanel = shellDatabasePanel2;
        jTabbedPane4.addTab("数据库管理", shellDatabasePanel2);
        this.tabbedPane.addTab("网络详情", new ShellNetstat(this.shellEntity));
        this.tabbedPane.addTab("笔记", new ShellNote(this.shellEntity));
        loadPlugins();
        add(this.tabbedPane);
        functions.setWindowSize(this, 1690, 680);
        setLocationRelativeTo(MainActivity.getFrame());
        setVisible(true);
        setDefaultCloseOperation(2);
    }

    public static String getCNName(String name) {
        for (String key : CN_HASH_MAP.keySet()) {
            if (key.toUpperCase().equals(name.toUpperCase())) {
                return CN_HASH_MAP.get(key);
            }
        }
        return name;
    }

    private String getPluginName(Plugin p) {
        return ((PluginAnnotation) p.getClass().getAnnotation(PluginAnnotation.class)).Name();
    }

    private void loadPlugins() {
        Plugin[] plugins = ApplicationContext.getAllPlugin(this.shellEntity.getPayload());
        for (int i = 0; i < plugins.length; i++) {
            try {
                Plugin plugin = plugins[i];
                plugin.init(this.shellEntity);
                this.tabbedPane.addTab(getCNName(getPluginName(plugin)), plugin.getView());
                this.pluginMap.put(getPluginName(plugin), plugin);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    public Plugin getPlugin(String pluginName) {
        return this.pluginMap.get(pluginName);
    }


    public void dispose() {
        super.dispose();
        this.pluginMap.keySet().forEach((key) -> {
            Plugin plugin = (Plugin)this.pluginMap.get(key);

            try {
                Method method = functions.getMethodByClass(plugin.getClass(), "closePlugin", (Class[])null);
                if (method != null) {
                    method.invoke(plugin, (Object[])null);
                }
            } catch (Exception var4) {
                Log.error(var4);
            }

        });
    }
}
