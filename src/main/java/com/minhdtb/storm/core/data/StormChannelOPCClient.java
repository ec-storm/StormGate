package com.minhdtb.storm.core.data;

import com.minhdtb.storm.entities.Channel;

public class StormChannelOPCClient extends StormChannelOPC {

    public StormChannelOPCClient() {
        super();
        getRaw().setType(Channel.ChannelType.CT_OPC_CLIENT);
    }

    public StormChannelOPCClient(Channel channel) {
        super(channel);
    }
}
