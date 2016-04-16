package com.minhdtb.storm.gui.newchannel;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.Publisher;
import com.minhdtb.storm.core.CoreChannelIECServer;
import com.minhdtb.storm.entities.Channel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DialogNewChannelController extends AbstractController {

    @FXML
    private TextField editChannelName;
    @FXML
    private TextField editChannelDescription;
    @FXML
    private ComboBox<DisplayChannelType> comboBoxChannelType;
    @FXML
    private AnchorPane paneAttribute;

    @Autowired
    private Publisher<Channel> publisher;

    @Override
    protected void onShow(WindowEvent event) {
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

    private EventHandler<KeyEvent> numericValidation(final Integer maxLength) {
        return e -> {
            TextField textField = (TextField) e.getSource();
            if (textField.getText().length() >= maxLength) {
                e.consume();
            }

            if (e.getCharacter().matches("[0-9.]")) {
                if (textField.getText().contains(".") && e.getCharacter().matches("[.]")) {
                    e.consume();
                } else if (textField.getText().length() == 0 && e.getCharacter().matches("[.]")) {
                    e.consume();
                }
            } else {
                e.consume();
            }
        };
    }

    private void loadChannelAttribute(DisplayChannelType type) {
        switch (type.getValue()) {
            case 0: {
                loadFxml("NewChannelIECServer");

                TextField editBindIp = (TextField) getView().getScene().lookup("#editBindIp");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");
                editPort.addEventFilter(KeyEvent.KEY_TYPED, numericValidation(5));

                editBindIp.setText("127.0.0.1");
                editPort.setText("2404");

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
        editChannelName.setText("New Channel");

        comboBoxChannelType.getItems().addAll(
                new DisplayChannelType("IEC 60870 Server", 0),
                new DisplayChannelType("IEC 60870 Client", 1),
                new DisplayChannelType("OPC Server", 2),
                new DisplayChannelType("OPC Client", 3));

        comboBoxChannelType.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadChannelAttribute(newValue);
        });
    }

    public void actionOK() {
        DisplayChannelType channelType = comboBoxChannelType.getValue();

        switch (channelType.getValue()) {
            case 0: {
                TextField editBindIp = (TextField) getView().getScene().lookup("#editBindIp");
                TextField editPort = (TextField) getView().getScene().lookup("#editPort");

                CoreChannelIECServer iecServer = new CoreChannelIECServer();
                iecServer.getChannel().setName(editChannelName.getText());
                iecServer.getChannel().setDescription(editChannelDescription.getText());
                iecServer.getChannel().setType(Channel.ChannelType.fromInt(channelType.getValue()));

                iecServer.setHost(editBindIp.getText());
                iecServer.setPort(Integer.parseInt(editPort.getText()));

                publisher.publish("application:addChannel", iecServer.getChannel());

                break;
            }
        }

        this.close();
    }

    public void actionCancel() {
        this.close();
    }


    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    private final class DisplayChannelType {
        private String name;
        private int value;

        @Override
        public String toString() {
            return getName();
        }
    }
}
