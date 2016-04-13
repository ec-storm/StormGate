package com.minhdtb.storm.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class VariableAttribute {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String value;

    @ManyToOne
    @JoinColumn(name = "variable_id")
    private Variable variable;
}
