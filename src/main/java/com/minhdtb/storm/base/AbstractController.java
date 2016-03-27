package com.minhdtb.storm.base;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractController implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
