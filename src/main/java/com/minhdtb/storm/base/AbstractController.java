package com.minhdtb.storm.base;

import javafx.fxml.Initializable;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractController implements Initializable {

    private AbstractView view;


    public void setView(AbstractView view) {
        this.view = view;
    }

    public AbstractView getView() {
        return this.view;
    }

    public void close() {
        this.getView().getStage().close();
    }

    protected abstract void onShow(WindowEvent event);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
