package com.eaProject.demo.services;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.eaProject.demo.exceptions.UnprocessableEntityException;
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
	private SessionService sessionService;

	// Service for adding an appointment by Orgil
	public Appointment addAppointment(Appointment appointment) throws UnprocessableEntityException {
		long sessId = appointment.getSession().getId();
		Session session = sessionRepository.getOne(sessId);
		if (session.getDate().compareTo(new Date()) > 0) {
			return appointmentRepository.save(appointment);
		} else {
			throw new UnprocessableEntityException("Can not access session starts in less than 48 hours.");
		}
	}

	public void deleteAppointment(Appointment appointment) {

		appointmentRepository.deleteById(appointment.getId());

		//if deleted appointment was approved then make next appointment approved (Orgil)
		if(appointment.getAppointmentStatus().equals(AppointmentStatus.APPROVED)) {
			approveNextAppointment(appointment.getSession());
		}
	}

	private void approveNextAppointment(Session session) {
		List<Appointment> appointments =session.getAppointments();
		if(appointments.size() > 0) {
			Appointment nextAppointment = appointments.get(0);
			nextAppointment.setAppointmentStatus(AppointmentStatus.APPROVED);
			appointmentRepository.save(nextAppointment);
		}
	}

	public Appointment updateFromClient(Long id, @Valid Appointment appointment)
			throws UnprocessableEntityException {
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
				throw  new UnprocessableEntityException("Appointments can be cancelled or modified up to 48 hours before the session");
			}

		}
		else {
			 throw  new EntityNotFoundException(String.format("Appointment with id : %d doesn't exist",id));
		}

	}

	public Appointment updateFromAdmin(Long id, @Valid Appointment appointment) {

			Appointment newAppointment=  appointmentRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException(String.format("Appointment with id: %d doesn't exist", id)));
			newAppointment.setAppointmentStatus(appointment.getAppointmentStatus());
			//appointments.setClient(appointment.getClient());  //--> TO DISCUSS
			newAppointment.setSession(appointment.getSession());
			//appointments.setSession(appointment.get);
		    return updateAppointment(newAppointment);
	}

	public List<Appointment> GetAllAdmin() {
			List<Appointment> list = appointmentRepository.findAll();
			return list;
	}

	public List<Appointment> getClientAppointments(Person client) {
		return appointmentRepository.findByClient(client)
				.orElse(Collections.emptyList());
	}

	public Appointment getClientAppointment(Long appointmentId, Person client) {
		return appointmentRepository.findTopByIdAndClient(appointmentId, client)
				.orElse(null);
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

	public Appointment getAppointment(Long appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElse(null);
		if(appointment == null)
			throw new EntityNotFoundException(String.format("Appointment with id : %d not found", appointmentId));
		return appointment;
	}

	public List<Appointment> getAllAppointment() throws Exception {
		return appointmentRepository.findAll();
	}

	public Appointment updateAppointment(Appointment appointment) {
		return appointmentRepository.save(appointment);
	}

	public Appointment getApprovedAppointment(Long sessionId) {
		return appointmentRepository
				.findTopBySessionAndAppointmentStatus(sessionId, AppointmentStatus.APPROVED)
				.orElse(null);
	}

	public Boolean hasApprovedAppointment(Long sessionId) {
		return  getApprovedAppointment(sessionId) != null;
	}
}
