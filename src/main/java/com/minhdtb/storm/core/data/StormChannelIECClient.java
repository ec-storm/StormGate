package com.minhdtb.storm.core.data;

import com.minhdtb.storm.core.lib.j60870.ASdu;
import com.minhdtb.storm.entities.Channel;

public class StormChannelIECClient extends StormChannelIEC {

    public StormChannelIECClient() {
        super();
        getRaw().setType(Channel.ChannelType.CT_IEC_CLIENT);
    }

    public StormChannelIECClient(Channel channel) {
        super(channel);
    }

    @Override
    public void send(ASdu aSdu) {
        
    }

    @Override
    public void start() {

    }
}
