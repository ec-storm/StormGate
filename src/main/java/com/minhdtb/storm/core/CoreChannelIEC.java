package com.minhdtb.storm.core;

import com.minhdtb.storm.entities.Channel;

public class CoreChannelIEC extends CoreChannel {

    public String getHost() {
        return getAttribute("host");
    }

    public void setHost(String host) {
        setAttribute("host", host);
    }

    public int getPort() {
        return Integer.parseInt(getAttribute("port"));
    }

    public void setPort(int port) {
        setAttribute("port", String.valueOf(port));
    }
}
