package cc.wybxc.backend;

import cc.wybxc.common.DanmakuMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketBackend implements DanmakuBackend {

    private final int port;
    private WebSocketServer server = null;

    private final static Logger logger = LoggerFactory.getLogger(WebSocketBackend.class);

    public WebSocketBackend(int port) {
        this.port = port;
    }

    @Override
    public void start(@NonNull BlockingQueue<DanmakuMessage> danmakuQueue) {
        server = new Server(new InetSocketAddress(port), danmakuQueue);
        server.run();
    }

    @Override
    public void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (InterruptedException e) {
                logger.error("Failed to stop WebSocket server", e);
            }
        }
    }

    static class WebSocketDanmakuMessage {
        public String text;
        public double size = 40;
        public String color = "#FFFFFF";
        public double speed = 144;

        public DanmakuMessage toDanmakuMessage() {
            return new DanmakuMessage(text, size, color, speed);
        }
    }

    static class Server extends WebSocketServer {

        private final BlockingQueue<DanmakuMessage> danmakuQueue;

        private final static ObjectMapper mapper = new ObjectMapper();


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
                var message = mapper.readValue(s, WebSocketDanmakuMessage.class).toDanmakuMessage();
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
