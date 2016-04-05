package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.StormGateApplication;
import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.base.Subscriber;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.services.ProfileService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
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
import java.util.stream.Collectors;


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
    TreeView<Object> treeViewProfile;
    @FXML
    public PropertySheet propertySheetInformation;

    @Autowired
    ProfileService service;

    @Autowired
    private Subscriber<Profile> subscriber;

    private void initGUI() {
        labelStatus.setText("Stopped.");

        menuNewProfile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

        menuOpenProfile.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).color(Color.BLACK));
        menuOpenProfile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        menuSave.setGraphic(fontAwesome.create(FontAwesome.Glyph.SAVE).color(Color.BLACK));
        menuSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
    }

    private void openProfile(Profile profile) {
        Platform.runLater(() -> {
            TreeItem<Object> rootItem = new TreeItem<>(profile,
                    fontAwesome.create(FontAwesome.Glyph.ROCKET));

            for (Channel channel : profile.getChannels()) {
                rootItem.getChildren().add(createNode(channel));
            }

            treeViewProfile.setRoot(rootItem);
            rootItem.setExpanded(true);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subscriber.on("application:openProfile", this::openProfile);

        initGUI();

        Timer timer = new Timer();
        timer.schedule(new TimeDisplayTask(), 1000, 1000);

        treeViewProfile.setCellFactory(p -> new TreeCellFactory());
    }

    public void actionCloseApplication() {
        Platform.exit();
        System.exit(0);
    }

    public void actionNewProfile() {
        ((StormGateApplication) this.application).showDialogNewProfile();
    }

    public void actionOpenProfile() {
        ((StormGateApplication) this.application).showDialogOpenProfile();
    }

    private TreeItem<Object> createNode(Object o) {
        TreeItem<Object> treeItem = new TreeItem<Object>(o) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<Object>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;

                    super.getChildren().setAll(buildChildren(this));
                }

                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;

                    if (o instanceof Variable) {
                        isLeaf = true;
                    }
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<Object>> buildChildren(TreeItem<Object> treeItem) {
                if (treeItem.getValue() instanceof Channel) {
                    ObservableList<TreeItem<Object>> children = FXCollections.observableArrayList();

                    children.addAll(((Channel) treeItem.getValue())
                            .getVariables().stream().map(variable -> createNode(variable)).collect(Collectors.toList()));

                    return children;
                }

                return FXCollections.emptyObservableList();
            }
        };

        treeItem.setExpanded(true);

        return treeItem;
    }

    private final class TimeDisplayTask extends TimerTask {
        public void run() {
            Platform.runLater(() -> {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
                Calendar cal = Calendar.getInstance();
                labelSystemTime.setText(String.format("Now is %s", dateFormat.format(cal.getTime())));
            });
        }
    }

    private final class TreeCellFactory extends TreeCell<Object> {

        private ContextMenu variableMenu = new ContextMenu();
        private ContextMenu channelMenu = new ContextMenu();
        private ContextMenu profileMenu = new ContextMenu();

        public TreeCellFactory() {
            variableMenu.getItems().add(new MenuItem("Delete Variable"));

            channelMenu.getItems().add(new MenuItem("Add Variable"));
            channelMenu.getItems().add(new MenuItem("Delete Channel"));

            MenuItem menuItem = new MenuItem("Add Channel");
            menuItem.setOnAction(event -> ((StormGateApplication) application).showDialogOpenProfile());
            profileMenu.getItems().add(menuItem);
            profileMenu.getItems().add(new MenuItem("Delete Profile"));
        }

        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
            } else {
                if (item instanceof Channel) {
                    setText(((Channel) item).getName());
                } else if (item instanceof Variable) {
                    setText(((Variable) item).getName());
                } else if (item instanceof Profile) {
                    setText(((Profile) item).getName());
                } else {
                    setText(item.toString());
                }
            }

            if (item instanceof Variable) {
                setContextMenu(variableMenu);
            }

            if (item instanceof Channel) {
                setContextMenu(channelMenu);
            }

            if (item instanceof Profile) {
                setContextMenu(profileMenu);
            }
        }
    }
}
