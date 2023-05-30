package cc.wybxc.backend;

import cc.wybxc.common.DanmakuMessage;

import java.util.concurrent.BlockingQueue;

/**
 * 弹幕后端接口。
 * <p>
 * 弹幕后端负责从第三方接收弹幕消息，然后将其放入 {@link DanmakuMessage} 队列中。
 * */
public interface DanmakuBackend {
    void start(BlockingQueue<DanmakuMessage> danmakuQueue);

    void stop();
}
