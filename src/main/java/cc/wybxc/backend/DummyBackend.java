package cc.wybxc.backend;

import cc.wybxc.common.DanmakuMessage;
import lombok.NonNull;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;

/**
 * 测试用弹幕后端。
 * <p>
 * 每秒向 {@link DanmakuMessage} 队列中放入一条弹幕消息。
 * */
public class DummyBackend implements DanmakuBackend {
    private final Timer timer = new Timer();

    @Override
    public void start(@NonNull BlockingQueue<DanmakuMessage> danmakuQueue) {
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                var now = System.currentTimeMillis();
                var message = new DanmakuMessage(
                        "测试弹幕 " + now,
                        40,
                        "#FFFFFF",
                        144
                );
                var result = danmakuQueue.offer(message);
            }
        }, 0, 100);
    }

    @Override
    public void stop() {
        timer.cancel();
    }
}
