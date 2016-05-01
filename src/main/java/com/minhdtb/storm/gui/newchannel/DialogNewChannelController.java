package com.minhdtb.storm.gui.newchannel;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.*;
import com.minhdtb.storm.entities.Channel;
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
    protected void onShow(WindowEvent event) {
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
                stormChannelIECServer.getChannel().setName(editChannelName.getText());
                stormChannelIECServer.getChannel().setDescription(editChannelDescription.getText());
                stormChannelIECServer.getChannel().setType(Channel.ChannelType.fromInt(channelType.getValue()));

                stormChannelIECServer.setHost(editHost.getText());
                stormChannelIECServer.setPort(Integer.parseInt(editPort.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelIECServer.getChannel())) {
                    getPublisher().publish("application:addChannel", stormChannelIECServer.getChannel());
                    close();
                } else {
                    Utils.showError(getView(), String.format("Channel \"%s\" is already exists.", stormChannelIECServer.getChannel().getName()));
                }

                break;
            }
            case 1: {
                TextField editHost = (TextField) getView().getScene().lookup("#editHost");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");

                StormChannelIECClient stormChannelIECClient = new StormChannelIECClient();
                stormChannelIECClient.getChannel().setName(editChannelName.getText());
                stormChannelIECClient.getChannel().setDescription(editChannelDescription.getText());
                stormChannelIECClient.getChannel().setType(Channel.ChannelType.fromInt(channelType.getValue()));

                stormChannelIECClient.setHost(editHost.getText());
                stormChannelIECClient.setPort(Integer.parseInt(editPort.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelIECClient.getChannel())) {
                    getPublisher().publish("application:addChannel", stormChannelIECClient.getChannel());
                    close();
                } else {
                    Utils.showError(getView(), String.format("Channel \"%s\" is already exists.", stormChannelIECClient.getChannel().getName()));
                }

                break;
            }
            case 2: {
                TextField editProgId = (TextField) getView().getScene().lookup("#editProgId");
                TextField editRefreshRate = (TextField) getView().getScene().lookup("#editRefreshRate");

                StormChannelOPCClient stormChannelOPCClient = new StormChannelOPCClient();
                stormChannelOPCClient.getChannel().setName(editChannelName.getText());
                stormChannelOPCClient.getChannel().setDescription(editChannelDescription.getText());
                stormChannelOPCClient.getChannel().setType(Channel.ChannelType.fromInt(channelType.getValue()));

                stormChannelOPCClient.setProgId(editProgId.getText());
                stormChannelOPCClient.setRefreshRate(Integer.parseInt(editRefreshRate.getText()));

                if (!dataManager.existChannel(dataManager.getCurrentProfile(), stormChannelOPCClient.getChannel())) {
                    getPublisher().publish("application:addChannel", stormChannelOPCClient.getChannel());
                    close();
                } else {
                    Utils.showError(getView(), String.format("Channel \"%s\" is already exists.", stormChannelOPCClient.getChannel().getName()));
                }

                break;
            }
        }
    }

    public void actionCancel() {
        this.close();
    }
}
