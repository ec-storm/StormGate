package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.gui.newprofile.NewProfileView;
import com.minhdtb.storm.services.ProfileService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ApplicationPresenter implements Initializable {
    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    @Autowired
    ProfileService service;
    @Autowired
    NewProfileView dialogView;

    @FXML
    Label labelStatus;
    @FXML
    Label labelSystemTime;
    @FXML
    MenuItem menuNewProfile;
    @FXML
    MenuItem menuOpenProfile;
    @FXML
    public MenuItem menuSave;
    @FXML
    private Parent application;

    private void initGUI() {
        labelStatus.setText("Stopped.");

        menuNewProfile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

        menuOpenProfile.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).color(Color.BLACK));
        menuOpenProfile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        menuSave.setGraphic(fontAwesome.create(FontAwesome.Glyph.SAVE).color(Color.BLACK));
        menuSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
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
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Profile");
        dialogStage.resizableProperty().setValue(Boolean.FALSE);
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Window parent = application.getScene().getWindow();
        dialogStage.initOwner(parent);

        if (dialogView.getView().getScene() == null) {
            Scene scene = new Scene(dialogView.getView());
            dialogStage.setScene(scene);
        } else {
            dialogStage.setScene(dialogView.getView().getScene());
        }

        dialogStage.showAndWait();
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
