package com.vhk.kirjad.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "e_kiitus")
public class Kiitus implements Serializable {

    @Id
    private String lapse_id;

    @OneToOne
    @JoinColumn(name = "lapse_id")
    @MapsId
    @JsonIgnore
    private Student student;

    private String kiituskiri;

    private boolean saadetud;

    private String saatja;

    @Temporal(value = TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date aeg;

}
