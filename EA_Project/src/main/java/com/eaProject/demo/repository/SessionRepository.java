package com.eaProject.demo.repository;

import com.eaProject.demo.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Session;

@Repository
public interface SessionRepository  extends JpaRepository<Session, Long> {
    Session findByProvider(Person provider);
}
