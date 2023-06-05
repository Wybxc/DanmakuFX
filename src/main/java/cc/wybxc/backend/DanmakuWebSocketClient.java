package cc.wybxc.backend;

import cc.wybxc.common.DanmakuMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class DanmakuWebSocketClient extends Application {

    private WebSocketClient client;
    private final int port;

    public DanmakuWebSocketClient(int port) {
        this.port = port;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danmaku Client");

        TextField inputField = new TextField();
        Button sendButton = new Button("Send");
        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);

        ChoiceBox<Integer> fontSizeChoice = new ChoiceBox<>();
        fontSizeChoice.getItems().addAll(16, 20, 24, 28);
        fontSizeChoice.setValue(20);

        ChoiceBox<String> colorChoice = new ChoiceBox<>();
        colorChoice.getItems().addAll("#FFFFFF", "#FF0000", "#00FF00", "#0000FF");
        colorChoice.setValue("#FFFFFF");

        ChoiceBox<Double> speedChoice = new ChoiceBox<>();
        speedChoice.getItems().addAll(100.0, 200.0, 300.0, 400.0);
        speedChoice.setValue(200.0);

        TextField repeatField = new TextField();
        Button repeatButton = new Button("Repeat");

        HBox inputControls = new HBox(fontSizeChoice, colorChoice, speedChoice, repeatField, repeatButton);
        inputControls.setSpacing(10);

        VBox root = new VBox(messageArea, inputControls, inputField, sendButton);
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            // Create WebSocket client and connect to the server
            client = new WebSocketClient(new URI("ws://localhost:" + port)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Platform.runLater(() -> {
                        messageArea.appendText("Connected to WebSocket server\n");
                    });
                }

                @Override
                public void onMessage(String message) {
                    Platform.runLater(() -> {
                        messageArea.appendText("Received message from server: " + message + "\n");
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Platform.runLater(() -> {
                        messageArea.appendText("Disconnected from WebSocket server\n");
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Platform.runLater(() -> {
                        messageArea.appendText("WebSocket error: " + ex.getMessage() + "\n");
                    });
                }
            };
            client.connect();

            sendButton.setOnAction(e -> {
                String text = inputField.getText();
                int fontSize = fontSizeChoice.getValue();
                String color = colorChoice.getValue();
                double speed = speedChoice.getValue();

                DanmakuMessage danmakuMessage = new DanmakuMessage(text, fontSize, color, speed);
                String jsonMessage = danmakuMessage.toJson();

                client.send(jsonMessage);

                inputField.clear();
            });

            repeatButton.setOnAction(e -> {
                String text = inputField.getText();
                int repeatCount = Integer.parseInt(repeatField.getText());
                int fontSize = fontSizeChoice.getValue();
                String color = colorChoice.getValue();
                double speed = speedChoice.getValue();

                DanmakuMessage danmakuMessage = new DanmakuMessage(text, fontSize, color, speed);
                String jsonMessage = danmakuMessage.toJson();

                for (int i = 0; i < repeatCount; i++) {
                    client.send(jsonMessage);
                }

                inputField.clear();
                repeatField.clear();
            });

            primaryStage.setOnCloseRequest(e -> {
                client.close();
            });
        } catch (URISyntaxException e) {
            System.err.println("Invalid WebSocket server URI: " + e.getMessage());
        }
    }

}
