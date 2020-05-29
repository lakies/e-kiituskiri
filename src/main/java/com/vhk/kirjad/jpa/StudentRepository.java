package com.vhk.kirjad.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface StudentRepository extends CrudRepository<Student, String> {

    Collection<Student> findAllByKlass(String klass);

}
