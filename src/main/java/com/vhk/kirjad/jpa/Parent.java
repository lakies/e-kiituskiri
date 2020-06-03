package com.vhk.kirjad.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "stuudium_vanemad")
@Data
public class Parent {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "lapse_id")
    @ToString.Exclude
    @JsonIgnore
    private Student student;

    private String eesnimi;
    private String perekonnanimi;
    private String email;
}
