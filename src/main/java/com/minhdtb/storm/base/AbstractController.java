package com.minhdtb.storm.base;

import com.minhdtb.storm.services.Publisher;
import com.minhdtb.storm.services.Subscriber;
import javafx.fxml.Initializable;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public abstract class AbstractController implements Initializable {

    private Publisher<Object> publisher;

    private Subscriber<Object> subscriber;

    private AbstractView view;

    private ResourceBundle resources;

    protected Publisher<Object> getPublisher() {
        return publisher;
    }

    protected Subscriber<Object> getSubscriber() {
        return subscriber;
    }

    void setView(AbstractView view) {
        this.view = view;
    }

    public AbstractView getView() {
        return this.view;
    }

    protected void close() {
        this.getView().getStage().close();
    }

    public abstract void onShow(WindowEvent event);

    @Override
    public final void initialize(URL location, ResourceBundle resources) {
        Assert.notNull(resources, "ResourceBundle must not be null.");
        this.resources = resources;
    }

    @Autowired
    public void setPublisher(Publisher<Object> publisher) {
        Assert.notNull(publisher, "Publisher must not be null.");
        this.publisher = publisher;
    }

    @Autowired
    public void setSubscriber(Subscriber<Object> subscriber) {
        Assert.notNull(subscriber, "Subscriber must not be null.");
        this.subscriber = subscriber;
    }

    private Object getResource(String key) {
        return resources.getObject(key);
    }

    protected String getResourceString(String key) {
        return String.valueOf(getResource(key));
    }

    protected ResourceBundle getResources() {
        return resources;
    }
}
