package com.minhdtb.storm.gui.base;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.ResourceBundle.getBundle;

public abstract class AbstractFxmlView implements ApplicationContextAware {

    protected ObjectProperty<Object> presenterProperty;
    protected FXMLLoader fxmlLoader;
    protected ResourceBundle bundle;

    protected URL resource;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        if (this.applicationContext != null) {
            return;
        }

        this.applicationContext = applicationContext;
    }

    public AbstractFxmlView() {

        this.presenterProperty = new SimpleObjectProperty<>();
        this.resource = getClass().getResource(getFxmlName());
        this.bundle = getResourceBundle(getBundleName());
    }

    private Object createControllerForType(Class<?> type) {
        return this.applicationContext.getBean(type);
    }

    FXMLLoader loadSynchronously(URL resource, ResourceBundle bundle) throws IllegalStateException {

        FXMLLoader loader = new FXMLLoader(resource, bundle);
        loader.setControllerFactory(this::createControllerForType);

        try {
            loader.load();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load " + getConventionalName(), ex);
        }

        return loader;
    }

    void ensureFxmlLoaderInitialized() {

        if (this.fxmlLoader != null) {
            return;
        }

        this.fxmlLoader = loadSynchronously(resource, bundle);
        this.presenterProperty.set(this.fxmlLoader.getController());
    }

    public Parent getView() {

        ensureFxmlLoaderInitialized();

        Parent parent = fxmlLoader.getRoot();
        addCSSIfAvailable(parent);
        return parent;
    }

    public void getView(Consumer<Parent> consumer) {
        CompletableFuture.supplyAsync(this::getView, Platform::runLater).thenAccept(consumer);
    }

    public Node getViewWithoutRootContainer() {

        ObservableList<Node> children = getView().getChildrenUnmodifiable();
        if (children.isEmpty()) {
            return null;
        }

        return children.listIterator().next();
    }

    void addCSSIfAvailable(Parent parent) {

        URL uri = getClass().getResource(getStyleSheetName());
        if (uri == null) {
            return;
        }

        String uriToCss = uri.toExternalForm();
        parent.getStylesheets().add(uriToCss);
    }

    String getStyleSheetName() {
        return getConventionalName(".css");
    }

    public Object getPresenter() {

        ensureFxmlLoaderInitialized();

        return this.presenterProperty.get();
    }

    public void getPresenter(Consumer<Object> presenterConsumer) {

        this.presenterProperty.addListener((ObservableValue<? extends Object> o, Object oldValue, Object newValue) -> {
            presenterConsumer.accept(newValue);
        });
    }

    protected String getConventionalName(String ending) {
        return getConventionalName() + ending;
    }

    protected String getConventionalName() {
        return stripEnding(getClass().getSimpleName().toLowerCase());
    }

    String getBundleName() {
        return getClass().getPackage().getName() + "." + getConventionalName();
    }

    static String stripEnding(String clazz) {

        if (!clazz.endsWith("view")) {
            return clazz;
        }

        return clazz.substring(0, clazz.lastIndexOf("view"));
    }

    final String getFxmlName() {
        return getConventionalName(".fxml");
    }

    private ResourceBundle getResourceBundle(String name) {
        try {
            return getBundle(name);
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }
}
