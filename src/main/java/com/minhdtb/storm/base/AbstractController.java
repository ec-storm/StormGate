package com.minhdtb.storm.base;

import com.minhdtb.storm.StormGateApplication;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractController implements Initializable {
    protected StormGateApplication application;
    protected Stage stage;

    public void setApplication(StormGateApplication application) {
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
