package com.minhdtb.storm.core.engine;

import com.minhdtb.storm.core.data.*;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StormEngine {

    @Autowired
    private DataManager dataManager;

    private List<IStormChannel> channelList = new ArrayList<>();

    public void run() {

        channelList.clear();

        Profile profile = dataManager.getCurrentProfile();

        for (Channel channel : profile.getChannels()) {
            switch (channel.getType()) {
                case CT_IEC_CLIENT: {
                    StormChannelIECClient stormChannelIECClient = new StormChannelIECClient(channel);
                    channelList.add(stormChannelIECClient);

                    break;
                }
                case CT_IEC_SERVER: {
                    StormChannelIECServer stormChannelIECServer = new StormChannelIECServer(channel);
                    channelList.add(stormChannelIECServer);

                    break;
                }
                case CT_OPC_CLIENT: {
                    StormChannelOPCClient stormChannelOPCClient = new StormChannelOPCClient(channel);
                    channelList.add(stormChannelOPCClient);

                    break;
                }
            }
        }

        channelList.stream().forEach(IStormChannel::start);
    }
}
