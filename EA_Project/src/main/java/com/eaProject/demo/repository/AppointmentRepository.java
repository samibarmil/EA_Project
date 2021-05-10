package com.eaProject.demo.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.Appointment;

@Repository
@Transactional
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {

}
