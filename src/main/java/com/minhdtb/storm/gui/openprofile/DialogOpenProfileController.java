package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractController;
import org.springframework.stereotype.Component;

@Component
public class DialogOpenProfileController extends AbstractController {

    public void actionOK() {

    }

    public void actionCancel() {
        this.close();
    }
}
