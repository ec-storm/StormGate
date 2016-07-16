package com.minhdtb.storm.gui.newvariableiec;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.StormVariableIEC;
import com.minhdtb.storm.core.lib.j60870.TypeId;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;

import static com.minhdtb.storm.common.GlobalConstants.*;

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

    private final DataManager dataManager;

    private ResourceBundle resources;

    @Autowired
    public DialogNewVariableIECController(DataManager dataManager) {
        Assert.notNull(dataManager, "DataManager must not be null");
        this.dataManager = dataManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        comboBoxVariableType.getItems().addAll(
                new NamedValueType(resources.getString(KEY_T09), TypeId.M_ME_NA_1.getId()),
                new NamedValueType(resources.getString(KEY_T13), TypeId.M_ME_NC_1.getId()),
                new NamedValueType(resources.getString(KEY_T45), TypeId.C_SC_NA_1.getId()),
                new NamedValueType(resources.getString(KEY_T46), TypeId.C_DC_NA_1.getId()));

        editSectorAddress.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));
        editInformationObjectAddress.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));
    }

    @Override
    public void onShow(WindowEvent event) {
        editVariableName.setText(resources.getString(KEY_NEW_VARIABLE));
        editSectorAddress.setText("3");
        editInformationObjectAddress.setText("1");
        comboBoxVariableType.getSelectionModel().selectFirst();
    }

    public void actionOK() {
        NamedValueType variableType = comboBoxVariableType.getValue();
        StormVariableIEC variableIEC = new StormVariableIEC();
        variableIEC.setName(editVariableName.getText());
        variableIEC.setSectorAddress(Integer.parseInt(editSectorAddress.getText()));
        variableIEC.setInformationObjectAddress(Integer.parseInt(editInformationObjectAddress.getText()));
        variableIEC.setDataType(variableType.getValue());

        Channel channel = ((DialogNewVariableIECView) getView()).getChannel();

        if (!dataManager.existVariable(channel, variableIEC.getRaw())) {
            getPublisher().publish("application:addVariable", variableIEC.getRaw());
            close();
        } else {
            Utils.showError(getView(), String.format(resources.getString(KEY_ERROR_VARIABLE_EXISTS), variableIEC.getName()));
        }
    }

    public void actionCancel() {
        close();
    }
}
