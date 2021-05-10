package com.eaProject.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Person;

import java.util.Optional;

@Repository
public interface PersonRepository  extends JpaRepository<Person, Long> {

    public Optional<Person> findTopByUsername(String username);
}
