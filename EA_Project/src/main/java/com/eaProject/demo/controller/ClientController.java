package com.eaProject.demo.controller;

import java.util.List;

import javax.validation.Valid;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.exceptions.ResourceNotFoundException;
import com.eaProject.demo.services.AppointmentService;

@RestController
@RequestMapping("/client")
public class ClientController {
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private PersonService personService;
	@Autowired
	private SessionService sessionService;

	// endpoint for creating appointment by Orgil
	@PostMapping("/sessions/{id}/appointments")
	public ResponseEntity<?> createAppointment(@PathVariable Long id) {

		Person currentUser = personService.getCurrentUser();
		if(!appointmentService.isFirstAppointmentOfSession(id, currentUser))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Only one appointment allowed for a session");

		Session session = sessionService.getSessionById(id).orElse(null);
		if (session == null)
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Session with id : %d not found!", id));

		Appointment appointment = appointmentService.addAppointment(new Appointment(session, currentUser));
		appointment.getClient().setPassword(null);

		return ResponseEntity.ok(appointment);
	}

	// endpoint for deleting an appointment by Orgil
	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@RequestHeader(value="User-Agent") String userAgent, @PathVariable(name = "id") Long appointmentId) {
		appointmentService.deleteAppointmentClient(appointmentId);
	}
	
	@PutMapping("/client/appointments/{id}")
    public Appointment update(@PathVariable(value = "id") Long id,@Valid @RequestBody Appointment appointment) throws Exception{
        return appointmentService.updatefromclient(id,appointment);
    }

	// All appointments requested by the client
	@GetMapping(path="/appointments",produces = "application/json; charset=UTF-8")
	@ResponseBody
	public ResponseEntity<?> GetAll() throws Exception {
		Person currentPerson = personService.getCurrentUser();
		return ResponseEntity.ok(appointmentService.getClientAppointments(currentPerson));
	}
}
