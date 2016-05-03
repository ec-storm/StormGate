package com.minhdtb.storm.core.data;


import com.minhdtb.storm.entities.Channel;

public abstract class StormChannelOPC extends StormChannel {

    public String getProgId() {
        return getAttribute("progId");
    }

    public void setProgId(String progId) {
        setAttribute("progId", progId);
    }

    public int getRefreshRate() {
        return Integer.parseInt(getAttribute("refreshRate"));
    }

    public void setRefreshRate(int refreshRate) {
        setAttribute("refreshRate", String.valueOf(refreshRate));
    }

    StormChannelOPC() {
        super();
    }

    StormChannelOPC(Channel channel) {
        super(channel);
    }
}
