package com.minhdtb.storm.core.data;

import com.minhdtb.storm.core.lib.opcda.OPCDAManager;
import com.minhdtb.storm.entities.Channel;

public class StormChannelOPCClient extends StormChannelOPC {

    private OPCDAManager opcdaManager = new OPCDAManager();

    public StormChannelOPCClient() {
        super();
        getRaw().setType(Channel.ChannelType.CT_OPC_CLIENT);
    }

    public StormChannelOPCClient(Channel channel) {
        super(channel);
    }

    @Override
    public void start() {
        opcdaManager.connect(getProgId());
    }

    @Override
    public void stop() {

    }
}
