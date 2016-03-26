package com.minhdtb.storm.gui.application;

import com.minhdtb.storm.base.AbstractView;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApplicationView extends AbstractView {

    @PostConstruct
    public void init() {
        setFxml("Application.fxml");
    }
}
