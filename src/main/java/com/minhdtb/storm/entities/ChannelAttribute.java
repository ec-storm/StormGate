package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ChannelAttribute {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String value;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;
}
