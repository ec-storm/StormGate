package com.minhdtb.storm.base;

import com.google.common.base.Strings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractView implements ApplicationContextAware {

    @Value("${application.language:}")
    private String language;

    private AbstractApplication application;

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
        Locale locale = new Locale("en", "US");
        if (!Strings.isNullOrEmpty(language)) {
            String[] languages = language.split(",");
            if (languages.length == 2) {
                locale = new Locale(languages[0], languages[1]);
            }
        }

        this.fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
        this.fxmlLoader.setResources(new StormResourceBundle("i18n.language", this.getClass().getSimpleName(), locale));
        this.fxmlLoader.setControllerFactory(this::createControllerForType);
        try {
            this.scene = new Scene(this.fxmlLoader.load());
            this.scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            this.controller = this.fxmlLoader.getController();
            this.controller.setView(this);
        } catch (IOException e) {
            throw new RuntimeException("setFxml failed - " + e.getMessage());
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

    public void show(boolean resizeable) {
        if (this.getStage() == null) {
            this.setStage(new Stage());
            this.getStage().initModality(modality);
            this.getStage().setResizable(resizeable);
            this.getStage().initOwner(this.getOwner().getWindow());
        }

        this.getStage().setTitle(this.title);

        if (this.getIcon() != null) {
            this.getStage().getIcons().clear();
            this.getStage().getIcons().add(this.getIcon());
        } else {
            Image image = getOwner().getIcon();
            if (image != null) {
                this.getStage().getIcons().clear();
                this.getStage().getIcons().add(image);
            }
        }

        AbstractController controller = fxmlLoader.getController();
        this.getStage().setOnShowing(controller::onShow);
        this.getStage().setScene(this.getScene());
        this.getStage().show();
    }

    public Node getNodeById(String id) {
        return getScene().lookup("#" + id);
    }

    private class StormResourceBundle extends ResourceBundle {

        private ResourceBundle resourceBundle;
        private String category;

        StormResourceBundle(String baseName, String category, Locale locale) {
            Assert.notNull(baseName, "baseName must not be null.");
            Assert.notNull(category, "category must not be null.");

            resourceBundle = getBundle(baseName, locale);
            this.category = category;
        }

        @Override
        protected Object handleGetObject(String key) {
            Object value = null;
            try {
                value = resourceBundle.getObject(category + "." + key);
            } catch (Exception ignored) {
            }

            if (value != null) {
                return value;
            } else {
                return "";
            }
        }

        @Override
        public Enumeration<String> getKeys() {
            return resourceBundle.getKeys();
        }

        @Override
        public boolean containsKey(String key) {
            return true;
        }
    }
}
