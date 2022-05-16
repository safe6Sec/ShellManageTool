package util;

import core.ApplicationContext;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
   public static void log(String data, Object... values) {
      echo("*", String.format(data, values));
   }

   public static void error(Throwable exception) {
      String stackTrace = "";
      StackTraceElement[] elements = exception.getStackTrace();

      for(int i = 0; i < elements.length; ++i) {
         stackTrace = stackTrace + elements[i] + "->";
      }

      if (stackTrace.length() > 2) {
         stackTrace = stackTrace.substring(0, stackTrace.length() - 2);
      }

      echo("!", String.format("%s stackTrace: %s", exception.getMessage(), stackTrace));
   }

   public static void error(String data) {
      echo("!", data);
   }

   private static void echo(String identification, String message) {
      String data = null;
      if (ApplicationContext.isOpenC("isSuperLog")) {
         data = String.format("[%s] Time:%s LastStackTrace:%s ThreadId:%s Message: %s", identification, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), getLastStackTrace(), Thread.currentThread().getId(), message);
      } else {
         data = String.format("[%s] Time:%s ThreadId:%s Message: %s", identification, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), Thread.currentThread().getId(), message);
      }

      System.out.println(data);
   }

   private static String getLastStackTrace() {
      String className = Thread.currentThread().getStackTrace()[4].getClassName();
      String methodName = Thread.currentThread().getStackTrace()[4].getMethodName();
      int line = Thread.currentThread().getStackTrace()[4].getLineNumber();
      return String.format("%s->%s<->%s", className, methodName, line);
   }
}
