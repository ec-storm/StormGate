package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class NewProfileController extends AbstractController {

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
