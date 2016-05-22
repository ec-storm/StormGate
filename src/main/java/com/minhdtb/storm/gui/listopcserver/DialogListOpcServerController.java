package com.minhdtb.storm.gui.listopcserver;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.core.lib.opcda.OPCDaClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.springframework.stereotype.Controller;

@Controller
public class DialogListOpcServerController extends AbstractController {

    @FXML
    public TextField editHost;
    @FXML
    public Button buttonRefresh;
    @FXML
    public ListView<String> listServer;

    private OPCDaClient OPCDaClient = new OPCDaClient();

    @Override
    public void onShow(WindowEvent event) {
        editHost.setText("localhost");
        buttonRefresh.setOnAction(actionEvent -> {
            OPCDaClient.setHost(editHost.getText());

            listServer.getItems().clear();
            listServer.getItems().addAll(OPCDaClient.getAllServers());
        });
        buttonRefresh.getOnAction().handle(null);

        listServer.setOnMouseClicked(actionEvent -> {
            if (actionEvent.getClickCount() == 2 && (listServer.getSelectionModel().getSelectedItem() != null)) {
                actionOK();
            }
        });
    }

    public void actionOK() {
        String selectedProgId = listServer.getSelectionModel().getSelectedItem();
        getPublisher().publish("opc:progId", selectedProgId);
        close();
    }

    public void actionCancel() {
        close();
    }
}
