package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.GraphicItemBuilder;
import com.minhdtb.storm.common.MenuItemBuilder;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.engine.StormEngine;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.ChannelAttribute;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.gui.newchannel.DialogNewChannelView;
import com.minhdtb.storm.gui.newprofile.DialogNewProfileView;
import com.minhdtb.storm.gui.newvariableiec.DialogNewVariableIECView;
import com.minhdtb.storm.gui.newvariableopc.DialogNewVariableOPCView;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import org.controlsfx.control.PropertySheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.minhdtb.storm.common.GlobalConstants.*;
import static com.minhdtb.storm.core.data.StormChannelIECClient.HOST;
import static com.minhdtb.storm.core.data.StormChannelIECClient.PORT;
import static com.minhdtb.storm.core.data.StormChannelOPCClient.PROG_ID;
import static com.minhdtb.storm.core.data.StormChannelOPCClient.REFRESH_RATE;
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
    @FXML
    private VBox propDetailBox;

    private final DataManager dataManager;

    private final StormEngine stormEngine;

    private final DialogNewProfileView dialogNewProfileView;

    private final DialogNewChannelView dialogNewChannelView;

    private final DialogOpenProfileView dialogOpenProfileView;

    private final DialogNewVariableIECView dialogNewVariableIECView;

    private final DialogNewVariableOPCView dialogNewVariableOPCView;

    private ContextMenu menuTreeView = new ContextMenu();

    private ContextMenu menuLog = new ContextMenu();

    private boolean isRunning;

    private boolean isAutoScroll;

    private ResourceBundle resources;

    @Autowired
    public ApplicationController(DialogNewChannelView dialogNewChannelView,
                                 DialogOpenProfileView dialogOpenProfileView,
                                 DialogNewVariableIECView dialogNewVariableIECView,
                                 DialogNewVariableOPCView dialogNewVariableOPCView,
                                 DialogNewProfileView dialogNewProfileView,
                                 StormEngine stormEngine,
                                 DataManager dataManager) {
        Assert.notNull(stormEngine, "StormEngine must not be null.");
        Assert.notNull(dataManager, "DataManager must not be null");

        this.dialogNewChannelView = dialogNewChannelView;
        this.dialogOpenProfileView = dialogOpenProfileView;
        this.dialogNewVariableIECView = dialogNewVariableIECView;
        this.dialogNewVariableOPCView = dialogNewVariableOPCView;
        this.dialogNewProfileView = dialogNewProfileView;

        this.stormEngine = stormEngine;
        this.dataManager = dataManager;
    }

    private enum ItemType {
        PROFILE, CHANNEL, VARIABLE
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        getSubscriber().on("application:openProfile", this::openProfile);
        getSubscriber().on("application:newProfile", this::newProfile);
        getSubscriber().on("application:deleteProfile", this::deleteProfile);
        getSubscriber().on("application:saveProfile", this::saveProfile);
        getSubscriber().on("application:saveChannel", this::saveChannel);

        getSubscriber().on("application:addChannel", this::addChannel);
        getSubscriber().on("application:deleteChannel", this::deleteChannel);

        getSubscriber().on("application:addVariable", this::addVariable);
        getSubscriber().on("application:deleteVariable", this::deleteVariable);

        getSubscriber().on("application:log", message -> Platform.runLater(() ->
                writeLog("-fx-fill: black;-fx-font-weight:bold;",
                        "-fx-fill: black;-fx-font-size: 14px;", (String) message)));

        getSubscriber().on("application:error", message -> Platform.runLater(() ->
                writeLog("-fx-fill: red;-fx-font-weight:bold;",
                        "-fx-fill: red;-fx-font-size: 14px;", (String) message)));

        initGUI();
    }

    @Override
    public void onShow(WindowEvent event) {
        Timer timer = new Timer();
        timer.schedule(new TimeDisplayTask(), 1000, 1000);
    }

    private void initGUI() {
        labelStatus.setText(resources.getString(KEY_STOPPED));

        setButtonRun(MaterialDesignIcon.PLAY, "black", resources.getString(KEY_START));
        buttonRun.setDisable(true);

        propDetail.setSearchBoxVisible(false);

        Platform.runLater(() -> {
            webViewScript = new WebView();
            webViewScript.getEngine().load(getClass().getResource("/html/ace.html").toExternalForm());
            paneScript.getChildren().add(webViewScript);
            AnchorPane.setTopAnchor(webViewScript, 3.0);
            AnchorPane.setBottomAnchor(webViewScript, 3.0);
            AnchorPane.setLeftAnchor(webViewScript, 3.0);
            AnchorPane.setRightAnchor(webViewScript, 3.0);
            webViewScript.setContextMenuEnabled(false);
        });

        textFlowLog.setMaxHeight(4000.0);
        textFlowLog.setStyle("-fx-background-color: white");

        menuItemNewProfile.setAccelerator(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

        GlyphsDude.setIcon(menuItemOpenProfile, MaterialDesignIcon.FOLDER, "1.5em");
        menuItemOpenProfile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        GlyphsDude.setIcon(menuItemSave, MaterialDesignIcon.CONTENT_SAVE, "1.5em");
        menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        menuTreeView.getItems().add(MenuItemBuilder.create()
                .setText(resources.getString(KEY_MENU_NEW_PROFILE))
                .setAccelerator(
                        new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN))
                .setAction(event -> dialogNewProfileView.showDialog(
                        getView(), resources.getString(KEY_NEW_PROFILE))).build());
        menuTreeView.getItems().add(MenuItemBuilder.create()
                .setText(resources.getString(KEY_MENU_OPEN_PROFILE))
                .setIcon(MaterialDesignIcon.FOLDER, "1.5em")
                .setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN))
                .setAction(event -> dialogOpenProfileView.showDialog(
                        getView(), resources.getString(KEY_OPEN_PROFILE))).build());

        menuLog.getItems().add(MenuItemBuilder.create()
                .setText(resources.getString(KEY_MENU_CLEAR_ALL))
                .setAction(event -> textFlowLog.getChildren().clear()).build());
        menuLog.getItems().add(MenuItemBuilder.create()
                .setText(resources.getString(KEY_MENU_COPY_ALL))
                .setAction(event -> copyLog()).build());
        menuLog.getItems().add(MenuItemBuilder.create()
                .setText(resources.getString(KEY_MENU_DISABLE_AUTO_SCROLL))
                .setAction(event -> setAutoScroll()).build());
        isAutoScroll = true;

        treeViewProfile.setCellFactory(p -> new TreeCellFactory(this));

        treeViewProfile.setOnMouseClicked(event -> {
            if (treeViewProfile.getSelectionModel().getSelectedItem() != null) {
                Object selected = treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                if (selected instanceof Profile) {
                    showProfile((Profile) selected);
                } else if (selected instanceof Channel) {
                    showChannel((Channel) selected);
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
                        String script = replaceSpecialCharacters(new String(profile.getScript()));
                        webViewScript.getEngine().executeScript("editor.setValue('" + script + "')");
                    });
                } else {
                    Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setValue('')"));
                }

                buttonRun.setDisable(false);
                showProfile(profile);
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
                propDetailBox.setVisible(false);
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
            dataManager.addChannel((Channel) object, profile -> Platform.runLater(() -> openProfile(profile)));
        }
    }

    private void deleteChannel(Object object) {
        if (object instanceof Channel) {
            dataManager.deleteChannel((Channel) object, profile -> Platform.runLater(() -> openProfile(profile)));
        }
    }

    private void addVariable(Object object) {
        if (object instanceof Variable) {
            Channel channel = (Channel) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
            dataManager.addVariable(channel, (Variable) object, profile -> Platform.runLater(() -> openProfile(profile)));
        }
    }

    private void deleteVariable(Object object) {
        if (object instanceof Variable) {
            dataManager.deleteVariable((Variable) object, profile -> Platform.runLater(() -> openProfile(profile)));
        }
    }

    private void showProfile(Profile profile) {
        propDetailBox.setVisible(true);
        propDetail.getItems().clear();
        propDetail.getItems().add(new PropertyItem(
                resources.getString(KEY_GENERAL), resources.getString(KEY_NAME), profile.getName()));
        propDetail.getItems().add(new PropertyItem(
                resources.getString(KEY_GENERAL), resources.getString(KEY_DESCRIPTION), profile.getDescription()));
        Object[] userData = {ItemType.PROFILE, profile};
        propDetail.setUserData(userData);
    }

    private String getName(Channel channel, ChannelAttribute channelAttribute) {
        String name = "";
        switch (channelAttribute.getName()) {
            case HOST:
                if (channel.getType() == Channel.ChannelType.CT_IEC_CLIENT) {
                    name = resources.getString(KEY_SERVER_IP);
                } else if (channel.getType() == Channel.ChannelType.CT_IEC_SERVER) {
                    name = resources.getString(KEY_BIND_IP);
                }
                break;
            case PORT:
                name = resources.getString(KEY_PORT);
                break;
            case PROG_ID:
                name = resources.getString(KEY_PROG_ID);
                break;
            case REFRESH_RATE:
                name = resources.getString(KEY_REFRESH_RATE);
                break;
        }

        return name;
    }

    private void showChannel(Channel channel) {
        propDetail.getItems().clear();
        propDetail.getItems().add(new PropertyItem(
                resources.getString(KEY_GENERAL), resources.getString(KEY_NAME), channel.getName()));
        propDetail.getItems().add(new PropertyItem(
                resources.getString(KEY_GENERAL),
                resources.getString(KEY_DESCRIPTION),
                channel.getDescription()));
        PropertyItem typeItem = new PropertyItem(
                resources.getString(KEY_GENERAL),
                resources.getString(KEY_TYPE),
                resources.getString(channel.getType().toString()));
        typeItem.setDisable();
        propDetail.getItems().add(typeItem);
        for (ChannelAttribute channelAttribute : channel.getAttributes()) {
            PropertyItem attributeItem =
                    new PropertyItem(resources.getString(KEY_ATTRIBUTES), getName(channel, channelAttribute), channelAttribute.getValue());
            if (channelAttribute.getName().equals(PROG_ID)) {
                attributeItem.setDisable();
            }
            propDetail.getItems().add(attributeItem);
        }

        Object[] userData = {ItemType.CHANNEL, channel};
        propDetail.setUserData(userData);
    }

    private void writeLog(String timeStyle, String textStyle, String message) {
        String now = new SimpleDateFormat(DATE_TIME_FORMAT).format(getInstance().getTime());
        Text txtTime = new Text(now + "> ");
        txtTime.setStyle(timeStyle);

        Text txtMessage = new Text(message);
        txtMessage.setStyle(textStyle);

        textFlowLog.getChildren().addAll(txtTime, txtMessage);
        if (isAutoScroll) {
            scrollLog.setVvalue(1.0);
        }
    }

    private void copyLog() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        StringBuilder log = new StringBuilder();
        List<Node> nodes = textFlowLog.getChildren();
        for (int i = 0; i < nodes.size() / 2; i++) {
            for (int j = 2 * i; j <= 2 * i + 1; j++) {
                log.append(((Text) nodes.get(j)).getText());
            }
        }
        content.putString(log.toString());
        clipboard.setContent(content);
    }


    private void setAutoScroll() {
        isAutoScroll = !isAutoScroll;
        if (isAutoScroll) {
            menuLog.getItems().get(2).setText(resources.getString(KEY_MENU_DISABLE_AUTO_SCROLL));
        } else {
            menuLog.getItems().get(2).setText(resources.getString(KEY_MENU_ENABLE_AUTO_SCROLL));
        }
    }

    private String replaceSpecialCharacters(String text) {
        String content = text.replace("'", "\\'");
        content = content.replace("\"", "\\\"");
        content = content.replace(System.getProperty("line.separator"), "\\n");
        content = content.replace("\n", "\\n");
        content = content.replace("\r", "\\n");
        return content;
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
                                .getVariables().stream().map(
                                        variable -> createNode(variable)).collect(Collectors.toList()));
                    } catch (NullPointerException ignored) {

                    }

                    return children;
                }

                return FXCollections.emptyObservableList();
            }
        };

        if (o instanceof Profile)
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
        dialogNewProfileView.showDialog(getView(), resources.getString(KEY_NEW_PROFILE));
    }

    @FXML
    public void actionOpenProfile() {
        dialogOpenProfileView.showDialog(getView(), resources.getString(KEY_OPEN_PROFILE));
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
                setButtonRun(MaterialDesignIcon.STOP, "red", resources.getString(KEY_STOP));
                treeViewProfile.setDisable(true);
                labelStatus.setText(resources.getString(KEY_RUNNING));
                textFlowLog.getChildren().clear();
                isRunning = true;
                Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setReadOnly(true)"));
            });
        } else {
            stormEngine.stop();
            setButtonRun(MaterialDesignIcon.PLAY, "black", resources.getString(KEY_START));
            isRunning = false;
            labelStatus.setText(KEY_STOPPED);
            treeViewProfile.setDisable(false);
            Platform.runLater(() -> webViewScript.getEngine().executeScript("editor.setReadOnly(false)"));
        }
    }

    @FXML
    public void actionSave() {
        Object[] userData = (Object[]) propDetail.getUserData();
        String confirmMessage;
        switch ((ItemType) userData[0]) {
            case PROFILE:
                confirmMessage = String.format(
                        resources.getString(KEY_CONFIRM_SAVE_PROFILE), ((Profile) userData[1]).getName());
                Utils.showConfirm(
                        getView(), confirmMessage, e -> getPublisher().publish("application:saveProfile", userData[1]));
                break;
            case CHANNEL:
                confirmMessage = String.format(
                        resources.getString(KEY_CONFIRM_SAVE_CHANNEL), ((Channel) userData[1]).getName());
                Utils.showConfirm(
                        getView(), confirmMessage, e -> getPublisher().publish("application:saveChannel", userData[1]));
                break;
            case VARIABLE:
                break;
        }

    }

    private void saveProfile(Object userData) {
        Profile profile = (Profile) userData;
        List<PropertySheet.Item> items = propDetail.getItems();
        String currentName = profile.getName();
        String currentDescription = profile.getDescription();
        profile.setName((String) items.get(0).getValue());
        if (dataManager.existProfile(profile)) {
            profile.setName(currentName);
            profile.setDescription(currentDescription);
            Platform.runLater(() -> Utils.showError(
                    getView(), String.format(resources.getString(KEY_ERROR_PROFILE_EXISTS), profile.getName())));
        } else {
            profile.setDescription((String) items.get(1).getValue());
            dataManager.saveProfile((Profile) userData, null);
        }
    }

    private void saveChannel(Object userData) {
        Channel channel = (Channel) userData;
        String oldName = channel.getName();
        List<PropertySheet.Item> items = propDetail.getItems();
        String newName = (String) items.get(0).getValue();
        if (!oldName.equals(newName)) {
            channel.setName(newName);
        }
        channel.setDescription((String) items.get(1).getValue());

        for (int i = 3; i < items.size(); i++) {
            for (ChannelAttribute channelAttribute : channel.getAttributes()) {
                if (items.get(i).getName().equals(getName(channel, channelAttribute))) {
                    channelAttribute.setValue((String) items.get(i).getValue());
                }
            }
        }
        dataManager.saveChannel(channel, profile -> Platform.runLater(() -> {
            if (oldName.equals(newName)) {
                return;
            }
            TreeItem<Object> selectedItem = treeViewProfile.getSelectionModel().getSelectedItem();
            Object value = selectedItem.getValue();
            selectedItem.setValue(null);
            selectedItem.setValue(value);
        }));
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


    @FXML
    public void onLogShowContextMenu(ContextMenuEvent event) {
        menuLog.show(scrollLog, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    @FXML
    public void onHideLogMenu() {
        menuLog.hide();
    }

    private final class TimeDisplayTask extends TimerTask {
        public void run() {
            Platform.runLater(() -> {
                DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT + " ");
                Calendar cal = Calendar.getInstance();
                labelSystemTime.setText(String.format(resources.getString(KEY_NOW), dateFormat.format(cal.getTime())));
            });
        }
    }

    private final class PropertyItem implements PropertySheet.Item {

        private String category;
        private String name;
        private Object value;
        private boolean editable = true;

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
        public boolean isEditable() {
            return editable;
        }

        @Override
        public Optional<ObservableValue<?>> getObservableValue() {
            return Optional.empty();
        }

        void setDisable() {
            editable = false;
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
                    .setText(resources.getString(KEY_MENU_DELETE_VARIABLE))
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Variable variable = (Variable) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(getController().getView(),
                                String.format(resources.getString(KEY_CONFIRM_DELETE_VARIABLE), variable.getName()),
                                e -> getPublisher().publish("application:deleteVariable", variable));
                    }).build());

            menuChannel.getItems().add(MenuItemBuilder.create()
                    .setText(resources.getString(KEY_MENU_NEW_VARIABLE))
                    .setAction(event -> {
                        Channel channel = (Channel) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        if (channel.getType() == Channel.ChannelType.CT_IEC_CLIENT ||
                                channel.getType() == Channel.ChannelType.CT_IEC_SERVER) {
                            dialogNewVariableIECView
                                    .setChannel(channel)
                                    .showDialog(getController().getView(), resources.getString(KEY_NEW_IEC_60870_VARIABLE));
                        } else if (channel.getType() == Channel.ChannelType.CT_OPC_CLIENT) {
                            dialogNewVariableOPCView
                                    .setChannel(channel)
                                    .showDialog(getController().getView(), resources.getString(KEY_NEW_OPC_VARIABLE));
                        }
                    }).build());

            menuChannel.getItems().add(MenuItemBuilder.create()
                    .setText(resources.getString(KEY_MENU_DELETE_CHANNEL))
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Channel channel = (Channel) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(getController().getView(),
                                String.format(resources.getString(KEY_CONFIRM_DELETE_CHANNEL), channel.getName()),
                                e -> getPublisher().publish("application:deleteChannel", channel));
                    }).build());

            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText(resources.getString(KEY_MENU_NEW_CHANNEL))
                    .setAction(event -> dialogNewChannelView.showDialog(
                            getController().getView(), resources.getString(KEY_NEW_CHANNEL)))
                    .build());
            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText(resources.getString(KEY_MENU_DELETE_PROFILE))
                    .setIcon(MaterialDesignIcon.DELETE, "1.5em")
                    .setAccelerator(new KeyCodeCombination(KeyCode.DELETE))
                    .setAction(event -> {
                        Profile profile = (Profile) treeViewProfile.getSelectionModel().getSelectedItem().getValue();
                        Utils.showConfirm(getController().getView(),
                                String.format(resources.getString(KEY_CONFIRM_DELETE_PROFILE), profile.getName()),
                                e -> getPublisher().publish("application:deleteProfile", profile));
                    }).build());
            menuProfile.getItems().add(new SeparatorMenuItem());
            menuProfile.getItems().add(MenuItemBuilder.create()
                    .setText(resources.getString(KEY_MENU_CLOSE))
                    .setAction(event -> {
                        treeViewProfile.getRoot().getChildren().clear();
                        treeViewProfile.setRoot(null);
                        buttonRun.setDisable(true);
                        propDetailBox.setVisible(false);
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

                    GlyphIcon icon;
                    if (channel.getVariables().size() == 0) {
                        icon = GlyphsBuilder.create(MaterialDesignIconView.class)
                                .glyph(MaterialDesignIcon.PANORAMA_FISHEYE)
                                .size("1.2em")
                                .style("-fx-background-color: " + getColor(channel.getType()))
                                .style("-fx-fill: " + getColor(channel.getType()))
                                .build();
                    } else {
                        icon = GlyphsBuilder.create(MaterialDesignIconView.class)
                                .glyph(MaterialDesignIcon.ADJUST)
                                .size("1.2em")
                                .style("-fx-background-color: " + getColor(channel.getType()))
                                .style("-fx-fill: " + getColor(channel.getType()))
                                .build();
                    }

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
