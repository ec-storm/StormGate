package com.minhdtb.storm.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;

import lombok.Data;

@Data
@Entity
public class Channel {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.ORDINAL)
    private ChannelType type;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @PostLoad
    public void load() {
        if (type == null) {
            type = ChannelType.CT_OPC_CLIENT;
        }
    }

    public enum ChannelType {
        CT_OPC_CLIENT, CT_OPC_SERVER, CT_IEC_CLIENT, CT_IEC_SERVER
    }
}
