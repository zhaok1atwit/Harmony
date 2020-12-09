import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import struct.ClientReadThread;
import struct.ColorHBox;
import struct.Message;
import struct.Utils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The chat client
 * @author Matt Lefebvre
 */
public final class ChatClient extends Application {
    private static final String DEFAULT_COLOR = "BLACK";
    private static final String DEFAULT_FONT = "Cambria";
    private Timeline timeline;
    private ClientReadThread readThread;
    private Socket socket;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;

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
                outToServer = new ObjectOutputStream(socket.getOutputStream());
                inFromServer = new ObjectInputStream(socket.getInputStream());

                // GUI
                final VBox chatRoom = new VBox();
                final HBox hBox = new HBox();
                hBox.setAlignment(Pos.BOTTOM_CENTER);

                final VBox messageHolder = new VBox();
                final ScrollPane scrollPane = new ScrollPane(messageHolder);

                final TextField chatInput = new TextField();
                chatInput.setMinWidth(300);
                chatInput.setMinHeight(30);
                chatInput.setPromptText("Send a message to the chat room");
                chatInput.prefWidthProperty().bind(chatRoom.prefWidthProperty());

                // Read thread (process the messages asynchronously and updates the UI synchronously)
                readThread = new ClientReadThread(socket, inFromServer);
                timeline = new Timeline();
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), action -> {
                    while (!readThread.getMessages().isEmpty()) {
                        final Message message = readThread.getMessages().remove(0);
                        final LabelBuilder labelBuilder = LabelBuilder.create().text(message.getContent());

                        final String color = message.getColor();
                        if (color != null && !color.isEmpty()) {
                            labelBuilder.textFill(Color.valueOf(color));
                        }

                        final String font = message.getFont();
                        if (font != null && !font.isEmpty()) {
                            labelBuilder.style(String.format("-fx-font-size: 18; -fx-font-family: %s", message.getFont()));
                        }

                        messageHolder.getChildren().add(labelBuilder.build());
                    }
                }));
                readThread.start();
                timeline.play();
                outToServer.writeObject(new Message("", "", name));

                final List<String> colors = Files.lines(new File("colors.txt").toPath()).collect(Collectors.toList());
                final ListView<ColorHBox> colorsListView = new ListView<>(FXCollections.observableArrayList(colors.stream().map(color -> {
                    final ColorHBox container = new ColorHBox(color);
                    container.setSpacing(5);

                    final Rectangle rectangle = new Rectangle();
                    rectangle.setWidth(40);
                    rectangle.setHeight(40);
                    rectangle.setFill(Color.valueOf(color));

                    final Label colorLabel = new Label(Utils.fullyCapitalize(color));
                    colorLabel.setStyle("-fx-font-size: 24");

                    container.getChildren().addAll(rectangle, colorLabel);

                    return container;
                }).collect(Collectors.toList())));

                final List<String> fonts = Files.lines(new File("fonts.txt").toPath()).collect(Collectors.toList());
                final ListView<Label> fontsListView = new ListView<>(FXCollections.observableArrayList(fonts.stream().map(font -> {
                    final Label fontLabel = new Label(font);
                    fontLabel.setStyle(String.format("-fx-font-family: %s; -fx-font-size: 24", font));
                    return fontLabel;
                }).collect(Collectors.toList())));

                final Button sendMessage = new Button("Send");
                sendMessage.setOnAction(sendMessageEvent -> {
                    final String message = chatInput.getCharacters().toString();
                    if (message.isEmpty()) {
                        return;
                    }
                    try {
                        final ColorHBox selectedColor = colorsListView.getSelectionModel().getSelectedItem();
                        final Label selectedFont = fontsListView.getSelectionModel().getSelectedItem();
                        outToServer.writeObject(new Message(
                                selectedColor == null ? DEFAULT_COLOR : selectedColor.getColor(),
                                selectedFont == null ? DEFAULT_FONT : selectedFont.getText(),
                                message
                        ));
                        chatInput.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                VBox.setVgrow(scrollPane, Priority.ALWAYS);
                HBox.setHgrow(chatInput, Priority.ALWAYS);

                hBox.getChildren().addAll(chatInput, sendMessage);
                chatRoom.getChildren().addAll(scrollPane, hBox);

                final TabPane tabPane = new TabPane();
                tabPane.getTabs().add(new Tab("Chat", chatRoom));
                tabPane.getTabs().add(new Tab("Color", colorsListView));
                tabPane.getTabs().add(new Tab("Font", fontsListView));
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

                stage.setScene(new Scene(tabPane));
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
            outToServer.writeObject(new Message("", "", "/quit"));
            timeline.stop();
            socket.close();
            inFromServer.close();
            outToServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
