package util;

import java.io.File;

public class SystemUtils {
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    private static final String USER_HOME_KEY = "user.home";
    private static final String USER_NAME_KEY = "user.name";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";
    private static final String JAVA_HOME_KEY = "java.home";
    public static final String AWT_TOOLKIT = getSystemProperty("awt.toolkit");
    public static final String FILE_ENCODING = getSystemProperty("file.encoding");
    @Deprecated
    public static final String FILE_SEPARATOR = getSystemProperty("file.separator");
    public static final boolean IS_JAVA_10 = getJavaVersionMatches("10");
    public static final boolean IS_JAVA_11 = getJavaVersionMatches("11");
    public static final boolean IS_JAVA_12 = getJavaVersionMatches("12");
    public static final boolean IS_JAVA_13 = getJavaVersionMatches("13");
    public static final boolean IS_JAVA_14 = getJavaVersionMatches("14");
    public static final boolean IS_JAVA_15 = getJavaVersionMatches("15");
    public static final boolean IS_JAVA_1_1 = getJavaVersionMatches("1.1");
    public static final boolean IS_JAVA_1_2 = getJavaVersionMatches("1.2");
    public static final boolean IS_JAVA_1_3 = getJavaVersionMatches("1.3");
    public static final boolean IS_JAVA_1_4 = getJavaVersionMatches("1.4");
    public static final boolean IS_JAVA_1_5 = getJavaVersionMatches("1.5");
    public static final boolean IS_JAVA_1_6 = getJavaVersionMatches("1.6");
    public static final boolean IS_JAVA_1_7 = getJavaVersionMatches("1.7");
    public static final boolean IS_JAVA_1_8 = getJavaVersionMatches("1.8");
    @Deprecated
    public static final boolean IS_JAVA_1_9 = getJavaVersionMatches("9");
    public static final boolean IS_JAVA_9 = getJavaVersionMatches("9");
    public static final boolean IS_OS_400 = getOsMatchesName("OS/400");
    public static final boolean IS_OS_AIX = getOsMatchesName("AIX");
    public static final boolean IS_OS_FREE_BSD = getOsMatchesName("FreeBSD");
    public static final boolean IS_OS_HP_UX = getOsMatchesName("HP-UX");
    public static final boolean IS_OS_IRIX = getOsMatchesName("Irix");
    public static final boolean IS_OS_LINUX;
    public static final boolean IS_OS_MAC = getOsMatchesName("Mac");
    public static final boolean IS_OS_MAC_OSX = getOsMatchesName("Mac OS X");
    public static final boolean IS_OS_MAC_OSX_CHEETAH = getOsMatches("Mac OS X", "10.0");
    public static final boolean IS_OS_MAC_OSX_EL_CAPITAN = getOsMatches("Mac OS X", "10.11");
    public static final boolean IS_OS_MAC_OSX_JAGUAR = getOsMatches("Mac OS X", "10.2");
    public static final boolean IS_OS_MAC_OSX_LEOPARD = getOsMatches("Mac OS X", "10.5");
    public static final boolean IS_OS_MAC_OSX_LION = getOsMatches("Mac OS X", "10.7");
    public static final boolean IS_OS_MAC_OSX_MAVERICKS = getOsMatches("Mac OS X", "10.9");
    public static final boolean IS_OS_MAC_OSX_MOUNTAIN_LION = getOsMatches("Mac OS X", "10.8");
    public static final boolean IS_OS_MAC_OSX_PANTHER = getOsMatches("Mac OS X", "10.3");
    public static final boolean IS_OS_MAC_OSX_PUMA = getOsMatches("Mac OS X", "10.1");
    public static final boolean IS_OS_MAC_OSX_SNOW_LEOPARD = getOsMatches("Mac OS X", "10.6");
    public static final boolean IS_OS_MAC_OSX_TIGER = getOsMatches("Mac OS X", "10.4");
    public static final boolean IS_OS_MAC_OSX_YOSEMITE = getOsMatches("Mac OS X", "10.10");
    public static final boolean IS_OS_NET_BSD = getOsMatchesName("NetBSD");
    public static final boolean IS_OS_OPEN_BSD = getOsMatchesName("OpenBSD");
    public static final boolean IS_OS_OS2 = getOsMatchesName("OS/2");
    public static final boolean IS_OS_SOLARIS = getOsMatchesName("Solaris");
    public static final boolean IS_OS_SUN_OS = getOsMatchesName("SunOS");
    public static final boolean IS_OS_UNIX;
    public static final boolean IS_OS_WINDOWS = getOsMatchesName(OS_NAME_WINDOWS_PREFIX);
    public static final boolean IS_OS_WINDOWS_10 = getOsMatchesName("Windows 10");
    public static final boolean IS_OS_WINDOWS_2000 = getOsMatchesName("Windows 2000");
    public static final boolean IS_OS_WINDOWS_2003 = getOsMatchesName("Windows 2003");
    public static final boolean IS_OS_WINDOWS_2008 = getOsMatchesName("Windows Server 2008");
    public static final boolean IS_OS_WINDOWS_2012 = getOsMatchesName("Windows Server 2012");
    public static final boolean IS_OS_WINDOWS_7 = getOsMatchesName("Windows 7");
    public static final boolean IS_OS_WINDOWS_8 = getOsMatchesName("Windows 8");
    public static final boolean IS_OS_WINDOWS_95 = getOsMatchesName("Windows 95");
    public static final boolean IS_OS_WINDOWS_98 = getOsMatchesName("Windows 98");
    public static final boolean IS_OS_WINDOWS_ME = getOsMatchesName("Windows Me");
    public static final boolean IS_OS_WINDOWS_NT = getOsMatchesName("Windows NT");
    public static final boolean IS_OS_WINDOWS_VISTA = getOsMatchesName("Windows Vista");
    public static final boolean IS_OS_WINDOWS_XP = getOsMatchesName("Windows XP");
    public static final boolean IS_OS_ZOS = getOsMatchesName("z/OS");
    public static final String JAVA_AWT_FONTS = getSystemProperty("java.awt.fonts");
    public static final String JAVA_AWT_GRAPHICSENV = getSystemProperty("java.awt.graphicsenv");
    public static final String JAVA_AWT_HEADLESS = getSystemProperty("java.awt.headless");
    public static final String JAVA_AWT_PRINTERJOB = getSystemProperty("java.awt.printerjob");
    public static final String JAVA_CLASS_PATH = getSystemProperty("java.class.path");
    public static final String JAVA_CLASS_VERSION = getSystemProperty("java.class.version");
    public static final String JAVA_COMPILER = getSystemProperty("java.compiler");
    public static final String JAVA_ENDORSED_DIRS = getSystemProperty("java.endorsed.dirs");
    public static final String JAVA_EXT_DIRS = getSystemProperty("java.ext.dirs");
    public static final String JAVA_HOME = getSystemProperty(JAVA_HOME_KEY);
    public static final String JAVA_IO_TMPDIR = getSystemProperty(JAVA_IO_TMPDIR_KEY);
    public static final String JAVA_LIBRARY_PATH = getSystemProperty("java.library.path");
    public static final String JAVA_RUNTIME_NAME = getSystemProperty("java.runtime.name");
    public static final String JAVA_RUNTIME_VERSION = getSystemProperty("java.runtime.version");
    public static final String JAVA_SPECIFICATION_NAME = getSystemProperty("java.specification.name");
    public static final String JAVA_SPECIFICATION_VENDOR = getSystemProperty("java.specification.vendor");
    public static final String JAVA_SPECIFICATION_VERSION = getSystemProperty("java.specification.version");
    private static final JavaVersion JAVA_SPECIFICATION_VERSION_AS_ENUM = JavaVersion.get(JAVA_SPECIFICATION_VERSION);
    public static final String JAVA_UTIL_PREFS_PREFERENCES_FACTORY = getSystemProperty("java.util.prefs.PreferencesFactory");
    public static final String JAVA_VENDOR = getSystemProperty("java.vendor");
    public static final String JAVA_VENDOR_URL = getSystemProperty("java.vendor.url");
    public static final String JAVA_VERSION = getSystemProperty("java.version");
    public static final String JAVA_VM_INFO = getSystemProperty("java.vm.info");
    public static final String JAVA_VM_NAME = getSystemProperty("java.vm.name");
    public static final String JAVA_VM_SPECIFICATION_NAME = getSystemProperty("java.vm.specification.name");
    public static final String JAVA_VM_SPECIFICATION_VENDOR = getSystemProperty("java.vm.specification.vendor");
    public static final String JAVA_VM_SPECIFICATION_VERSION = getSystemProperty("java.vm.specification.version");
    public static final String JAVA_VM_VENDOR = getSystemProperty("java.vm.vendor");
    public static final String JAVA_VM_VERSION = getSystemProperty("java.vm.version");
    @Deprecated
    public static final String LINE_SEPARATOR = getSystemProperty("line.separator");
    public static final String OS_ARCH = getSystemProperty("os.arch");
    public static final String OS_NAME = getSystemProperty("os.name");
    public static final String OS_VERSION = getSystemProperty("os.version");
    @Deprecated
    public static final String PATH_SEPARATOR = getSystemProperty("path.separator");
    public static final String USER_COUNTRY;
    public static final String USER_DIR = getSystemProperty(USER_DIR_KEY);
    public static final String USER_HOME = getSystemProperty(USER_HOME_KEY);
    public static final String USER_LANGUAGE = getSystemProperty("user.language");
    public static final String USER_NAME = getSystemProperty(USER_NAME_KEY);
    public static final String USER_TIMEZONE = getSystemProperty("user.timezone");

