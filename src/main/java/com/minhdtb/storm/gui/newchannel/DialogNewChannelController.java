package com.minhdtb.storm.gui.newchannel;

import com.minhdtb.storm.base.AbstractController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DialogNewChannelController extends AbstractController {

    @FXML
    private TextField editChannelName;
    @FXML
    private TextField editChannelDescription;
    @FXML
    private ComboBox<ChannelType> comboBoxChannelType;
    @FXML
    private AnchorPane paneAttribute;

    @Override
    protected void onShow(WindowEvent event) {

    }

    private void loadChannelAttribute(ChannelType type) {
        switch (type.getValue()) {
            case 0: {

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
                new ChannelType("IEC 60870 Server", 0),
                new ChannelType("IEC 60870 Client", 1),
                new ChannelType("OPC Server", 2),
                new ChannelType("OPC Client", 3));

        comboBoxChannelType.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadChannelAttribute(newValue);
        });

        comboBoxChannelType.getSelectionModel().selectFirst();
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    private final class ChannelType {
        private String name;
        private int value;

        @Override
        public String toString() {
            return getName();
        }
    }
}
