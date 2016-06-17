package com.minhdtb.storm.gui.newvariableopc;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.StormVariableOPC;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

import static com.minhdtb.storm.common.GlobalConstants.*;

@Controller
public class DialogNewVariableOPCController extends AbstractController {

    @FXML
    public TextField editVariableName;
    @FXML
    public TextField editTagName;
    @FXML
    public ComboBox<NamedValueType> comboBoxVariableType;

    @Autowired
    private DataManager dataManager;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        comboBoxVariableType.getItems().addAll(
                new NamedValueType(resources.getString(KEY_BOOLEAN), 0),
                new NamedValueType(resources.getString(KEY_INTEGER), 1),
                new NamedValueType(resources.getString(KEY_FLOAT), 2),
                new NamedValueType(resources.getString(KEY_STRING), 3));

    }

    @Override
    public void onShow(WindowEvent event) {
        editVariableName.setText(resources.getString(KEY_NEW_VARIABLE));
        editTagName.setText("");
        comboBoxVariableType.getSelectionModel().selectFirst();
    }

    public void actionOK() {
        NamedValueType variableType = comboBoxVariableType.getValue();
        StormVariableOPC variableOPC = new StormVariableOPC();
        variableOPC.setName(editVariableName.getText());
        variableOPC.setTagName(editTagName.getText());
        variableOPC.setDataType(variableType.getValue());

        Channel channel = ((DialogNewVariableOPCView) getView()).getChannel();

        if (!dataManager.existVariable(channel, variableOPC.getRaw())) {
            getPublisher().publish("application:addVariable", variableOPC.getRaw());
            close();
        } else {
            Utils.showError(getView(), String.format(resources.getString(KEY_ERROR_VARIABLE_EXISTS), variableOPC.getName()));
        }
    }

    public void actionCancel() {
        close();
    }
}