    static {
        String systemProperty;
        boolean z;
        boolean z2 = false;
        if (getSystemProperty("user.country") == null) {
            systemProperty = getSystemProperty("user.region");
        } else {
            systemProperty = getSystemProperty("user.country");
        }
        USER_COUNTRY = systemProperty;
        if (getOsMatchesName("Linux") || getOsMatchesName("LINUX")) {
            z = true;
        } else {
            z = false;
        }
        IS_OS_LINUX = z;
        if (IS_OS_AIX || IS_OS_HP_UX || IS_OS_IRIX || IS_OS_LINUX || IS_OS_MAC_OSX || IS_OS_SOLARIS || IS_OS_SUN_OS || IS_OS_FREE_BSD || IS_OS_OPEN_BSD || IS_OS_NET_BSD) {
            z2 = true;
        }
        IS_OS_UNIX = z2;
    }

    public static File getJavaHome() {
        return new File(System.getProperty(JAVA_HOME_KEY));
    }

    public static String getHostName() {
        return IS_OS_WINDOWS ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
    }

    public static File getJavaIoTmpDir() {
        return new File(System.getProperty(JAVA_IO_TMPDIR_KEY));
    }

    private static boolean getJavaVersionMatches(String versionPrefix) {
        return isJavaVersionMatch(JAVA_SPECIFICATION_VERSION, versionPrefix);
    }

