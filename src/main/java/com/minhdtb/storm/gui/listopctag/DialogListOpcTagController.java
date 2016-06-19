package com.minhdtb.storm.gui.listopctag;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.core.lib.opcda.OPCDaClient;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.stage.WindowEvent;
import org.springframework.stereotype.Controller;

@Controller
public class DialogListOpcTagController extends AbstractController {

    @FXML
    public TreeView treeTags;

    private OPCDaClient opcDaClient = new OPCDaClient();

    @Override
    public void onShow(WindowEvent event) {
        DialogListOpcTagView view = (DialogListOpcTagView) getView();
        opcDaClient.setHost(view.getHost());
        opcDaClient.connect(view.getProgId());
    }

    public void actionOK() {
        close();
    }

    public void actionCancel() {
        close();
    }
}
