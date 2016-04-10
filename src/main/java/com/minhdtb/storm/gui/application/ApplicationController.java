package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.MenuItemBuilder;
import com.minhdtb.storm.common.Publisher;
import com.minhdtb.storm.common.Subscriber;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.gui.newprofile.DialogNewProfileView;
import com.minhdtb.storm.gui.openprofile.DialogOpenProfileView;
import com.minhdtb.storm.services.ProfileService;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class ApplicationController extends AbstractController {

    @FXML
    private Label labelStatus;
    @FXML
    private Label labelSystemTime;
    @FXML
    private MenuItem menuItemNewProfile;
    @FXML
    private MenuItem menuItemOpenProfile;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private TreeView<Object> treeViewProfile;

    @Autowired
    private ProfileService service;

    @Autowired
    private Subscriber<Profile> subscriber;

    @Autowired
    private Publisher<Profile> publisher;

    @Autowired
    DialogNewProfileView dialogNewProfileView;

    @Autowired
    DialogOpenProfileView dialogOpenProfileView;

    private ContextMenu menuTreeView = new ContextMenu();

    private void initGUI() {
        labelStatus.setText("Stopped.");

        menuItemNewProfile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

        GlyphsDude.setIcon(menuItemOpenProfile, MaterialDesignIcon.FOLDER, "1.5em");
        System.out.println(menuItemOpenProfile.getGraphic().getStyleClass());
        menuItemOpenProfile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        GlyphsDude.setIcon(menuItemSave, MaterialDesignIcon.CONTENT_SAVE, "1.5em");
        menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        menuTreeView.getItems().add(MenuItemBuilder.create()
                .setText("New Profile")
                .setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN))
                .setAction(event -> dialogNewProfileView.showDialog(getView())).build());
        menuTreeView.getItems().add(MenuItemBuilder.create()
                .setText("Open Profile")
                .setIcon(MaterialDesignIcon.FOLDER, "1.5em")
                .setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN))
                .setAction(event -> dialogOpenProfileView.showDialog(getView())).build());
    }

    private void openProfile(Profile profile) {
        Platform.runLater(() -> {
            TreeItem<Object> rootItem = new TreeItem<>(profile);

            if (profile.getChannels() != null) {
                for (Channel channel : profile.getChannels()) {
                    rootItem.getChildren().add(createNode(channel));
                }
            }

            treeViewProfile.setRoot(rootItem);
            rootItem.setExpanded(true);
        });
    }

    private void deleteProfile(Profile profile) {
        Platform.runLater(() -> {
            Profile profileCurrent = (Profile) treeViewProfile.getRoot().getValue();
            if (Objects.equals(profileCurrent.getId(), profile.getId())) {
                treeViewProfile.setRoot(null);
            }

            service.delete(profile);
        });
    }

    @Override
    protected void onShow(WindowEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subscriber.on("application:openProfile", this::openProfile);
        subscriber.on("application:deleteProfile", this::deleteProfile);

        initGUI();

        Timer timer = new Timer();
        timer.schedule(new TimeDisplayTask(), 1000, 1000);

        treeViewProfile.setCellFactory(p -> new TreeCellFactory(this));
    }

    public void actionCloseApplication() {
        Platform.exit();
        System.exit(0);
    }

    public void actionNewProfile() {
        dialogNewProfileView.showDialog(getView());
    }

    public void actionOpenProfile() {
        dialogOpenProfileView.showDialog(getView());
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

    @FXML
    public void onTreeViewShowContextMenu(ContextMenuEvent event) {
        if (treeViewProfile.getRoot() == null) {
            menuTreeView.show(treeViewProfile, event.getScreenX(), event.getScreenY());
            event.consume();
        }
    }

    @FXML
    public void onHideMenu() {
        menuTreeView.hide();
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

        private ContextMenu menuVariable = new ContextMenu();
        private ContextMenu menuChannel = new ContextMenu();
        private ContextMenu menuProfile = new ContextMenu();

        private AbstractController controller;

        public TreeCellFactory(AbstractController controller) {
            this.controller = controller;

            menuVariable.getItems().add(new MenuItem("Delete Variable"));

            menuChannel.getItems().add(new MenuItem("New Variable"));
            menuChannel.getItems().add(new MenuItem("Delete Channel"));

            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText("New Channel")
                    .setAction(event -> {
                    }).build());
            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText("Delete Profile")
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Profile profile = (Profile) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(this.controller.getView(),
                                String.format("Do you really want to delete \"%s\"?", profile.getName()),
                                e -> publisher.publish("application:deleteProfile", profile));
                    }).build());
            menuProfile.getItems().add(new SeparatorMenuItem());
            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText("Close")
                    .setAction(event -> treeViewProfile.setRoot(null)).build());
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
                setContextMenu(menuVariable);
            }

            if (item instanceof Channel) {
                setContextMenu(menuChannel);
            }

            if (item instanceof Profile) {
                setContextMenu(menuProfile);
            }
        }
    }
}
