package com.minhdtb.storm.gui.listopcserver;

import com.minhdtb.storm.base.AbstractView;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogListOpcServerView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("ListOpcServer.fxml");
    }

    public void showDialog(AbstractView owner) {
        this.setTitle("Select OPC Server")
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(owner)
                .setApplication(owner.getApplication())
                .show();
    }
}
