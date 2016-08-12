package com.minhdtb.storm.gui.newvariableopc;

import com.google.common.base.Strings;
import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.NamedValueType;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.StormChannelOPCClient;
import com.minhdtb.storm.core.data.StormVariableOPC;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.gui.listopctag.DialogListOpcTagView;
import com.minhdtb.storm.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import static com.minhdtb.storm.gui.newvariableopc.DialogNewVariableOPCController.OPCDataType.*;

@Controller
public class DialogNewVariableOPCController extends AbstractController {

    @FXML
    public TextField editVariableName;
    @FXML
    public TextField editTagName;
    @FXML
    public ComboBox<NamedValueType> comboBoxVariableType;
    @FXML
    public Button buttonListTags;

    private final DataManager dataManager;

    private final DialogListOpcTagView dialogListOpcTagView;

    public enum OPCDataType {
        DT_BOOLEAN, DT_INTEGER, DT_FLOAT, DT_STRING;

        public static OPCDataType fromInt(int i) {
            return OPCDataType.values()[i];
        }
    }

    @Autowired
    public DialogNewVariableOPCController(DialogListOpcTagView dialogListOpcTagView, DataManager dataManager) {
        Assert.notNull(dataManager, "DataManager must not be null");

        this.dataManager = dataManager;
        this.dialogListOpcTagView = dialogListOpcTagView;
    }

    @Override
    public void onShow(WindowEvent event) {
        buttonListTags.setOnAction(eventMouse -> {
            Channel channel = ((DialogNewVariableOPCView) getView()).getChannel();
            if (channel != null) {
                StormChannelOPCClient stormChannelOPCClient = new StormChannelOPCClient(channel);
                dialogListOpcTagView.setHost(stormChannelOPCClient.getHost());
                dialogListOpcTagView.setProgId(stormChannelOPCClient.getProgId());
                dialogListOpcTagView.showDialog(getView());
            }
        });

        getSubscriber().on("opc:select", object -> editTagName.setText((String) object));

        editVariableName.setText(getResourceString("newVariable"));
        comboBoxVariableType.getSelectionModel().selectFirst();
        editTagName.setText("");
    }

    @Override
    public void onCreate() {
        comboBoxVariableType.getItems().addAll(
                new NamedValueType(getResourceString("DT_BOOLEAN"), DT_BOOLEAN.ordinal()),
                new NamedValueType(getResourceString("DT_INTEGER"), DT_INTEGER.ordinal()),
                new NamedValueType(getResourceString("DT_FLOAT"), DT_FLOAT.ordinal()),
                new NamedValueType(getResourceString("DT_STRING"), DT_STRING.ordinal()));
    }

    public void actionOK() {
        if (Strings.isNullOrEmpty(editVariableName.getText())) {
            Utils.showError(getView(), getResourceString("MSG001"));
            return;
        }

        if (Strings.isNullOrEmpty(editTagName.getText())) {
            Utils.showError(getView(), getResourceString("MSG002"));
            return;
        }

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
            Utils.showError(getView(), String.format(getResourceString("MSG003"), variableOPC.getName()));
        }
    }

    public void actionCancel() {
        close();
    }
}
