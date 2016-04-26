package com.minhdtb.storm.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;

public class AbstractView implements ApplicationContextAware {

    protected AbstractApplication application;

    private ApplicationContext applicationContext;

    private Scene scene;

    private Stage stage;

    private FXMLLoader fxmlLoader;

    private String title;

    private Modality modality;

    private AbstractView owner;

    private AbstractController controller;

    private Image image;

    private Object createControllerForType(Class<?> type) {
        return this.applicationContext.getBean(type);
    }

    protected void setFxml(String fxml) {
        this.fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
        this.fxmlLoader.setControllerFactory(this::createControllerForType);
        try {
            this.scene = new Scene(this.fxmlLoader.load());
            this.scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            this.controller = this.fxmlLoader.getController();
            this.controller.setView(this);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.applicationContext != null) {
            return;
        }

        this.applicationContext = applicationContext;
    }

    public AbstractView setApplication(AbstractApplication application) {
        this.application = application;
        return this;
    }

    public AbstractApplication getApplication() {
        return this.application;
    }

    public AbstractView setStage(Stage stage) {
        this.stage = stage;
        return this;
    }

    public Stage getStage() {
        return this.stage;
    }

    public Scene getScene() {
        return this.scene;
    }

    public AbstractController getController() {
        return this.controller;
    }

    public Window getWindow() {
        return this.getStage().getScene().getWindow();
    }

    public AbstractView setTitle(String title) {
        this.title = title;
        return this;
    }

    public AbstractView setModality(Modality modality) {
        this.modality = modality;
        return this;
    }

    public AbstractView setOwner(AbstractView owner) {
        this.owner = owner;
        return this;
    }

    public AbstractView getOwner() {
        return this.owner;
    }

    public AbstractView setIcon(Image image) {
        this.image = image;
        return this;
    }

    public Image getIcon() {
        return this.image;
    }

    public void show() {
        if (this.stage == null) {
            this.stage = new Stage();
            this.stage.initModality(this.modality);
            this.stage.setResizable(false);
            this.stage.initOwner(this.owner.getWindow());
        }

        this.stage.setTitle(this.title);

        if (this.image != null) {
            this.stage.getIcons().clear();
            this.stage.getIcons().add(this.image);
        } else {
            Image image = getOwner().getIcon();
            if (image != null) {
                this.stage.getIcons();
                this.stage.getIcons().add(image);
            }
        }

        AbstractController controller = this.fxmlLoader.getController();
        this.stage.setOnShowing(controller::onShow);
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}
