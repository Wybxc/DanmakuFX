package cc.wybxc.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NonNull;

public record DanmakuMessage(@NonNull String text, double size, @NonNull String color, double speed) {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Data
    static class JsonDanmakuMessage {
        public String text;
        public double size = 40;
        public String color = "#FFFFFF";
        public double speed = 144;

        public static JsonDanmakuMessage fromDanmakuMessage(DanmakuMessage danmakuMessage) {
            var jsonDanmakuMessage = new JsonDanmakuMessage();
            jsonDanmakuMessage.text = danmakuMessage.text();
            jsonDanmakuMessage.size = danmakuMessage.size();
            jsonDanmakuMessage.color = danmakuMessage.color();
            jsonDanmakuMessage.speed = danmakuMessage.speed();
            return jsonDanmakuMessage;
        }

        public DanmakuMessage toDanmakuMessage() {
            return new DanmakuMessage(text, size, color, speed);
        }
    }

    public Color webColor() {
        try {
            return Color.web(color);
        } catch (IllegalArgumentException e) {
            return Color.WHITE;
        }
    }

    public static DanmakuMessage fromJson(String json) throws JsonProcessingException {
        return mapper.readValue(json, JsonDanmakuMessage.class).toDanmakuMessage();
    }

    public String toJson() throws JsonProcessingException {
        var jsonDanmakuMessage = JsonDanmakuMessage.fromDanmakuMessage(this);
        return mapper.writeValueAsString(jsonDanmakuMessage);
    }
}
