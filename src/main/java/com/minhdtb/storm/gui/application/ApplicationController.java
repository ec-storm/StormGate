package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.GraphicItemBuilder;
import com.minhdtb.storm.common.MenuItemBuilder;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.engine.StormEngine;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.gui.newchannel.DialogNewChannelView;
import com.minhdtb.storm.gui.newprofile.DialogNewProfileView;
import com.minhdtb.storm.gui.newvariable.DialogNewVariableIECView;
import com.minhdtb.storm.gui.openprofile.DialogOpenProfileView;
import com.minhdtb.storm.services.DataManager;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import org.controlsfx.control.PropertySheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.getInstance;

@Controller
public class ApplicationController extends AbstractController {

    @FXML
    private WebView webViewScript;
    @FXML
    public AnchorPane paneScript;
    @FXML
    private ScrollPane scrollLog;
    @FXML
    public Button buttonRun;
    @FXML
    public TextFlow textFlowLog;
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
    @FXML
    private PropertySheet propDetail;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private StormEngine stormEngine;

    @Autowired
    private DialogNewProfileView dialogNewProfileView;

    @Autowired
    private DialogNewChannelView dialogNewChannelView;

    @Autowired
    private DialogOpenProfileView dialogOpenProfileView;

    @Autowired
    private DialogNewVariableIECView dialogNewVariableIECView;

    private ContextMenu menuTreeView = new ContextMenu();

    private boolean isRunning;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getSubscriber().on("application:openProfile", this::openProfile);
        getSubscriber().on("application:newProfile", this::newProfile);
        getSubscriber().on("application:deleteProfile", this::deleteProfile);
        getSubscriber().on("application:saveProfile", this::saveProfile);

        getSubscriber().on("application:addChannel", this::addChannel);
        getSubscriber().on("application:deleteChannel", this::deleteChannel);

        getSubscriber().on("application:addVariable", this::addVariable);
        getSubscriber().on("application:deleteVariable", this::deleteVariable);

        getSubscriber().on("application:log", message -> Platform.runLater(() ->
                writeLog("-fx-fill: black;-fx-font-weight:bold;",
                        "-fx-fill: black;-fx-font-size: 14px;", (String) message)));

        getSubscriber().on("application:error", message -> Platform.runLater(() ->
                writeLog("-fx-fill: #ff4405;-fx-font-weight:bold;",
                        "-fx-fill: #ff4405;-fx-font-size: 14px;", (String) message)));

