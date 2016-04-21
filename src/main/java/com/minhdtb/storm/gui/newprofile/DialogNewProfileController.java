package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.Publisher;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DialogNewProfileController extends AbstractController {

    @FXML
    private TextField editNewProfileName;

    @Autowired
    DataService service;

    @Autowired
    private Publisher<Profile> publisher;

    public void actionCancel() {
        this.close();
    }

    public void actionOK() {
        if (!service.profileExists(editNewProfileName.getText())) {
            Profile profile = new Profile(editNewProfileName.getText());
            publisher.publish("application:openProfile", service.save(profile));
            close();
        } else {
            Utils.showError(this.getView(), "Profile already exists.");
        }
    }

    @Override
    protected void onShow(WindowEvent event) {
        editNewProfileName.setText("New Profile");
    }
}
