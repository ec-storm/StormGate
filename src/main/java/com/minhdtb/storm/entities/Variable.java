package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;

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
}
