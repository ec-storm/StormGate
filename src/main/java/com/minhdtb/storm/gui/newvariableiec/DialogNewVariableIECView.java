package com.minhdtb.storm.gui.newvariableiec;

import com.minhdtb.storm.base.AbstractView;
import com.minhdtb.storm.entities.Channel;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogNewVariableIECView extends AbstractView {

    private Channel channel;

    @PostConstruct
    public void init() {
        setFxml("NewVariableIEC.fxml");
    }

    public void showDialog(AbstractView owner, String title) {
        this.setTitle(title)
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(owner)
                .setApplication(owner.getApplication())
                .show();
    }

    public Channel getChannel() {
        return channel;
    }

    public DialogNewVariableIECView setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }
}
