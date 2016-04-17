package com.minhdtb.storm.common;

import com.minhdtb.storm.base.AbstractView;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
        if (result.get() == ButtonType.OK) {
            Ok.handle(null);
        } else {
            alert.close();
        }
    }
}
