package com.minhdtb.storm.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"attributes"})
public class Variable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @OneToMany(mappedBy = "variable", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariableAttribute> attributes;
}
