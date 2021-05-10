package com.eaProject.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.repository.AppointmentRepository;


@Service
public class AppointmentService {
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	// Service for adding an endpoint by Orgil
	public Appointment addAppointment(Appointment appointment) {
		return appointmentRepository.save(appointment);
	}
	
	// Service for deleting an endpoint by Orgil
	public void deleteAppointment(Long id) {
		appointmentRepository.deleteById(id);
	}
	
}
