package com.eaProject.demo.services;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.AppointmentRepository;
import com.eaProject.demo.repository.SessionRepository;

@Service
public class AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private SessionRepository sessionRepository;

	// Service for adding an endpoint by Orgil
	public Appointment addAppointment(Appointment appointment) {
		long sessId = appointment.getSession().getId();
		Session session = sessionRepository.getOne(sessId);
		if(session.getDate().compareTo(new Date())>0) {
			return appointmentRepository.save(appointment);
		}else {
			return null;
		}	
	}

	// Service for deleting an endpoint by Orgil
	public void deleteAppointment(Long id) {
		appointmentRepository.deleteById(id);
	}

}
