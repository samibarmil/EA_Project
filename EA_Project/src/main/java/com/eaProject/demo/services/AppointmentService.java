package com.eaProject.demo.services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaProject.demo.domain.Appointment;
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
		
		public List<Appointment> GetAllClient() {
			List<Appointment> list = appointmentRepository.findAll();
			return list;
		}
		
		public List<Appointment> GetAllAdmin() {
			List<Appointment> list = appointmentRepository.findAll();
			return list;
		}
	
}
