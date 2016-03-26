package com.minhdtb.storm.base;

import com.minhdtb.storm.StormGateApplication;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractController implements Initializable {
    protected StormGateApplication application;

    public void setApplication(StormGateApplication application) {
        this.application = application;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
