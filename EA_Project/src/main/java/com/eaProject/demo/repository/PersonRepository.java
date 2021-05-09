package com.eaProject.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Person;

@Repository
public interface PersonRepository  extends JpaRepository<Person, Long> {

}
