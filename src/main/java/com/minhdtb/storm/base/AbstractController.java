package com.minhdtb.storm.base;

import com.minhdtb.storm.services.Publisher;
import com.minhdtb.storm.services.Subscriber;
import javafx.fxml.Initializable;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public abstract class AbstractController implements Initializable {

    @Autowired
    private Publisher<Object> publisher;

    @Autowired
    private Subscriber<Object> subscriber;

    private AbstractView view;

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
    public void initialize(URL location, ResourceBundle resources) {
    }
}
