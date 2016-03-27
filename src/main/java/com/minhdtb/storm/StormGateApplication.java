package com.minhdtb.storm;

import com.minhdtb.storm.base.AbstractApplication;
import com.minhdtb.storm.gui.application.ApplicationView;
import com.minhdtb.storm.gui.newprofile.DialogNewProfileView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@Lazy
@SpringBootApplication
public class StormGateApplication extends AbstractApplication {

    @Autowired
    private ApplicationView applicationView;

    @Autowired
    private DialogNewProfileView dialogNewProfileView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Storm Gateway Service - Copyright © 2016");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("logo.png")));
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        applicationView
                .setApplication(this)
                .setStage(primaryStage)
                .show();
    }

    public void showDialogNewProfile() {
        dialogNewProfileView
                .setTitle("New Profile")
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(applicationView.getWindow())
                .setApplication(this)
                .show();
    }

    public void showError(Window owner, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setHeaderText(null);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
    }

    public static void main(String[] args) {
        launchApp(StormGateApplication.class, args);
    }
}
