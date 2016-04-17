package com.minhdtb.storm.entities;

import java.util.List;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

@Data
@ToString(exclude = {"channels"})
@Entity
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Channel> channels;

    public Profile(String name) {
        this.name = name;
    }
}
