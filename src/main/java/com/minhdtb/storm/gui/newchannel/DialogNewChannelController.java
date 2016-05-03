package com.minhdtb.storm.gui.newchannel;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.StormChannelIECClient;
import com.minhdtb.storm.core.data.StormChannelIECServer;
import com.minhdtb.storm.core.data.StormChannelOPCClient;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    @Autowired
    private DataManager dataManager;

    @Override
    public void onShow(WindowEvent event) {
        editChannelName.setText("New Channel");
        comboBoxChannelType.getSelectionModel().selectFirst();
    }

    private void loadFxml(String fxml) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml + ".fxml"));
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
        switch (type.getValue()) {
            case 0: {
                loadFxml("NewChannelIECServer");

                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");
                editPort.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));

                editHost.setText("127.0.0.1");
                editPort.setText("2405");

                break;
            }
            case 1: {
                loadFxml("NewChannelIECClient");

                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");
                editPort.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));

                editHost.setText("127.0.0.1");
                editPort.setText("2404");

                break;
            }
            case 2: {
                loadFxml("NewChannelOPCClient");

                TextField editProgId = (TextField) getView().getScene().lookup("#editProgId");
                TextField editRefreshRate = (TextField) getView().getScene().lookup("#editRefreshRate");
                editRefreshRate.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));

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
        comboBoxChannelType.getItems().addAll(
                new NamedValueType("IEC 60870 Server", 0),
                new NamedValueType("IEC 60870 Client", 1),
                new NamedValueType("OPC Client", 2));

        comboBoxChannelType.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadChannelAttribute(newValue);
        });
    }

    public void actionOK() {
        NamedValueType channelType = comboBoxChannelType.getValue();

        switch (channelType.getValue()) {
            case 0: {
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
                    Utils.showError(getView(), String.format("Channel \"%s\" is already exists.", stormChannelIECServer.getName()));
                }

                break;
            }
            case 1: {
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
                    Utils.showError(getView(), String.format("Channel \"%s\" is already exists.", stormChannelIECClient.getName()));
                }

                break;
            }
            case 2: {
                TextField editProgId = (TextField) getView().getScene().lookup("#editProgId");
                TextField editRefreshRate = (TextField) getView().getScene().lookup("#editRefreshRate");

                StormChannelOPCClient stormChannelOPCClient = new StormChannelOPCClient();
                stormChannelOPCClient.setName(editChannelName.getText());
                stormChannelOPCClient.setDescription(editChannelDescription.getText());

                stormChannelOPCClient.setProgId(editProgId.getText());
                stormChannelOPCClient.setRefreshRate(Integer.parseInt(editRefreshRate.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelOPCClient.getRaw())) {
                    getPublisher().publish("application:addChannel", stormChannelOPCClient.getRaw());
                    close();
                } else {
                    Utils.showError(getView(), String.format("Channel \"%s\" is already exists.", stormChannelOPCClient.getName()));
                }

                break;
            }
        }
    }

    public void actionCancel() {
        this.close();
    }
}
