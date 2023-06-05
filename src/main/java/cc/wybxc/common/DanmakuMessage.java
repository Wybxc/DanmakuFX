package cc.wybxc.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // 处理异常
            return null;
        }
    }
}
