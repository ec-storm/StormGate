package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Variable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(mappedBy = "variable", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<VariableAttribute> attributes;
}
