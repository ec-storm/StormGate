package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.ProfileService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DialogNewProfileController extends AbstractController {

    @FXML
    private TextField editNewProfileName;

    @Autowired
    ProfileService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editNewProfileName.setText("New Profile");
    }

    public void actionCancel() {
        this.close();
    }

    public void actionOK() {
        if (!service.profileExists(editNewProfileName.getText())) {
            Profile profile = new Profile(editNewProfileName.getText());
            service.save(profile);

            this.close();
        } else {
            Utils.showError(this.getView().getStage(), "Profile already exists.");
        }
    }

    @Override
    protected void onShow(WindowEvent event) {

    }
}
