package tn.esprit.controllers;

import tn.esprit.services.GeminiService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.application.Platform;

public class ChatbotController {

    @FXML private AnchorPane chatWindow;
    @FXML private VBox chatBody;
    @FXML private TextField userInput;
    @FXML private ScrollPane chatScrollPane;

    @FXML
    public void toggleChatbot() {
        chatWindow.setVisible(!chatWindow.isVisible());
    }

    @FXML
    public void handleKeyPress(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    @FXML
    public void sendMessage() {
        String message = userInput.getText().trim();
        if (message.isEmpty()) return;

        addMessage("Vous", message, "#d1e8ff");
        userInput.clear();

        new Thread(() -> {
            String response = GeminiService.getGeminiResponse(message);
            Platform.runLater(() -> addMessage("Assistant", response, "#e8f5e9"));
        }).start();
    }

    private void addMessage(String sender, String text, String bgColor) {
        VBox messageBox = new VBox();
        messageBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-padding: 10;");
        messageBox.setMaxWidth(250);

        Text senderText = new Text(sender + ":");
        senderText.setStyle("-fx-font-weight: bold;");

        Text messageText = new Text(text);
        messageText.setWrappingWidth(220);

        messageBox.getChildren().addAll(senderText, messageText);
        chatBody.getChildren().add(messageBox);

        // Scroll to bottom
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }



}
