package com.minhdtb.storm.core.data;

import com.minhdtb.storm.entities.Channel;

public class StormChannelIEC extends StormChannel {

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

    StormChannelIEC() {
        super();
    }

    StormChannelIEC(Channel channel) {
        super(channel);
    }
}
