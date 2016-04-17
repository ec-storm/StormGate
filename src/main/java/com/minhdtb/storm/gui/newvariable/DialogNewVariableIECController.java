package com.minhdtb.storm.gui.newvariable;


import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Publisher;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.CoreVariableIEC;
import com.minhdtb.storm.entities.Variable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
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
    private Publisher<Variable> publisher;

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
        CoreVariableIEC variableIEC = new CoreVariableIEC();
        variableIEC.getVariable().setName(editVariableName.getText());
        variableIEC.setSectorAddress(Integer.parseInt(editSectorAddress.getText()));
        variableIEC.setInformationObjectAddress(Integer.parseInt(editInformationObjectAddress.getText()));
        variableIEC.setDataType(variableType.getValue());

        publisher.publish("application:addVariable", variableIEC.getVariable());

        close();
    }

    public void actionCancel() {
        close();
    }
}
