package com.minhdtb.storm.gui.newchannel;

import com.google.common.base.Strings;
import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.StormChannelIECClient;
import com.minhdtb.storm.core.data.StormChannelIECServer;
import com.minhdtb.storm.core.data.StormChannelOPCClient;
import com.minhdtb.storm.gui.listopcserver.DialogListOpcServerView;
import com.minhdtb.storm.services.DataManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.minhdtb.storm.common.GlobalConstants.*;
import static com.minhdtb.storm.entities.Channel.ChannelType;
import static com.minhdtb.storm.entities.Channel.ChannelType.*;
import static java.util.ResourceBundle.getBundle;

@Controller
public class DialogNewChannelController extends AbstractController {

    @FXML
    private TextField editChannelName;
    @FXML
    private TextField editChannelDescription;
    @FXML
    private ComboBox<NamedValueType> comboBoxChannelType;
    @FXML
    private AnchorPane paneAttribute;

    private final DataManager dataManager;

    private final DialogListOpcServerView dialogListOpcServerView;
    private ResourceBundle resources;

    @Autowired
    public DialogNewChannelController(DialogListOpcServerView dialogListOpcServerView, DataManager dataManager) {
        Assert.notNull(dataManager, "DataManager must not be null");

        this.dataManager = dataManager;
        this.dialogListOpcServerView = dialogListOpcServerView;
    }

    @Override
    public void onShow(WindowEvent event) {
        editChannelName.setText(resources.getString(KEY_NEW_CHANNEL));
        editChannelDescription.setText("");
        comboBoxChannelType.getSelectionModel().selectFirst();

        getSubscriber().on("opc:progId", item -> Platform.runLater(() -> {
            TextField editProgId = (TextField) getView().getScene().lookup("#editProgId");
            editProgId.setText((String) item);
        }));
    }

    private void loadFxml(String fxml) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml + ".fxml"));
        loader.setResources(getBundle(BUNDLE_NAME));
        try {
            GridPane pane = loader.load();
            AnchorPane.setTopAnchor(pane, 5.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
            paneAttribute.getChildren().setAll(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadChannelAttribute(NamedValueType type) {
        switch (ChannelType.fromInt(type.getValue())) {
            case CT_IEC_SERVER: {
                loadFxml("NewChannelIECServer");

                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");
                editPort.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));

                editHost.setText("127.0.0.1");
                editPort.setText("2405");

                break;
            }
            case CT_IEC_CLIENT: {
                loadFxml("NewChannelIECClient");

                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");
                editPort.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));

                editHost.setText("127.0.0.1");
                editPort.setText("2404");

                break;
            }
            case CT_OPC_CLIENT: {
                loadFxml("NewChannelOPCClient");

                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editProgId = (TextField) getView().getScene().lookup("#editProgId");
                TextField editRefreshRate = (TextField) getView().getScene().lookup("#editRefreshRate");
                Button buttonListServer = (Button) getView().getScene().lookup("#buttonListServer");

                if (buttonListServer.getOnAction() == null) {
                    buttonListServer.setOnAction(event -> dialogListOpcServerView.showDialog(getView()));
                }

                editRefreshRate.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));

                editHost.setText("localhost");
                editProgId.setText("");
                editRefreshRate.setText("1000");

                break;
            }
            default: {
                paneAttribute.getChildren().clear();
                break;
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        comboBoxChannelType.getItems().addAll(
                new NamedValueType(resources.getString(KEY_IEC_60870_SERVER), CT_IEC_SERVER.ordinal()),
                new NamedValueType(resources.getString(KEY_IEC_60870_CLIENT), CT_IEC_CLIENT.ordinal()),
                new NamedValueType(resources.getString(KEY_OPC_CLIENT), CT_OPC_CLIENT.ordinal()));

        comboBoxChannelType.valueProperty().addListener((observable, oldValue, newValue) -> loadChannelAttribute(newValue));
    }

    public void actionOK() {
        NamedValueType channelType = comboBoxChannelType.getValue();

        switch (ChannelType.fromInt(channelType.getValue())) {
            case CT_IEC_SERVER: {
                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");

                StormChannelIECServer stormChannelIECServer = new StormChannelIECServer();
                stormChannelIECServer.setName(editChannelName.getText());
                stormChannelIECServer.setDescription(editChannelDescription.getText());

                stormChannelIECServer.setHost(editHost.getText());
                stormChannelIECServer.setPort(Integer.parseInt(editPort.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelIECServer.getRaw())) {
                    getPublisher().publish("application:addChannel", stormChannelIECServer.getRaw());
                    close();
                } else {
                    Utils.showError(getView(), String.format(resources.getString(KEY_ERROR_CHANNEL_EXISTS), stormChannelIECServer.getName()));
                }

                break;
            }
            case CT_IEC_CLIENT: {
                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");

                StormChannelIECClient stormChannelIECClient = new StormChannelIECClient();
                stormChannelIECClient.setName(editChannelName.getText());
                stormChannelIECClient.setDescription(editChannelDescription.getText());

                stormChannelIECClient.setHost(editHost.getText());
                stormChannelIECClient.setPort(Integer.parseInt(editPort.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelIECClient.getRaw())) {
                    getPublisher().publish("application:addChannel", stormChannelIECClient.getRaw());
                    close();
                } else {
                    Utils.showError(getView(), String.format(resources.getString(KEY_ERROR_CHANNEL_EXISTS), stormChannelIECClient.getName()));
                }

                break;
            }
            case CT_OPC_CLIENT: {
                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editProgId = (TextField) getView().getScene().lookup("#editProgId");
                TextField editRefreshRate = (TextField) getView().getScene().lookup("#editRefreshRate");

                if (Strings.isNullOrEmpty(editProgId.getText())) {
                    Utils.showError(getView(), resources.getString(KEY_ERROR_PROGID_MUST_NOT_BE_EMPTY));
                    return;
                }

                StormChannelOPCClient stormChannelOPCClient = new StormChannelOPCClient();
                stormChannelOPCClient.setName(editChannelName.getText());
                stormChannelOPCClient.setDescription(editChannelDescription.getText());
                stormChannelOPCClient.setHost(editHost.getText());
                stormChannelOPCClient.setProgId(editProgId.getText());
                stormChannelOPCClient.setRefreshRate(Integer.parseInt(editRefreshRate.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelOPCClient.getRaw())) {
                    getPublisher().publish("application:addChannel", stormChannelOPCClient.getRaw());
                    close();
                } else {
                    Utils.showError(getView(), String.format(resources.getString(KEY_ERROR_CHANNEL_EXISTS), stormChannelOPCClient.getName()));
                }

                break;
            }
        }
    }

    public void actionCancel() {
        this.close();
    }
}
