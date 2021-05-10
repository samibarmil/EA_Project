package com.eaProject.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Appointment;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {

}
