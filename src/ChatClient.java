import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public final class ChatClient extends Application {
    private Timeline timeline;
    private ClientReadThread readThread;
    private Socket socket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;

    private void addMessage(VBox messages, Label message) {
        messages.getChildren().add(message);
    }

    @Override
    public void start(Stage stage) {
        final Label label = new Label("Harmony");
        label.setStyle("-fx-font-size: 48; -fx-font-family: Cambria");

        final TextField nameInput = new TextField();
        nameInput.setMinSize(50, 30);
        nameInput.setMaxSize(200, 30);
        nameInput.setPromptText("Enter your name...");

        final Button enterName = new Button("Join Chatroom");
        enterName.setOnAction(event -> {
            final String name = nameInput.getCharacters().toString().trim();
            if (name.isEmpty()) {
                return;
            }

            try {
                // NETWORK
                socket = new Socket("localhost", 1234);
                outToServer = new DataOutputStream(socket.getOutputStream());
                inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // GUI
                final VBox chatRoom = new VBox();
                final HBox hBox = new HBox();
                hBox.setAlignment(Pos.BOTTOM_CENTER);

                final VBox messageHolder = new VBox();
                final ScrollPane scrollPane = new ScrollPane(messageHolder);

                final TextField chatInput = new TextField();
                chatInput.setMinWidth(300);
                chatInput.setPromptText("Send a message to the chat room");
                chatInput.prefWidthProperty().bind(chatRoom.prefWidthProperty());

                // Read thread (process the messages asynchronously and updates the UI synchronously)
                readThread = new ClientReadThread(socket, inFromServer);
                timeline = new Timeline();
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), action -> {
                    while (!readThread.getMessages().isEmpty()) {
                        messageHolder.getChildren().add(new Label(readThread.getMessages().remove(0)));
                    }
                }));
                readThread.start();
                timeline.play();
                outToServer.writeBytes(name + "\r\n");

                final Button sendMessage = new Button("Send");
                sendMessage.setOnAction(sendMessageEvent -> {
                    final String message = chatInput.getCharacters().toString();
                    if (message.isEmpty()) {
                        return;
                    }
                    try {
                        outToServer.writeBytes(message + "\r\n");
                        chatInput.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                HBox.setHgrow(chatInput, Priority.ALWAYS);

                hBox.getChildren().addAll(chatInput, sendMessage);
                chatRoom.getChildren().addAll(scrollPane, hBox);

                stage.setScene(new Scene(chatRoom));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        vbox.getChildren().addAll(label, nameInput, enterName);

        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setTitle("Harmony");
        stage.setScene(new Scene(vbox));
        stage.show();
    }

    @Override
    public void stop() {
        try {
            outToServer.writeBytes(ClientWriteThread.QUIT_COMMAND + "\r\n");
            timeline.stop();
            socket.close();
            inFromServer.close();
            outToServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
