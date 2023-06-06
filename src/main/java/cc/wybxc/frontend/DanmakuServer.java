package cc.wybxc.frontend;

import cc.wybxc.backend.DanmakuBackend;
import cc.wybxc.common.DanmakuMessage;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.NonNull;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DanmakuServer extends Application {
    private final @NonNull DanmakuDisplay danmakuDisplay;
    private final @NonNull Stage stage;

    public static BlockingQueue<DanmakuMessage> danmakuQueue = new LinkedBlockingQueue<>();

    public DanmakuServer() {
        var bounds = Screen.getPrimary().getVisualBounds();
        this.danmakuDisplay = new DanmakuDisplay(bounds.getWidth(), bounds.getHeight());
        this.stage = new Stage();
    }

    public static void launch() {
        DanmakuBackend.startBackend(danmakuQueue);
        Application.launch(DanmakuServer.class);
    }

    @Override
    public void start(@NonNull Stage primaryStage) {
        // 隐藏任务栏图标
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setOpacity(0);
        primaryStage.setWidth(0);
        primaryStage.setHeight(0);
        primaryStage.show();

        // 全屏置顶显示
        stage.setTitle("DanmakuFX");
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setMaximized(true);

        var scene = new Scene(danmakuDisplay);
        scene.setFill(null);
        stage.setScene(scene);

        var bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        var trayIcon = new FXTrayIcon.Builder(primaryStage).menuItem("Exit", event -> {
            Platform.exit();
            System.exit(0);
        }).build();
        trayIcon.show();

        stage.setOnCloseRequest((event) -> {
            trayIcon.hide();
            Platform.exit();
            System.exit(0);
        });

        // 定时轮询弹幕
        new Timer().scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                pollDanmaku();
            }
        }, 0, 100); // 100ms

        stage.show();
    }

    private void pollDanmaku() {
        try {
            var danmaku = danmakuQueue.poll(100, TimeUnit.MICROSECONDS);
            if (danmaku != null) {
                Platform.runLater(() -> emitDanmaku(danmaku));
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void emitDanmaku(@NonNull DanmakuMessage danmaku) {
        danmakuDisplay.emitDanmaku(danmaku);
    }
}
