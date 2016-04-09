package com.minhdtb.storm;

import com.minhdtb.storm.base.AbstractApplication;
import com.minhdtb.storm.gui.application.ApplicationView;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.Environment;
import reactor.bus.EventBus;

@SpringBootApplication
public class StormGateApplication extends AbstractApplication {

    @Autowired
    private ApplicationView applicationView;

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty()
                .assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

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

    public static void main(String[] args) {
        launchApp(StormGateApplication.class, args);
    }
}
