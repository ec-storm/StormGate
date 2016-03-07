package com.minhdtb.storm.gui.newprofile;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class NewProfilePresenter implements Initializable {

    @FXML
    private Parent dialogNewProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void actionCancel() {
        Stage stage = (Stage) dialogNewProfile.getScene().getWindow();
        stage.close();
    }
}
