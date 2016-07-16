package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractController;
import static com.minhdtb.storm.common.GlobalConstants.KEY_ERROR_PROFILE_EXISTS;
import static com.minhdtb.storm.common.GlobalConstants.KEY_NEW_PROFILE;

import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class DialogNewProfileController extends AbstractController {

    @FXML
    private TextField editNewProfileName;

    private final DataManager dataManager;

    private ResourceBundle resources;

    @Autowired
    public DialogNewProfileController(DataManager dataManager) {
        Assert.notNull(dataManager, "DataManager must not be null");
        this.dataManager = dataManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
    }

    public void actionCancel() {
        this.close();
    }

    public void actionOK() {
        Profile profile = new Profile(editNewProfileName.getText());

        if (!dataManager.existProfile(profile)) {
            getPublisher().publish("application:newProfile", profile);
            close();
        } else {
            Utils.showError(getView(), String.format(resources.getString(KEY_ERROR_PROFILE_EXISTS), profile.getName()));
        }
    }

    @Override
    public void onShow(WindowEvent event) {
        editNewProfileName.setText(resources.getString(KEY_NEW_PROFILE));
    }
}
