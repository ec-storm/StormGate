package com.minhdtb.storm.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"attributes"})
public class Variable implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(mappedBy = "variable", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariableAttribute> attributes;

    public List<VariableAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        return attributes;
    }
}
