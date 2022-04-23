package core;

import core.shell.ShellEntity;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyT {
    private static final String[] PTOXY_TYPES = {"NO_PROXY", "HTTP", "SOCKS"};

    private ProxyT() {
    }

    public static Proxy getProxy(ShellEntity context) {
        try {
            String type = context.getProxyType();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(context.getProxyHost(), context.getProxyPort());
            if ("SOCKS".equals(type.toUpperCase())) {
                return new Proxy(Proxy.Type.SOCKS, inetSocketAddress);
            }
            if ("HTTP".equals(context.getProxyType())) {
                return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
            }
            return Proxy.NO_PROXY;
        } catch (Exception e) {
            return Proxy.NO_PROXY;
        }
    }

    public static String[] getAllProxyType() {
        return PTOXY_TYPES;
    }
}
