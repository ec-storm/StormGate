package com.minhdtb.storm.gui.listopctag;

import com.minhdtb.storm.base.AbstractView;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogListOpcTagView extends AbstractView {

    private String host;

    private String progId;

    @PostConstruct
    public void init() {
        setFxml("ListOpcTag.fxml");
    }

    public void showDialog(AbstractView owner) {
        this.setTitle("Select OPC Tag")
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(owner)
                .setApplication(owner.getApplication())
                .show(true);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProgId() {
        return progId;
    }

    public void setProgId(String progId) {
        this.progId = progId;
    }
}
