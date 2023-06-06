package cc.wybxc.backend;

import cc.wybxc.common.ApplicationProperties;
import cc.wybxc.common.DanmakuMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketBackend extends DanmakuBackend {

    private final WebSocketServer server;

    private final static Logger logger = LoggerFactory.getLogger(WebSocketBackend.class);

    public WebSocketBackend(@NonNull BlockingQueue<DanmakuMessage> danmakuQueue) {
        super(danmakuQueue);
        int port = ApplicationProperties.getBackendPort();
        this.server = new Server(new InetSocketAddress(port), danmakuQueue);
    }

    @Override
    public void run() {
        server.run();
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (InterruptedException e) {
            logger.error("Failed to stop WebSocket server", e);
        }
    }


    static class Server extends WebSocketServer {

        private final BlockingQueue<DanmakuMessage> danmakuQueue;


        public Server(InetSocketAddress address, @NonNull BlockingQueue<DanmakuMessage> danmakuQueue) {
            super(address);
            this.danmakuQueue = danmakuQueue;
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            var address = webSocket.getRemoteSocketAddress();
            logger.info("WebSocket connection from {}:{}", address.getAddress(), address.getPort());
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            var address = webSocket.getRemoteSocketAddress();
            logger.info("WebSocket connection from {}:{} closed", address.getAddress(), address.getPort());
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            try {
                var message = DanmakuMessage.fromJson(s);
                var result = danmakuQueue.offer(message);
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse WebSocket message", e);
            }
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            var address = webSocket.getRemoteSocketAddress();
            logger.error("WebSocket connection from {}:{} error", address.getAddress(), address.getPort(), e);
        }

        @Override
        public void onStart() {
            logger.info("WebSocket server started");
        }
    }


}
