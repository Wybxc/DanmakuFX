package cc.wybxc.common;

import cc.wybxc.backend.DanmakuBackend;

public class ApplicationProperties {
    public static void init() {
        try (var properties = cc.wybxc.Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            var p = new java.util.Properties();
            p.load(properties);

            System.getProperties().putAll(p);
        } catch (Exception e) {
            System.err.println("Failed to load application.properties: " + e);
            System.exit(1);
        }
    }

    public static void load() {
        backendClass = System.getProperty("backend.class");
        backendPort = Integer.parseInt(System.getProperty("backend.port"));
    }

    private static String backendClass;

    public static Class<? extends DanmakuBackend> getBackendClass() throws ClassNotFoundException {
        var klass = Class.forName(backendClass);
        if (!DanmakuBackend.class.isAssignableFrom(klass)) {
            throw new ClassNotFoundException("Class " + backendClass + " is not a subclass of DanmakuBackend");
        }
        return klass.asSubclass(DanmakuBackend.class);
    }

    private static int backendPort;

    public static int getBackendPort() {
        return backendPort;
    }


}
