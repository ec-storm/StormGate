package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.services.ProfileService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationView extends VBox implements ApplicationContextAware {

    @Autowired
    ProfileService service;

    private ApplicationContext applicationContext;

    private Object createControllerForType(Class<?> type) {
        return this.applicationContext.getBean(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.applicationContext != null) {
            return;
        }

        this.applicationContext = applicationContext;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "Application.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(this::createControllerForType);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Stage setStage(Stage stage) {
        Scene scene = this.getScene();
        if (scene == null) {
            scene = new Scene(this);
        }

        stage.setScene(scene);
        return stage;
    }
}
