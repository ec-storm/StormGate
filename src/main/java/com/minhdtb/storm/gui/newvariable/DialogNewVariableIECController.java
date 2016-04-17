package com.minhdtb.storm.gui.newvariable;


import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editSectorAddress.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));
        editInformationObjectAddress.addEventFilter(KeyEvent.KEY_TYPED, Utils.numericValidation(5));
    }

    @Override
    protected void onShow(WindowEvent event) {
        editVariableName.setText("NewVariable");
        editSectorAddress.setText("3");
        editInformationObjectAddress.setText("1");

    }

    public void actionOK() {

    }

    public void actionCancel() {
        close();
    }
}
