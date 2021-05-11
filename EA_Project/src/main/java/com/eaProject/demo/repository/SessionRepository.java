package com.eaProject.demo.repository;

import com.eaProject.demo.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Session;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository  extends JpaRepository<Session, Long> {
    List<Session> findByProvider(Person provider);
    Optional<Session> findTopByIdAndProvider(Long id, Person provider);
}
