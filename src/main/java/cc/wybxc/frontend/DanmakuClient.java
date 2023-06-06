package cc.wybxc.frontend;

import cc.wybxc.common.ApplicationProperties;
import cc.wybxc.common.DanmakuMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class DanmakuClient extends Application {
    private WebSocketClient client;

    private TextField inputField;
    private ChoiceBox<Integer> fontSizeChoice;
    private ChoiceBox<String> colorChoice;
    private ChoiceBox<Double> speedChoice;
    private TextField repeatField;
    private TextArea messageArea;


    public static void launch() {
        Application.launch(DanmakuClient.class);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Danmaku Client");

        inputField = new TextField();
        Button sendButton = new Button("Send");
        messageArea = new TextArea();
        messageArea.setEditable(false);

        fontSizeChoice = new ChoiceBox<>();
        fontSizeChoice.getItems().addAll(16, 20, 24, 28);
        fontSizeChoice.setValue(20);

        colorChoice = new ChoiceBox<>();
        colorChoice.getItems().addAll("#FFFFFF", "#FF0000", "#00FF00", "#0000FF");
        colorChoice.setValue("#FFFFFF");

        speedChoice = new ChoiceBox<>();
        speedChoice.getItems().addAll(100.0, 200.0, 300.0, 400.0);
        speedChoice.setValue(200.0);

        repeatField = new TextField();
        Button repeatButton = new Button("Repeat");

        HBox inputControls = new HBox(fontSizeChoice, colorChoice, speedChoice, repeatField, repeatButton);
        inputControls.setSpacing(10);

        VBox root = new VBox(messageArea, inputControls, inputField, sendButton);
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();

        var port = ApplicationProperties.getBackendPort();

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
                String jsonMessage;
                try {
                    jsonMessage = getJsonDanmakuMessage();
                } catch (JsonProcessingException ex) {
                    messageArea.appendText("Failed to serialize message: " + ex.getMessage() + "\n");
                    return;
                }

                client.send(jsonMessage);

                inputField.clear();
            });

            repeatButton.setOnAction(e -> {
                int repeatCount = Integer.parseInt(repeatField.getText());
                String jsonMessage;
                try {
                    jsonMessage = getJsonDanmakuMessage();
                } catch (JsonProcessingException ex) {
                    messageArea.appendText("Failed to serialize message: " + ex.getMessage() + "\n");
                    return;
                }

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

    private String getJsonDanmakuMessage() throws JsonProcessingException {
        String text = inputField.getText();
        int fontSize = fontSizeChoice.getValue();
        String color = colorChoice.getValue();
        double speed = speedChoice.getValue();

        DanmakuMessage danmakuMessage = new DanmakuMessage(text, fontSize, color, speed);
        return danmakuMessage.toJson();
    }

}
