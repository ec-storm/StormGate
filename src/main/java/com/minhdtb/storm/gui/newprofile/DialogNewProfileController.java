package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.services.ProfileService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DialogNewProfileController extends AbstractController {

    @FXML
    private Parent dialogNewProfile;

    @Autowired
    ProfileService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void actionCancel() {
        Stage stage = (Stage) dialogNewProfile.getScene().getWindow();
        stage.close();
    }
}
