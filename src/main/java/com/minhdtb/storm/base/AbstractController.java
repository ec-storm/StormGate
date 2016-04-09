package com.minhdtb.storm.base;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractController implements Initializable {
    protected AbstractApplication application;
    protected Stage stage;

    public void setApplication(AbstractApplication application) {
        this.application = application;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        this.stage.close();
    }

    protected  abstract void onShow(WindowEvent event);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
