package com.minhdtb.storm.core.data;


public class CoreChannelOPC extends CoreChannel {

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
}
