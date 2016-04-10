package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Variable> variables;

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ChannelAttribute> attributes;

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
