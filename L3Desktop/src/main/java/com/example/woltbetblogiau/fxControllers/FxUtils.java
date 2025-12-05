package com.example.woltbetblogiau.fxControllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class FxUtils {
    public static void generateAlert(Alert.AlertType alertType, String titleText, String headerText, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void generateDialogAlert(Alert.AlertType alertType, String headerText, Exception e) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(headerText);

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(e.getMessage());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
