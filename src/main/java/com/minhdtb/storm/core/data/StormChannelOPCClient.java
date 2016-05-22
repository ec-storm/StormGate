package com.minhdtb.storm.core.data;

import com.minhdtb.storm.core.lib.opcda.IOPCDaEvent;
import com.minhdtb.storm.core.lib.opcda.OPCDaClient;
import com.minhdtb.storm.entities.Channel;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class StormChannelOPCClient extends StormChannelOPC implements IOPCDaEvent {

    private OPCDaClient opcDaClient = new OPCDaClient();

    public StormChannelOPCClient() {
        super();
        getRaw().setType(Channel.ChannelType.CT_OPC_CLIENT);
    }

    public StormChannelOPCClient(Channel channel) {
        super(channel);
    }

    @Override
    public void write(String tagName, Object value) {
        opcDaClient.write(tagName, value);
    }

    @Override
    public void start() {
        opcDaClient.setEventHandler(this);
        opcDaClient.connect(getProgId());

        getVariables().stream().forEach(variable -> opcDaClient.add(((StormVariableOPC) variable).getTagName()));
    }

    @Override
    public void stop() {
        opcDaClient.disconnect();
    }

    @Override
    public void onChange(String tagName, Object value, Date time, int quality) {
        Optional<IStormVariable> found = getVariables().stream().filter(variable -> variable instanceof StormVariableOPC &&
                Objects.equals(((StormVariableOPC) variable).getTagName(), tagName)).findFirst();
        if (found.isPresent()) {
            if (value != null) {
                found.get().setValue(value);
            }
        }
    }
}
