package cc.wybxc;

import cc.wybxc.backend.DanmakuBackend;
import cc.wybxc.frontend.DanmakuApplication;
import javafx.application.Application;

public class Main {
    private static void run(DanmakuBackend backend) {
        var danmakuQueue = DanmakuApplication.danmakuQueue;
        var backendThread = new Thread(() -> {
            backend.start(danmakuQueue);
        });
        backendThread.start();

        Application.launch(DanmakuApplication.class);
    }

    public static void main(String[] args) {
        try (var properties = cc.wybxc.Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            var p = new java.util.Properties();
            p.load(properties);
            var port = Integer.parseInt(p.getProperty("backend.port"));

            var backend = new cc.wybxc.backend.WebSocketBackend(port);
            run(backend);
        } catch (Exception e) {
            System.err.println("Failed to load application.properties: " + e);
            System.exit(1);
        }
    }
}
