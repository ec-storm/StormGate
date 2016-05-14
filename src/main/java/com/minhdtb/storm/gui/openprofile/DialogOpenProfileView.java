package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractView;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogOpenProfileView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("OpenProfile.fxml");
    }

    public void showDialog(AbstractView owner, String title) {
        this.setTitle(title)
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(owner)
                .setApplication(owner.getApplication())
                .show();
    }
}
