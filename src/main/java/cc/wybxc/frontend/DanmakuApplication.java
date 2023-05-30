package cc.wybxc.frontend;

import cc.wybxc.common.DanmakuMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.NonNull;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DanmakuApplication extends Application {
    private final @NonNull Pane pane;
    private final @NonNull Stage stage;

    public static BlockingQueue<DanmakuMessage> danmakuQueue = new LinkedBlockingQueue<>();

    public DanmakuApplication() {
        this.pane = new Pane();
        this.stage = new Stage();
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

        var scene = new Scene(pane);
        scene.setFill(null);
        stage.setScene(scene);

        var bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        // 阻止窗口被关闭
        stage.setOnCloseRequest(Event::consume);

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
        var text = new Text(danmaku.text());
        text.setFont(Font.font(danmaku.size()));
        text.setFill(danmaku.webColor());

        // 滚动弹幕
        var bounds = text.getLayoutBounds();
        text.setX(stage.getWidth());
        text.setY(bounds.getHeight());

        pane.getChildren().add(text);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(stage.getWidth() / danmaku.speed()),
                new KeyValue(text.xProperty(), -bounds.getWidth()))
        );
        timeline.onFinishedProperty().set(event -> pane.getChildren().remove(text));
        timeline.play();
    }
}
