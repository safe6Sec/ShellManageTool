package core;

import core.shell.ShellEntity;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

public class ProxyT {
   private static final String[] PTOXY_TYPES = new String[]{"NO_PROXY", "HTTP", "SOCKS", "GLOBAL_PROXY"};

   private ProxyT() {
   }

   public static Proxy getProxy(ShellEntity context) {
      try {
         String type = context.getProxyType();
         InetSocketAddress inetSocketAddress = new InetSocketAddress(context.getProxyHost(), context.getProxyPort());
         if ("SOCKS".equalsIgnoreCase(type)) {
            return new Proxy(Type.SOCKS, inetSocketAddress);
         } else if ("HTTP".equalsIgnoreCase(type)) {
            return new Proxy(Type.HTTP, inetSocketAddress);
         } else if ("GLOBAL_PROXY".equalsIgnoreCase(type)) {
            inetSocketAddress = new InetSocketAddress(Db.tryGetSetingValue("globalProxyHost", "127.0.0.1"), Integer.parseInt(Db.tryGetSetingValue("globalProxyPort", "8888")));
            type = Db.tryGetSetingValue("globalProxyType", "NO_PROXY");
            if ("SOCKS".equalsIgnoreCase(type)) {
               return new Proxy(Type.SOCKS, inetSocketAddress);
            } else if (!"HTTP".equalsIgnoreCase(type)) {
               return Proxy.NO_PROXY;
            } else {
               return new Proxy(Type.HTTP, inetSocketAddress);
            }
         } else {
            return Proxy.NO_PROXY;
         }
      } catch (Exception var3) {
         return Proxy.NO_PROXY;
      }
   }

   public static String[] getAllProxyType() {
      return PTOXY_TYPES;
   }
}
