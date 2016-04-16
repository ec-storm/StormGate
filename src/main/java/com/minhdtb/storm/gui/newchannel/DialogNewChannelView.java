package com.minhdtb.storm.gui.newchannel;


import com.minhdtb.storm.base.AbstractView;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogNewChannelView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("NewChannel.fxml");
    }

    public void showDialog(AbstractView owner) {
        this.setTitle("New Channel")
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(owner)
                .setApplication(owner.getApplication())
                .show();
    }
}
