package com.minhdtb.storm.core;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.ChannelAttribute;
import lombok.Data;

import java.util.Objects;

@Data
public class CoreChannel {

    protected Channel channel;

    protected String getAttribute(String name) {
        for (ChannelAttribute attribute : channel.getAttributes()) {
            if (Objects.equals(attribute.getName(), name)) {
                return attribute.getValue();
            }
        }

        return null;
    }

    protected void setAttribute(String name, String value) {
        boolean found = false;

        for (ChannelAttribute attribute : channel.getAttributes()) {
            if (Objects.equals(attribute.getName(), name)) {
                found = true;
                attribute.setValue(value);
                break;
            }
        }

        if (!found) {
            ChannelAttribute attribute = new ChannelAttribute();
            attribute.setChannel(channel);
            attribute.setName(name);
            attribute.setValue(value);

            channel.getAttributes().add(attribute);
        }
    }

    public Long getId() {
        return channel.getId();
    }

    protected CoreChannel() {
        channel = new Channel();
    }

    protected CoreChannel(Channel channelNew) {
        channel = channelNew;
    }
}
