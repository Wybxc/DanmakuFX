package cc.wybxc;

import cc.wybxc.backend.DanmakuBackend;
import cc.wybxc.frontend.DanmakuApplication;
import javafx.application.Application;

public class Main {
    private static void run(DanmakuBackend backend) {
        var danmakuQueue = DanmakuApplication.danmakuQueue;
        backend.start(danmakuQueue);
        Application.launch(DanmakuApplication.class);
    }

    public static void main(String[] args) {
        run(new cc.wybxc.backend.DummyBackend());
    }
}
