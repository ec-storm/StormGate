package com.minhdtb.storm;

import com.minhdtb.storm.gui.application.ApplicationView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@Lazy
@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport {

    @Autowired
    private ApplicationView applicationView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Storm Gateway Service - Copyright © 2016");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("logo.png")));
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        applicationView.setStage(primaryStage).show();
    }

    public static void main(String[] args) {
        launchApp(Main.class, args);
    }
}
