package com.eaProject.demo.services;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.AppointmentStatus;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.exceptions.ResourceNotFoundException;
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
		
		if(appointmentRepository.getOne(id)!=null) {
			Appointment appointment = appointmentRepository.getOne(id);
			Person client = appointment.getClient();
			
			// check if cancelling appointment was approved or not
			if(appointment.getAppointmentStatus()==AppointmentStatus.APPROVED) {
				hasApprovedAppointment = true;
			}
			
			// calculate how much time left until session
			Date sessionDate = appointment.getSession().getDate();
			long diffInMillies = Math.abs(sessionDate.getTime() - new Date().getTime());
			long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			// if session is in more than 48 hours, cancel
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
		}else {
			throw  new ResourceNotFoundException("Appointment with that id doesn't exist", "id=",id);
		}
		

	}

	// Service for deleting an appointment for Admin by Orgil
	public void deleteAppointmentAdmin(Long id) {
		Person admin = appointmentRepository.getOne(id).getClient();
		appointmentRepository.deleteById(id);
		emailService.EmailNotification(admin, NotificationAction.CANCELED, "Appointment");
	}
	
	public Appointment updatefromclient(Long id,@Valid Appointment appointment) throws Exception {
		if(appointmentRepository.getOne(id)!=null) {
			Date getsessiondate = appointmentRepository.getOne(id).getSession().getDate();
			long m = Math.abs(getsessiondate.getTime() - new Date().getTime());
			long d = TimeUnit.HOURS.convert(m, TimeUnit.MILLISECONDS);
			if (d > 48) {
				Appointment appointments=  appointmentRepository.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException("Note", "id",id));
				appointments.setAppointmentStatus(appointment.getAppointmentStatus());
				//appointments.setClient(appointment.getClient());
				//appointments.setSession(appointment.getSession());
				//appointments.setSession(appointment.get);

			    Appointment appointmentupdated=  appointmentRepository.save(appointments);
			    return appointmentupdated;
			}
			else {
				throw  new ResourceNotFoundException("Appointments can be cancelled or modified up to 48 hours before the session", "id=",id);
			}
			
		}else {
			 throw  new ResourceNotFoundException("Appointment with that id doesn't exist", "id=",id);
		}
	    
	}

	public Appointment updatefromadmin(Long id,@Valid Appointment appointment) throws Exception {

			Appointment appointments=  appointmentRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Appointment with that id doesn't exist", "id",id));
			appointments.setAppointmentStatus(appointment.getAppointmentStatus());
			//appointments.setClient(appointment.getClient());  //--> TO DISCUSS
			appointments.setSession(appointment.getSession());
			//appointments.setSession(appointment.get);

		    Appointment appointmentupdated=  appointmentRepository.save(appointments);
			
		    return appointmentupdated;
	}
		
	public List<Appointment> GetAllAdmin() {
			List<Appointment> list = appointmentRepository.findAll();
			return list;
	}

	public List<Appointment> getClientAppointments(Person client) {
		return appointmentRepository.findByClient(client)
				.orElse(Collections.emptyList());
	}

	public Boolean isFirstAppointmentOfSession(Long sessionId, Person client) {
		Appointment appointment = appointmentRepository
				.findTopBySessionAndClient(sessionId, client.getId())
				.orElse(null);
		return appointment == null;
	}

	public Boolean isOwnerOfAppointment(Person client, Appointment appointment) {

		return  client.getUsername().equals(appointment.getClient().getUsername());
	}

	public Appointment getAppointment(Long appointmentId) throws Exception {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElse(null);
		if(appointment == null)
			throw new Exception(String.format("Appointment with id : %d not found", appointmentId));
		return appointment;
	}
}
