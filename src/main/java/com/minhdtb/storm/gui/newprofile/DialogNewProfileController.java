package com.minhdtb.storm.gui.newprofile;

import com.google.common.base.Strings;
import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

@Controller
public class DialogNewProfileController extends AbstractController {

    @FXML
    private TextField editNewProfileName;

    private final DataManager dataManager;

    @Autowired
    public DialogNewProfileController(DataManager dataManager) {
        Assert.notNull(dataManager, "DataManager must not be null");
        this.dataManager = dataManager;
    }

    public void actionCancel() {
        this.close();
    }

    public void actionOK() {
        if (Strings.isNullOrEmpty(editNewProfileName.getText())) {
            Utils.showError(getView(), getResourceString("MSG001"));
            return;
        }

        Profile profile = new Profile(editNewProfileName.getText());

        if (!dataManager.existProfile(profile)) {
            getPublisher().publish("application:newProfile", profile);
            close();
        } else {
            Utils.showError(getView(), String.format(getResourceString("MSG002"), profile.getName()));
        }
    }

    @Override
    public void onShow(WindowEvent event) {
        editNewProfileName.setText(getResourceString("newProfile"));
    }

    @Override
    public void onCreate() {

    }
}
