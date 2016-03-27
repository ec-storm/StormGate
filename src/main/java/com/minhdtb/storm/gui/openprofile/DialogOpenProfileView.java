package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractView;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DialogOpenProfileView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("OpenProfile.fxml");
    }
}
