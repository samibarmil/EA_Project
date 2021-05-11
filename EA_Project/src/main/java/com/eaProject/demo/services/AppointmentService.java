package com.eaProject.demo.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.AppointmentStatus;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.AppointmentRepository;
import com.eaProject.demo.repository.SessionRepository;

@Service
public class AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private EmailService emailService;

	// Service for adding an appointment by Orgil
	public Appointment addAppointment(Appointment appointment) {
		long sessId = appointment.getSession().getId();
		Person client = appointment.getClient();
		Session session = sessionRepository.getOne(sessId);
		if (session.getDate().compareTo(new Date()) > 0) {
			emailService.EmailNotification(client , NotificationAction.CREATED, "Appointment");
			return appointmentRepository.save(appointment);
		} else {
			throw new RuntimeException("Appointment not in the future");
		}
	}

	// Service for deleting an appointment for Client by Orgil
	public void deleteAppointmentClient(Long id) {
		boolean hasApprovedAppointment = false;
		
		Appointment appointment = appointmentRepository.getOne(id);
		Person client = appointment.getClient();

		// check there are APPROVED appointments
		if(appointment.getAppointmentStatus()==AppointmentStatus.APPROVED) {
			hasApprovedAppointment = true;
		}
		Date sessionDate = appointment.getSession().getDate();
		long diffInMillies = Math.abs(sessionDate.getTime() - new Date().getTime());
		long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		// if session is later than 48 hours, cancel
		if (diff > 48) {
			appointmentRepository.deleteById(id);
			emailService.EmailNotification(client, NotificationAction.CANCELED, "Appointment");
		} else {
			throw new RuntimeException("Less than 48 hours");
		}
		
		List<Appointment> appointments = new ArrayList<>();
		appointments = appointmentRepository.findAllByOrderByIdAsc();

		//if deleted appointment was approved then make next appointment approved (Orgil)
		if(hasApprovedAppointment && appointments!=null) {
			Appointment nextAppointment = appointments.get(0);
			nextAppointment.setAppointmentStatus(AppointmentStatus.APPROVED);
			appointmentRepository.save(appointment);
		}
	}

	// Service for deleting an appointment for Admin by Orgil
	public void deleteAppointmentAdmin(Long id) {
		Person admin = appointmentRepository.getOne(id).getClient();
		appointmentRepository.deleteById(id);
		emailService.EmailNotification(admin, NotificationAction.CANCELED, "Appointment");
	}

}