    private static boolean getOsMatches(String osNamePrefix, String osVersionPrefix) {
        return isOSMatch(OS_NAME, OS_VERSION, osNamePrefix, osVersionPrefix);
    }

    private static boolean getOsMatchesName(String osNamePrefix) {
        return isOSNameMatch(OS_NAME, osNamePrefix);
    }

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            return null;
        }
    }

    public static String getEnvironmentVariable(String name, String defaultValue) {
        try {
            String value = System.getenv(name);
            return value == null ? defaultValue : value;
        } catch (SecurityException e) {
            return defaultValue;
        }
    }

    public static File getUserDir() {
        return new File(System.getProperty(USER_DIR_KEY));
    }

    public static File getUserHome() {
        return new File(System.getProperty(USER_HOME_KEY));
    }

    public static String getUserName() {
        return System.getProperty(USER_NAME_KEY);
    }

    public static String getUserName(String defaultValue) {
        return System.getProperty(USER_NAME_KEY, defaultValue);
    }

    public static boolean isJavaAwtHeadless() {
        return Boolean.TRUE.toString().equals(JAVA_AWT_HEADLESS);
    }

    public static boolean isJavaVersionAtLeast(JavaVersion requiredVersion) {
        return JAVA_SPECIFICATION_VERSION_AS_ENUM.atLeast(requiredVersion);
    }

    public static boolean isJavaVersionAtMost(JavaVersion requiredVersion) {
        return JAVA_SPECIFICATION_VERSION_AS_ENUM.atMost(requiredVersion);
    }

    static boolean isJavaVersionMatch(String version, String versionPrefix) {
        if (version == null) {
            return false;
        }
        return version.startsWith(versionPrefix);
    }

    static boolean isOSMatch(String osName, String osVersion, String osNamePrefix, String osVersionPrefix) {
        if (osName == null || osVersion == null || !isOSNameMatch(osName, osNamePrefix) || !isOSVersionMatch(osVersion, osVersionPrefix)) {
            return false;
        }
        return true;
    }

    static boolean isOSNameMatch(String osName, String osNamePrefix) {
        if (osName == null) {
            return false;
        }
        return osName.startsWith(osNamePrefix);
    }

    static boolean isOSVersionMatch(String osVersion, String osVersionPrefix) {
        if (isEmpty(osVersion)) {
            return false;
        }
        String[] versionPrefixParts = osVersionPrefix.split("\\.");
        String[] versionParts = osVersion.split("\\.");
        for (int i = 0; i < Math.min(versionPrefixParts.length, versionParts.length); i++) {
            if (!versionPrefixParts[i].equals(versionParts[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
