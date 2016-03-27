package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.StormGateApplication;
import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.ProfileService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ApplicationController extends AbstractController {

    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    @FXML
    Label labelStatus;
    @FXML
    Label labelSystemTime;
    @FXML
    MenuItem menuNewProfile;
    @FXML
    MenuItem menuOpenProfile;
    @FXML
    MenuItem menuSave;
    @FXML
    TreeView<String> treeViewProfile;

    @Autowired
    ProfileService service;

    private void initGUI() {
        labelStatus.setText("Stopped.");

        menuNewProfile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

        menuOpenProfile.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).color(Color.BLACK));
        menuOpenProfile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        menuSave.setGraphic(fontAwesome.create(FontAwesome.Glyph.SAVE).color(Color.BLACK));
        menuSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        listProfiles();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initGUI();

        Timer timer = new Timer();
        timer.schedule(new TimeDisplayTask(), 1000, 1000);
    }

    public void actionCloseApplication() {
        Platform.exit();
        System.exit(0);
    }

    public void actionNewProfile() {
        ((StormGateApplication) this.application).showDialogNewProfile();
    }

    public void listProfiles() {
        List<Profile> profileList = service.findAllProfile();

        TreeItem<String> root = new TreeItem<>("root");

        profileList.forEach(profile -> {
            TreeItem<String> item = new TreeItem<>(profile.getName(), null);
            root.getChildren().add(item);
        });

        treeViewProfile.setRoot(root);
        treeViewProfile.setShowRoot(false);
    }

    class TimeDisplayTask extends TimerTask {
        public void run() {
            Platform.runLater(() -> {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
                Calendar cal = Calendar.getInstance();
                labelSystemTime.setText(String.format("Now is %s", dateFormat.format(cal.getTime())));
            });
        }
    }
}
