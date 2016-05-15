package com.minhdtb.storm;

import com.minhdtb.storm.base.AbstractApplication;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.gui.application.ApplicationView;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.Environment;
import reactor.bus.EventBus;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        applicationView
                .setApplication(this)
                .setTitle("Storm Gateway Service - Copyright © 2016")
                .setIcon(new Image(getClass().getClassLoader().getResourceAsStream("logo.png")))
                .setStage(primaryStage)
                .show();
    }

    public static void main(String[] args) {
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        }) {
            @Override
            public void write(byte buf[], int off, int len) {
                Utils.log(new String(buf));
            }
        });

        launchApp(StormGateApplication.class, args);
    }
}
