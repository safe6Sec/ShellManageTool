package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static void log(String data, Object... values) {
        System.out.println(String.format(String.format("[*] Time:%s ThreadId:%s Message: %s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Long.valueOf(Thread.currentThread().getId()), data), values));
    }

    public static void error(Exception exception) {
        String stackTrace = new String();
        StackTraceElement[] elements = exception.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            stackTrace = stackTrace + elements[i] + "->";
        }
        System.out.println(String.format("[!] Time:%s ThreadId:%s Message:%s stackTrace: %s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Long.valueOf(Thread.currentThread().getId()), exception.getMessage(), stackTrace.substring(0, stackTrace.length() - 2)));
    }

    public static void error(String data) {
        System.out.println(String.format("[!] Time:%s ThreadId:%s Message: %s ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Long.valueOf(Thread.currentThread().getId()), data));
    }
}
