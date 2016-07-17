package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class ChannelAttribute implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String value;

    private String type;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;
}
