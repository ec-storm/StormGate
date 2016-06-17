package com.minhdtb.storm.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"variables", "attributes"})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "profile_id", "name" }))
public class Channel implements Serializable {

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

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variable> variables;

    @OneToMany(mappedBy = "channel", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelAttribute> attributes;

    @PostLoad
    public void load() {
        if (type == null) {
            type = ChannelType.CT_OPC_CLIENT;
        }
    }

    public enum ChannelType {
        CT_IEC_SERVER, CT_IEC_CLIENT, CT_OPC_CLIENT;

        public static ChannelType fromInt(int i) {
            return ChannelType.values()[i];
        }
    }

    public List<Variable> getVariables() {
        if (variables == null) {
            variables = new ArrayList<>();
        }

        return variables;
    }

    public List<ChannelAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        return attributes;
    }
}
