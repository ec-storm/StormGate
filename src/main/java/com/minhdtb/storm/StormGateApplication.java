package com.minhdtb.storm;

import com.minhdtb.storm.gui.application.ApplicationView;
import com.minhdtb.storm.gui.newprofile.DialogNewProfileView;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@Lazy
@SpringBootApplication
public class StormGateApplication extends AbstractJavaFxApplicationSupport {

    @Autowired
    private ApplicationView applicationView;

    @Autowired
    private DialogNewProfileView dialogNewProfileView;

    private Stage primaryStage;

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

        this.primaryStage = primaryStage;
    }

    public void newProfile() {
        Stage stage = new Stage();
        stage.setTitle("New Profile");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.primaryStage.getScene().getWindow());

        dialogNewProfileView
                .setApplication(this)
                .setStage(stage)
                .show();
    }

    public static void main(String[] args) {
        launchApp(StormGateApplication.class, args);
    }
}
