package com.takeout.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@WebListener
public class StartupInfoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String contextPath = sce.getServletContext().getContextPath();
        System.out.println("\n========================================");
        System.out.println("  饱了么外卖后端启动成功！");
        System.out.println("  项目路径: " + (contextPath.isEmpty() ? "/" : contextPath));
        System.out.println("----------------------------------------");
        System.out.println("  局域网地址（手机扫码或输入此地址）:");
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) continue;

                String name = ni.getName().toLowerCase();
                String displayName = ni.getDisplayName() == null ? "" : ni.getDisplayName().toLowerCase();
                if (displayName.contains("vmware") || displayName.contains("virtualbox")
                        || displayName.contains("hyper-v") || displayName.contains("vethernet")
                        || displayName.contains("loopback") || displayName.contains("pseudo")
                        || displayName.contains("bluetooth") || displayName.contains("vmnet")
                        || name.startsWith("vmnet") || name.startsWith("vbox")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        String host = addr.getHostAddress();
                        System.out.println("    http://" + host + ":8081" + contextPath
                                + "    [" + ni.getDisplayName() + "]");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("    (无法获取网络地址: " + e.getMessage() + ")");
        }
        System.out.println("========================================\n");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