        initGUI();
    }

    @Override
    public void onShow(WindowEvent event) {
        Timer timer = new Timer();
        timer.schedule(new TimeDisplayTask(), 1000, 1000);
    }

    private void initGUI() {
        labelStatus.setText("Stopped.");

        setButtonRun(MaterialDesignIcon.PLAY, "black", "START");
        buttonRun.setDisable(true);

        Platform.runLater(() -> {
            webViewScript = new WebView();
            webViewScript.getEngine().load(getClass().getResource("/html/ace.html").toExternalForm());
            paneScript.getChildren().add(webViewScript);
            AnchorPane.setTopAnchor(webViewScript, 3.0);
            AnchorPane.setBottomAnchor(webViewScript, 3.0);
            AnchorPane.setLeftAnchor(webViewScript, 3.0);
            AnchorPane.setRightAnchor(webViewScript, 3.0);

            webViewScript.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.V) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    String content = Utils.replaceSpecialCharacters((String) clipboard.getContent(DataFormat.PLAIN_TEXT));
                    if (content != null) {
                        webViewScript.getEngine().executeScript("pasteContent(\"" + content + "\")");
                    }
                }
            });

            webViewScript.setContextMenuEnabled(false);
        });

        textFlowLog.setMaxHeight(4000.0);
        textFlowLog.setStyle("-fx-background-color: white");

        menuItemNewProfile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

        GlyphsDude.setIcon(menuItemOpenProfile, MaterialDesignIcon.FOLDER, "1.5em");
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

        treeViewProfile.setCellFactory(p -> new TreeCellFactory(this));

        treeViewProfile.setOnMouseClicked(event -> {
            if (treeViewProfile.getSelectionModel().getSelectedItem() != null) {
                Object selected = treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                if (selected instanceof Profile) {
                    showProfileProperties((Profile) selected);
                } else if (selected instanceof Channel) {
                    propDetail.getItems().clear();
                } else {
                    propDetail.getItems().clear();
                }
            }
        });
    }

    private void openProfile(Object object) {
        if (object instanceof Profile) {
            dataManager.openProfile((Profile) object, profile -> Platform.runLater(() -> {
                TreeItem<Object> rootItem = new TreeItem<>(profile);
                if (profile.getChannels() != null) {
                    for (Channel channel : profile.getChannels()) {
                        rootItem.getChildren().add(createNode(channel));
                    }
                }

                treeViewProfile.setRoot(rootItem);
                rootItem.setExpanded(true);

                if (profile.getScript() != null) {
                    Platform.runLater(() -> {
                        String script = Utils.replaceSpecialCharacters(new String(profile.getScript()));
                        webViewScript.getEngine().executeScript("editor.setValue('" + script + "')");
                    });
                } else {
                    Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setValue('')"));
                }

                buttonRun.setDisable(false);
                showProfileProperties(profile);
            }));
        }
    }

    private void newProfile(Object object) {
        if (object instanceof Profile) {
            dataManager.saveProfile((Profile) object, this::openProfile);
        }
    }

    private void deleteProfile(Object object) {
        if (object instanceof Profile) {
            dataManager.deleteProfile((Profile) object, profile -> Platform.runLater(() -> {
                if (treeViewProfile.getRoot() != null) {
                    Profile profileCurrent = (Profile) treeViewProfile.getRoot().getValue();
                    if (Objects.equals(profileCurrent.getId(), profile.getId())) {
                        treeViewProfile.setRoot(null);
                        buttonRun.setDisable(true);
                        Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setValue('')"));
                    }
                }
            }));
        }
    }

    private void addChannel(Object object) {
        if (object instanceof Channel) {
            dataManager.addChannel((Channel) object, (profile, channel) -> Platform.runLater(() -> {
                treeViewProfile.getRoot().setValue(profile);
                treeViewProfile.getRoot().getChildren().add(createNode(channel));
            }));
        }
    }

    private void deleteChannel(Object object) {
        if (object instanceof Channel) {
            dataManager.deleteChannel((Channel) object, (profile, channel) -> Platform.runLater(() -> {
                treeViewProfile.getRoot().setValue(profile);
                TreeItem item = (TreeItem) treeViewProfile.getSelectionModel().getSelectedItem();
                item.getParent().getChildren().remove(item);
            }));
        }
    }

    private void addVariable(Object object) {
        if (object instanceof Variable) {
            Channel channel = (Channel) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
            dataManager.addVariable(channel, (Variable) object, variable -> Platform.runLater(() ->
                    treeViewProfile.getSelectionModel().getSelectedItem().getChildren().add(createNode(variable))));
        }
    }

    private void deleteVariable(Object object) {
        if (object instanceof Variable) {
            dataManager.deleteVariable((Variable) object, variable -> Platform.runLater(() -> {
                TreeItem item = (TreeItem) treeViewProfile.getSelectionModel().getSelectedItem();
                item.getParent().getChildren().remove(item);
            }));
        }
    }

    private void showProfileProperties(Profile profile) {
        propDetail.getItems().clear();
        propDetail.getItems().add(new PropertyItem("General", "Name", profile.getName()));
        propDetail.getItems().add(new PropertyItem("General", "Description", profile.getDescription()));
        propDetail.setUserData(profile);
    }

    private void writeLog(String timeStyle, String textStyle, String message) {
        String now = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(getInstance().getTime());
        Text txtTime = new Text(now + "> ");
        txtTime.setStyle(timeStyle);

        Text txtMessage = new Text(message);
        txtMessage.setStyle(textStyle);

        textFlowLog.getChildren().addAll(txtTime, txtMessage);
        scrollLog.setVvalue(1.0);
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

                    try {
                        children.addAll(((Channel) treeItem.getValue())
                                .getVariables().stream().map(variable -> createNode(variable)).collect(Collectors.toList()));
                    } catch (NullPointerException ignored) {

                    }

                    return children;
                }

                return FXCollections.emptyObservableList();
            }
        };

        treeItem.setExpanded(true);

        return treeItem;
    }

    private void setButtonRun(GlyphIcons glyphIcons, String color, String text) {
        GlyphIcon icon = GlyphsBuilder.create(MaterialDesignIconView.class)
                .glyph(glyphIcons)
                .size("1.5em")
                .style("-fx-fill: " + color)
                .build();

        buttonRun.setGraphic(GraphicItemBuilder.create()
                .setIcon(icon)
                .setLabelPadding(new Insets(0, 3, 0, 3))
                .setFont(Font.font(null, FontWeight.BOLD, 12))
                .setText(text)
                .build());
    }

    @FXML
    public void actionCloseApplication() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void actionNewProfile() {
        dialogNewProfileView.showDialog(getView());
    }

    @FXML
    public void actionOpenProfile() {
        dialogOpenProfileView.showDialog(getView());
    }

    @FXML
    public void actionSaveProfile() {
        Profile profile = dataManager.getCurrentProfile();
        if (profile != null) {
            Platform.runLater(() -> {
                String script = (String) webViewScript.getEngine().executeScript("editor.getValue()");
                profile.setScript(script.getBytes());
            });

            dataManager.saveProfile(profile, null);
        }
    }

    @FXML
    public void actionRun() {
        if (!isRunning) {
            stormEngine.start(profile -> {
                setButtonRun(MaterialDesignIcon.STOP, "red", "STOP");
                treeViewProfile.setDisable(true);
                labelStatus.setText("Running.");
                textFlowLog.getChildren().clear();
                isRunning = true;
                Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setReadOnly(true)"));
            });
        } else {
            stormEngine.stop();
            setButtonRun(MaterialDesignIcon.PLAY, "black", "START");
            isRunning = false;
            labelStatus.setText("Stopped.");
            treeViewProfile.setDisable(false);
            Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setReadOnly(false)"));
        }
    }

    @FXML
    public void actionSave() {
        Object userData = propDetail.getUserData();
        String confirmMessage = "";
        String tmpKey = "application:";
        if (userData instanceof Profile) {
            confirmMessage = String.format("Do you really want to save profile \"%s\"?", ((Profile) userData).getName());
            tmpKey += "saveProfile";
        }
        final String key = tmpKey;
        Utils.showConfirm(getView(), confirmMessage, e -> getPublisher().publish(key, userData));
    }

    private void saveProfile(Object userData) {
        if (!(userData instanceof Profile)) {
            return;
        }
        Profile profile = (Profile) userData;
        List<PropertySheet.Item> items = propDetail.getItems();
        String currentName = profile.getName();
        String currentDescription = profile.getDescription();
        profile.setName((String) items.get(0).getValue());
        if (!dataManager.existProfile(profile)) {
            profile.setDescription((String) items.get(1).getValue());
            dataManager.saveProfile((Profile) userData, null);
        } else {
            profile.setName(currentName);
            profile.setDescription(currentDescription);
            Platform.runLater(() ->
                    Utils.showError(getView(), String.format("Profile \"%s\" already exists.", profile.getName())));
        }
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

    private final class PropertyItem implements PropertySheet.Item {

        private String category;
        private String name;
        private Object value;

        PropertyItem(String category, String name, Object value) {
            this.category = category;
            this.name = name;
            this.value = value;
        }

        @Override
        public Class<?> getType() {
            if (this.value != null) {
                return this.value.getClass();
            } else {
                return "".getClass();
            }
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public Optional<ObservableValue<?>> getObservableValue() {
            return Optional.empty();
        }
    }

    private final class TreeCellFactory extends TreeCell<Object> {

        private ContextMenu menuVariable = new ContextMenu();
        private ContextMenu menuChannel = new ContextMenu();
        private ContextMenu menuProfile = new ContextMenu();

        private AbstractController controller;

        private AbstractController getController() {
            return this.controller;
        }

        TreeCellFactory(AbstractController controller) {
            this.controller = controller;

            menuVariable.getItems().add(MenuItemBuilder.create()
                    .setText("Delete Variable")
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Variable variable = (Variable) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(getController().getView(),
                                String.format("Do you really want to delete variable \"%s\"?", variable.getName()),
                                e -> getPublisher().publish("application:deleteVariable", variable));
                    }).build());

            menuChannel.getItems().add(MenuItemBuilder.create()
                    .setText("New Variable")
                    .setAction(event -> {
                        Channel channel = (Channel) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        if (channel.getType() == Channel.ChannelType.CT_IEC_CLIENT ||
                                channel.getType() == Channel.ChannelType.CT_IEC_SERVER) {
                            dialogNewVariableIECView
                                    .setChannel(channel)
                                    .showDialog(getController().getView());
                        }
                    }).build());

            menuChannel.getItems().add(MenuItemBuilder.create()
                    .setText("Delete Channel")
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Channel channel = (Channel) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(getController().getView(),
                                String.format("Do you really want to delete channel \"%s\"?", channel.getName()),
                                e -> getPublisher().publish("application:deleteChannel", channel));
                    }).build());

            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText("New Channel")
                    .setAction(event -> dialogNewChannelView.showDialog(getController().getView()))
                    .build());
            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText("Delete Profile")
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Profile profile = (Profile) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(getController().getView(),
                                String.format("Do you really want to delete profile \"%s\"?", profile.getName()),
                                e -> getPublisher().publish("application:deleteProfile", profile));
                    }).build());
            menuProfile.getItems().add(new SeparatorMenuItem());
            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText("Close")
                    .setAction(event -> {
                        treeViewProfile.getRoot().getChildren().clear();
                        treeViewProfile.setRoot(null);
                        buttonRun.setDisable(true);
                        Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setValue('')"));
                    }).build());
        }

        private String getColor(Channel.ChannelType type) {
            String color = "red";
            switch (type) {
                case CT_IEC_CLIENT:
                    color = "green";
                    break;
                case CT_OPC_CLIENT:
                    color = "blue";
                    break;
            }

            return color;
        }

        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                if (item instanceof Channel) {
                    Channel channel = (Channel) item;

                    GlyphIcon icon = GlyphsBuilder.create(MaterialDesignIconView.class)
                            .glyph(MaterialDesignIcon.PANORAMA_FISHEYE)
                            .size("1.2em")
                            .style("-fx-fill: " + getColor(channel.getType()))
                            .build();

                    setGraphic(GraphicItemBuilder.create()
                            .setIcon(icon)
                            .setText(channel.getName())
                            .build());
                } else if (item instanceof Variable) {
                    Variable variable = (Variable) item;

                    GlyphIcon icon = GlyphsBuilder.create(MaterialDesignIconView.class)
                            .glyph(MaterialDesignIcon.TAG)
                            .size("1.2em")
                            .style("-fx-fill: " + getColor(variable.getChannel().getType()))
                            .build();

                    setGraphic(GraphicItemBuilder.create()
                            .setIcon(icon)
                            .setText(variable.getName())
                            .build());
                } else if (item instanceof Profile) {
                    GlyphIcon icon = GlyphsBuilder.create(MaterialDesignIconView.class)
                            .glyph(MaterialDesignIcon.ACCOUNT)
                            .size("1.5em")
                            .build();

                    setGraphic(GraphicItemBuilder.create()
                            .setIcon(icon)
                            .setText(null)
                            .build());
                } else {
                    setText(null);
                    setGraphic(null);
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
