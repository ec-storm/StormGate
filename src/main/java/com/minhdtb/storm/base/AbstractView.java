package com.minhdtb.storm.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;

public class AbstractView implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Scene scene;

    private Stage stage;

    private String fxmlName;

    private FXMLLoader fxmlLoader;

    private Object createControllerForType(Class<?> type) {
        return this.applicationContext.getBean(type);
    }

    protected void setFxml(String fxml) {
        this.fxmlName = fxml;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.applicationContext != null) {
            return;
        }

        this.applicationContext = applicationContext;
    }

    public AbstractView setStage(Stage stage) {
        this.stage = stage;
        AbstractController controller = this.fxmlLoader.getController();
        controller.setStage(stage);
        return this;
    }

    public AbstractView setApplication(AbstractApplication application) {
        this.fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/" + this.fxmlName));
        this.fxmlLoader.setControllerFactory(this::createControllerForType);
        try {
            Parent parent = this.fxmlLoader.load();
            AbstractController controller = this.fxmlLoader.getController();
            controller.setApplication(application);
            this.scene = new Scene(parent);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public void show() {
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}
