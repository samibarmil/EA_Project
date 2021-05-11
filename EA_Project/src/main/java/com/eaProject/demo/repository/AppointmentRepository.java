package com.eaProject.demo.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Appointment;

@Repository
@Transactional
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {
	 public List<Appointment> findAllByOrderByIdAsc();
	 public Optional<List<Appointment>> findByClient(Person client);
	 @Query("SELECT a FROM Appointment a WHERE a.session.id = :sessionId AND a.client.id = :clientId")
	 public Optional<Appointment> findTopBySessionAndClient(@Param("sessionId") Long sessionId, @Param("clientId") Long clientId);
}
