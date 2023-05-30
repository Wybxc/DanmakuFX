package cc.wybxc.frontend;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DanmakuLayout {
    @Data
    @Builder
    static class DanmakuBound {
        private double x;
        private double y;
        private double width;
        private double height;
        private double speed;
    }

    private final double stageWidth;
    private final double stageHeight;

    private final List<DanmakuBound> danmakuBounds = new ArrayList<>();
    private Instant lastUpdate = Instant.now();

    public DanmakuLayout(double stageWidth, double stageHeight) {
        this.stageWidth = stageWidth;
        this.stageHeight = stageHeight;
    }

    private void updateDanmakuBounds() {
        var now = Instant.now();
        // 时间，单位秒
        var delta = Duration.between(lastUpdate, now).toMillis() / 1000.0;
        lastUpdate = now;

        for (var bound : danmakuBounds) {
            // 修正 speed
            var speed = (1 + bound.getWidth() / stageWidth) * bound.getSpeed();
            bound.setX(bound.getX() - speed * delta);
        }

        // 移除超出屏幕的弹幕
        danmakuBounds.removeIf(bound -> bound.getX() + bound.getWidth() < 0);
    }

    private long hitCount(double x, double y) {
        return danmakuBounds.stream()
                .filter(bound -> bound.getX() <= x && x <= bound.getX() + bound.getWidth()
                        && bound.getY() <= y && y <= bound.getY() + bound.getHeight())
                .count();
    }

    public DanmakuBound addDanmaku(double width, double height, double speed) {
        updateDanmakuBounds();

        var minHitCount = Long.MAX_VALUE;
        var minHitCountY = 0.0;
        for (double y = height; y <= stageHeight; y += 10) {
            var hit = hitCount(stageWidth, y);
            if (hit == 0) {
                minHitCountY = y;
                break;
            }
            if (hit < minHitCount) {
                minHitCount = hit;
                minHitCountY = y;
            }
        }

        var bound = DanmakuBound.builder()
                .x(stageWidth)
                .y(minHitCountY)
                .width(width)
                .height(height)
                .speed(speed)
                .build();

        danmakuBounds.add(bound);
        return bound;
    }
}
