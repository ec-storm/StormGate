package com.minhdtb.storm.common;

import com.minhdtb.storm.base.AbstractView;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.Optional;

public class Utils {

    public static void showError(AbstractView owner, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "");
        alert.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            alert.initOwner(owner.getWindow());
        }

        alert.setHeaderText(message);
        alert.setContentText(null);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
    }

    public static void showConfirm(AbstractView owner, String message, EventHandler<Event> Ok) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            alert.initOwner(owner.getWindow());
        }

        alert.setHeaderText(message);
        alert.setContentText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Ok.handle(null);
        } else {
            alert.close();
        }
    }

    public static EventHandler<KeyEvent> numericValidation(final Integer maxLength) {
        return e -> {
            TextField textField = (TextField) e.getSource();
            if (textField.getText().length() >= maxLength) {
                e.consume();
            }

            if (e.getCharacter().matches("[0-9.]")) {
                if (textField.getText().contains(".") && e.getCharacter().matches("[.]")) {
                    e.consume();
                } else if (textField.getText().length() == 0 && e.getCharacter().matches("[.]")) {
                    e.consume();
                }
            } else {
                e.consume();
            }
        };
    }
}
