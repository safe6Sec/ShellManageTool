package core;

import core.ui.component.dialog.ImageShowDialog;
import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import util.Log;
import util.functions;

public class ApplicationConfig {

    public static final String GIT = "https://github.com/BeichenDream/Godzilla";
    private static final String GITEE_CONFIG_URL = "https://gitee.com/beichendram/Godzilla/raw/master/%s";
    private static final String GIT_CONFIG_URL = "https://raw.githubusercontent.com/BeichenDream/Godzilla/master/%s";
    private static String ACCESS_URL = "https://gitee.com/beichendram/Godzilla/raw/master/%s";
    private static final HashMap<String, String> headers = new HashMap<>();

    static {
        headers.put("Accept", "*/*");
        headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
    }

    public static void invoke() {
        HashMap<String, String> configMap = null;
        try {
            configMap = getAppConfig(String.format(GITEE_CONFIG_URL, "application.config"));
            ACCESS_URL = GITEE_CONFIG_URL;
        } catch (Exception e) {
            try {
                configMap = getAppConfig(String.format(GIT_CONFIG_URL, "application.config"));
                ACCESS_URL = GIT_CONFIG_URL;
            } catch (Exception e2) {
                Log.error("Network connection failure");
            }
        }
        try {
            String hashString = getAppConfig(String.format(ACCESS_URL, "hashsumJar")).get(ApplicationContext.VERSION);
            File jarFile = functions.getCurrentJarFile();
            String jarHashString = new String();
            if (jarFile != null) {
                FileInputStream inputStream = new FileInputStream(jarFile);
                byte[] jar = functions.readInputStream(inputStream);
                inputStream.close();
                jarHashString = functions.SHA(jar, "SHA-512");
            }
            if (hashString == null) {
                String tipString = String.format("未找到当前版本(%s)的Hash\r\n当前Hash:%s\r\n你使用的软件可能已被病毒感染   文件哈希效验失败", ApplicationContext.VERSION, jarHashString);
                JOptionPane.showMessageDialog((Component) null, tipString, String.format("警告\t当前版本:%s", ApplicationContext.VERSION), 2);
                Log.error(String.format(tipString, ApplicationContext.VERSION));
                System.exit(0);
            } else if (jarFile == null) {
                return;
            } else {
                if (!jarHashString.equals(hashString)) {
                    String tipString2 = String.format("你使用的软件可能已被病毒感染   文件哈希效验失败\r\n效验Jar哈希:%s\r\n本地Jar哈希:%s", hashString, jarHashString);
                    JOptionPane.showMessageDialog((Component) null, tipString2, String.format("警告\t当前版本:", ApplicationContext.VERSION), 2);
                    Log.error(String.format(tipString2, hashString, jarHashString));
                    System.exit(0);
                } else {
                    Log.error(String.format("效验Hash成功   Hash Url:%s\r\n效验Jar哈希:%s\r\n本地Jar哈希:%s", String.format(ACCESS_URL, "hashsumJar"), hashString, jarHashString));
                }
            }
        } catch (Exception e3) {
            Log.error(e3);
        }
        if (configMap != null && configMap.size() > 0) {
            String version = configMap.get("currentVersion");
            boolean isShowGroup = Boolean.valueOf(configMap.get("isShowGroup")).booleanValue();
            String wxGroupImageUrl = configMap.get("wxGroupImageUrl");
            String showGroupTitle = configMap.get("showGroupTitle");
            String gitUrl = configMap.get("gitUrl");
            boolean isShowAppTip = Boolean.valueOf(configMap.get("isShowAppTip")).booleanValue();
            String appTip = configMap.get("appTip");
            if (version != null && wxGroupImageUrl != null && appTip != null && gitUrl != null) {
                if (functions.stringToint(version.replace(".", "")) > functions.stringToint(ApplicationContext.VERSION.replace(".", ""))) {
                    JOptionPane.showMessageDialog((Component) null, String.format("新版本已经发布\n当前版本:%s\n最新版本:%s", ApplicationContext.VERSION, version), "message", 2);
                    functions.openBrowseUrl(gitUrl);
                }
                if (isShowAppTip) {
                    JOptionPane.showMessageDialog((Component) null, appTip, "message", 1);
                }
                if (isShowGroup) {
                    try {
                        ImageShowDialog.showImageDiaolog(new ImageIcon(ImageIO.read(new ByteArrayInputStream(functions.httpReqest(wxGroupImageUrl, "GET", headers, null)))), showGroupTitle);
                    } catch (IOException e4) {
                        Log.error(e4);
                        Log.error("showGroup fail!");
                    }
                }
            }
        }
    }

    private static HashMap<String, String> getAppConfig(String configUrl) throws Exception {
        String configString;
        byte[] result = functions.httpReqest(configUrl, "GET", headers, null);
        if (result == null) {
            throw new Exception("readApplication Fail!");
        }
        try {
            configString = new String(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            configString = new String(result);
        }
        HashMap<String, String> hashMap = new HashMap<>();
        String[] lines = configString.split("\n");
        for (String line : lines) {
            int index = line.indexOf(58);
            if (index != -1) {
                hashMap.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
            }
        }
        return hashMap;
    }
}
