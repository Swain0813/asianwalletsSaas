package com.asianwallets.common.utils;

import com.google.common.collect.Maps;

import java.awt.*;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SystemUtil {
    /**
     * CUP 个数
     *
     * @return int CUP 个数
     */
    public static final int cupNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 空闲内存量
     *
     * @return long 空闲内存量
     */
    public static final long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * 内存总量
     *
     * @return long 内存总量
     */
    public static final long totalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * 最大内存量
     *
     * @return long 最大内存量
     */
    public static final long maxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    // check if Operating System is Windows
    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os != null && (os.toLowerCase().indexOf("win") >= 0);
    }

    public static void checkTranslucencyMode() {

        SystemUtil
                .checkTranslucencyMode(WindowTranslucency.PERPIXEL_TRANSLUCENT);
        SystemUtil
                .checkTranslucencyMode(WindowTranslucency.PERPIXEL_TRANSPARENT);

    }

    public static void checkTranslucencyMode(WindowTranslucency arg) {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (!gd.isWindowTranslucencySupported(arg)) {
            System.err.println("'" + arg
                    + "' translucency mode isn't supported.");
            System.exit(-1);
        }
    }

    public static Map<Object, Object> print() {
        HashMap<Object, Object> objectObjectHashMap = Maps.newHashMap();
        Map<String, String> envs = System.getenv();
        for (String key : envs.keySet()) {
            objectObjectHashMap.put(key, envs.get(key));
        }
        Properties properties = System.getProperties();

        for (Object key : properties.keySet()) {
            objectObjectHashMap.put(key, properties.get(key));
        }
        objectObjectHashMap.put("securityManager", System.getSecurityManager());
        return objectObjectHashMap;

    }
}
