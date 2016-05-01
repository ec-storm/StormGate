package com.minhdtb.storm.gui.newvariable;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.StormVariableIEC;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class DialogNewVariableIECController extends AbstractController {

    @FXML
    public TextField editVariableName;
    @FXML
    public TextField editSectorAddress;
    @FXML
    public TextField editInformationObjectAddress;
    @FXML
    public ComboBox<NamedValueType> comboBoxVariableType;

    @Autowired
    private DataManager dataManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBoxVariableType.getItems().addAll(
                new NamedValueType("T9", 0),
                new NamedValueType("T13", 1),
                new NamedValueType("T45", 2),
                new NamedValueType("T46", 3));

        editSectorAddress.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));
        editInformationObjectAddress.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));
    }

    @Override
    protected void onShow(WindowEvent event) {
        editVariableName.setText("NewVariable");
        editSectorAddress.setText("3");
        editInformationObjectAddress.setText("1");
        comboBoxVariableType.getSelectionModel().selectFirst();
    }

    public void actionOK() {
        NamedValueType variableType = comboBoxVariableType.getValue();
        StormVariableIEC variableIEC = new StormVariableIEC();
        variableIEC.getVariable().setName(editVariableName.getText());
        variableIEC.setSectorAddress(Integer.parseInt(editSectorAddress.getText()));
        variableIEC.setInformationObjectAddress(Integer.parseInt(editInformationObjectAddress.getText()));
        variableIEC.setDataType(variableType.getValue());

        Channel channel = ((DialogNewVariableIECView) getView()).getChannel();

        if (!dataManager.existVariable(channel, variableIEC.getVariable())) {
            getPublisher().publish("application:addVariable", variableIEC.getVariable());
            close();
        } else {
            Utils.showError(getView(), String.format("Variable \"%s\" is already exists.", variableIEC.getVariable().getName()));
        }
    }

    public void actionCancel() {
        close();
    }
}
