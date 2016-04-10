package com.minhdtb.storm.core;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.ChannelAttribute;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Data
public class GateChannel {

    private Channel channel;

    protected String getAttribute(String name) {
        for (ChannelAttribute attribute : this.getChannel().getAttributes()) {
            System.out.println(attribute.getName());
            if (Objects.equals(attribute.getName(), name)) {
                return attribute.getValue();
            }
        }

        return null;
    }

    protected void setAttribute(String name, String value) {
        boolean found = false;
        if (this.getChannel().getAttributes() != null) {
            for (ChannelAttribute attribute : this.getChannel().getAttributes()) {
                if (Objects.equals(attribute.getName(), name)) {
                    found = true;
                    attribute.setValue(value);
                    break;
                }
            }
        } else {
            this.getChannel().setAttributes(new ArrayList<>());
        }

        if (!found) {
            ChannelAttribute attribute = new ChannelAttribute();
            attribute.setChannel(this.getChannel());
            attribute.setName(name);
            attribute.setValue(value);

            this.getChannel().getAttributes().add(attribute);
        }
    }

    public GateChannel() {
        this.channel = new Channel();
    }

    public GateChannel(Channel channel) {
        this.channel = channel;
    }

    public Long getId() {
        Long id = this.channel.getId();
        if (id == null) {
            id = (new Date()).getTime();
        }

        return id;
    }
}
