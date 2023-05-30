package cc.wybxc.common;

import javafx.scene.paint.Color;
import lombok.NonNull;

public record DanmakuMessage(@NonNull String text, double size, @NonNull String color, double speed) {
    public Color webColor() {
        try {
            return Color.web(color);
        } catch (IllegalArgumentException e) {
            return Color.WHITE;
        }
    }
}
