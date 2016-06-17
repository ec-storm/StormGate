package com.minhdtb.storm.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"channels"})
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class Profile implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Lob
    private byte[] script;

    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Channel> channels;

    public Profile(String name) {
        this.name = name;
    }

    public List<Channel> getChannels() {
        if (channels == null) {
            channels = new ArrayList<>();
        }

        return channels;
    }
}
