package com.eaProject.demo.services;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

	// Service for adding an appointment by Orgil
	public Appointment addAppointment(Appointment appointment) {
		long sessId = appointment.getSession().getId();
		Session session = sessionRepository.getOne(sessId);
		if (session.getDate().compareTo(new Date()) > 0) {
			return appointmentRepository.save(appointment);
		} else {
			throw new RuntimeException("Appointment not in the future");
		}
	}

	// Service for deleting an appointment for Client by Orgil
	public void deleteAppointmentClient(Long id) {
		if (appointmentRepository.getOne(id) != null) {
			Date sessionDate = appointmentRepository.getOne(id).getSession().getDate();

			long diffInMillies = Math.abs(sessionDate.getTime() - new Date().getTime());
			long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			if (diff > 48) {
				appointmentRepository.deleteById(id);
			} else {
				throw new RuntimeException("Less than 48 hours");
			}
		} else {
			throw new RuntimeException("Appointment doesn't exist");
		}
	}

	// Service for deleting an appointment for Admin by Orgil
	public void deleteAppointmentAdmin(Long id) {
		appointmentRepository.deleteById(id);
	}

}
