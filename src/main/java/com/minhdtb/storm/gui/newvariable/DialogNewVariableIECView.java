package com.minhdtb.storm.gui.newvariable;

import com.minhdtb.storm.base.AbstractView;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class DialogNewVariableIECView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("NewVariableIEC.fxml");
    }

    public void showDialog(AbstractView owner) {
        this.setTitle("New IEC 60870 Variable")
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(owner)
                .setApplication(owner.getApplication())
                .show();
    }
}
