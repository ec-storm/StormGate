package com.minhdtb.storm.gui.newprofile;

import com.minhdtb.storm.base.AbstractView;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogNewProfileView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("NewProfile.fxml");
    }
}
