package cc.wybxc.frontend;

import cc.wybxc.common.DanmakuMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.NonNull;

public class DanmakuDisplay extends Pane {
    private final double stageWidth;
    private final double stageHeight;

    private final DanmakuLayout layout;


    public DanmakuDisplay(double stageWidth, double stageHeight) {
        this.stageWidth = stageWidth;
        this.stageHeight = stageHeight;
        this.layout = new DanmakuLayout(stageWidth, stageHeight);
    }

    public void emitDanmaku(@NonNull DanmakuMessage danmaku) {
        var text = new Text(danmaku.text());
        text.setFont(Font.font(danmaku.size()));
        text.setFill(danmaku.webColor());

        // 滚动弹幕
        var layoutBounds = text.getLayoutBounds();
        var bounds = layout.addDanmaku(layoutBounds.getWidth(), layoutBounds.getHeight(), danmaku.speed());
        text.setX(bounds.getX());
        text.setY(bounds.getY());

        getChildren().add(text);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(stageWidth / danmaku.speed()),
                new KeyValue(text.xProperty(), -layoutBounds.getWidth()))
        );
        timeline.onFinishedProperty().set(event -> getChildren().remove(text));
        timeline.play();
    }
}
