package com.vhk.kirjad.jpa;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
@Table(name = "stuudium_opilased")
public class Student {
    @Id
    private String id;

    private String eesnimi;

    private String perekonnanimi;

    private String klass;

    private String email;

    @OneToOne(mappedBy = "student")
    private Kiitus kiitus;

    @OneToMany(mappedBy = "student")
    private Collection<Parent> parents;
}
