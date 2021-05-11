package com.eaProject.demo.controller;

import com.eaProject.demo.domain.*;
import com.eaProject.demo.services.AppointmentService;
import com.eaProject.demo.services.EmailService;
import com.eaProject.demo.services.NotificationAction;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private PersonService personService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private EmailService emailservice;

	@RequestMapping("/add-provider")
	public ResponseEntity<?> addProvider(@RequestBody Person person) {
		PersonRole[] personRoles = new PersonRole[] { new PersonRole(Role.PROVIDER) };
		person.setPersonRole(Arrays.asList(personRoles));
		Person personWithId = null;
		try {
			personWithId = personService.addPerson(person);
			emailservice.DomainEmailNotification(person, NotificationAction.CREATED, personWithId);
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
		}
		personWithId.setPassword(null);
		return ResponseEntity.ok(personWithId);
	}

	// Todo: GET /sessions?futureOnly=true

	// Todo: GET /sessions/{id}
	Session getSession(@PathVariable long id) throws Exception {
		return sessionService.getSessionById(id).orElseThrow(() -> new Exception("Id not found"));
	}

	// Todo: GET /sessions/{id}/appointments

	// Todo: DELETE /sessions/{id}
	@DeleteMapping("/sessions/delete/{id}")
	void deleteSession(@PathVariable long id) {
		sessionService.deleteSessionById(id);
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, sessionService.getSessionById(id));
	}

	// todo: EDIT /sessions/edit/{id}
	@PutMapping("/sessions/edit/{id}")
	Session editSession(@RequestBody Session editSession, @PathVariable long id) throws Exception {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, editSession);
		return sessionService.editSession(id, editSession);

	}

	// todo: ADD /sessions/add
	@PostMapping(path = "/sessions/add")
	Session addSession(@RequestBody Session session) {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.CREATED, session);
		return sessionService.addSession(session);
	}

	// Todo: GET /appointments/
	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(sessionService.getSessionAppointments(id));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	// Todo: UPDATE /appointments/{id}
	@PutMapping("/client/appointments/{id}")
	public Appointment update(@PathVariable(value = "id") Long id, @Valid @RequestBody Appointment appointment)
			throws Exception {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, appointment);
		return appointmentService.updatefromadmin(id, appointment);
	}

	// Todo: DELETE /appointments/{id}
	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@RequestHeader(value = "User-Agent") String userAgent,
			@PathVariable(name = "id") Long appointmentId) {
		
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, "");
		appointmentService.deleteAppointmentClient(appointmentId);
	}

	//GET /persons
	@GetMapping("/persons")
	public ResponseEntity<?> getAllPersons() {
		return ResponseEntity.ok(personService.getAllPersons());
	}

	//GET /persons/{id}
	@GetMapping("/persons/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable(value = "id") Long id){
		try {
			return ResponseEntity.ok(personService.getPersonById(id));
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}

	//UPDATE /persons/{id}
	@PutMapping("/persons/{id}")
	public ResponseEntity<?> updatePerson(@PathVariable(value = "id")Long id, @RequestBody Person person){
		try {
			Person p = personService.updatePerson(id, person);
			emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, p);
			return ResponseEntity.ok(personService.updatePerson(id, p));
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
}
