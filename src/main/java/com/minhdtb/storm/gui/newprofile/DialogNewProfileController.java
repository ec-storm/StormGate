package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.entities.Profile;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.stereotype.Component;

@Component
public class DialogNewProfileController extends AbstractController {

    @FXML
    private TextField editNewProfileName;

    public void actionCancel() {
        this.close();
    }

    public void actionOK() {
        Profile profile = new Profile(editNewProfileName.getText());
        getPublisher().publish("application:newProfile", profile);
        close();
    }

    @Override
    protected void onShow(WindowEvent event) {
        editNewProfileName.setText("New Profile");
    }
}
