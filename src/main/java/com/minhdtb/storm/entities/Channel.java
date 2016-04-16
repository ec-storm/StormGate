package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Variable> variables;

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ChannelAttribute> attributes;

    @PostLoad
    public void load() {
        if (type == null) {
            type = ChannelType.CT_OPC_CLIENT;
        }
    }

    public enum ChannelType {
        CT_OPC_CLIENT, CT_OPC_SERVER, CT_IEC_CLIENT, CT_IEC_SERVER;

        private static ChannelType[] values = null;

        public static ChannelType fromInt(int i) {
            if (ChannelType.values == null) {
                ChannelType.values = ChannelType.values();
            }
            return ChannelType.values[i];
        }
    }
}
