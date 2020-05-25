package com.vhk.kirjad.jpa;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "e_kiitus")
public class Kiitus implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "lapse_id")
    private Student student;

    private String kiituskiri;

    private boolean saadetud;

}
