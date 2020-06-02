package com.vhk.kirjad.jpa;

import org.springframework.data.repository.CrudRepository;

public interface KiitusRepository extends CrudRepository<Kiitus, Integer> {
    Kiitus findByStudent(Student student);
}
