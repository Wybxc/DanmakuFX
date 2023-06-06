package cc.wybxc.backend;

import cc.wybxc.common.ApplicationProperties;
import cc.wybxc.common.DanmakuMessage;
import lombok.NonNull;

import java.util.concurrent.BlockingQueue;

/**
 * 弹幕后端接口。
 * <p>
 * 弹幕后端负责从第三方接收弹幕消息，然后将其放入 {@link DanmakuMessage} 队列中。
 */
public abstract class DanmakuBackend implements Runnable {
    protected final BlockingQueue<DanmakuMessage> danmakuQueue;

    public DanmakuBackend(@NonNull BlockingQueue<DanmakuMessage> danmakuQueue) {
        this.danmakuQueue = danmakuQueue;
    }

    public abstract void stop();

    public static void startBackend(@NonNull BlockingQueue<DanmakuMessage> danmakuQueue) {
        try {
            var backendClass = ApplicationProperties.getBackendClass();
            var backend = backendClass.getConstructor(BlockingQueue.class).newInstance(danmakuQueue);
            var thread = new Thread(backend);
            thread.start();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load backend class: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Failed to start backend: " + e);
            System.exit(1);
        }
    }
}
