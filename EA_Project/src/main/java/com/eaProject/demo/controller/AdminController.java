package com.eaProject.demo.controller;

import com.eaProject.demo.domain.*;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import com.eaProject.demo.services.AppointmentService;
import com.eaProject.demo.services.EmailService;
import com.eaProject.demo.services.NotificationAction;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
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
	public ResponseEntity<?> addProvider(@RequestBody Person person) throws UnprocessableEntityException {
		PersonRole[] personRoles = new PersonRole[] { new PersonRole(Role.PROVIDER) };
		person.setPersonRole(Arrays.asList(personRoles));
		Person personWithId = null;
		personWithId = personService.addPerson(person);
		emailservice.DomainEmailNotification(person, NotificationAction.CREATED, personWithId);
		personWithId.setPassword(null);
		return ResponseEntity.ok(personWithId);
	}

	// Todo: GET /sessions?futureOnly=true
	@GetMapping("/sessions")
	ResponseEntity<?> getSession(@RequestParam(required = false, name = "futureOnly") boolean futureOnly) throws Exception {
		if(futureOnly) {
			return ResponseEntity.ok(sessionService.getAllFutureSessions());
		}
		return ResponseEntity.ok(sessionService.getAllSessions());
	}

	// Todo: GET /sessions/{id}
	@GetMapping("/sessions/{id}")
	ResponseEntity<?> getSession(@PathVariable long id) throws Exception {
		return ResponseEntity.ok(sessionService.getSessionById(id));
	}

	// Todo: DELETE /sessions/{id}
	@DeleteMapping("/sessions/{id}")
	ResponseEntity<?> deleteSession(@PathVariable long id) {
		Session session = sessionService.getSessionById(id);
		sessionService.deleteSessionById(id);
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, "");
		return ResponseEntity.ok("Delete Successfully");
	}

	// todo: EDIT /sessions/{id}
	@PutMapping("/sessions/{id}")
	ResponseEntity<?> editSession(@RequestBody Session editSession, @PathVariable long id)
			throws UnprocessableEntityException {
		Session session = sessionService.getSessionById(id);
		editSession.setProvider(session.getProvider());
		Session updateSession = sessionService.editSession(id, editSession);
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, updateSession);
		return ResponseEntity.ok(updateSession);
	}

	// todo: ADD /sessions
	@PostMapping(path = "/sessions", produces = "application/json")
	ResponseEntity<?> addSession(@RequestBody Session session) {
		Person currentUser = personService.getCurrentUser();
		session.setProvider(currentUser);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.CREATED, session);
		return ResponseEntity.ok(sessionService.addSession(session));
	}

	// Todo: GET /sessions/{id}/appointments
	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) {
		return ResponseEntity.ok(sessionService.getSessionAppointments(id));
	}


	// Todo: GET /appointments
	@GetMapping("/appointments")
	ResponseEntity<?> getAppointments() {
		return ResponseEntity.ok(appointmentService.getAllAppointment());
	}

	// Todo: GET /appointments
	@GetMapping("/appointments/{id}")
	ResponseEntity<?> getAppointmentById(@PathVariable("id") long id) {
		return ResponseEntity.ok(appointmentService.getAppointment(id));
	}

	// Todo: UPDATE /appointments/{id}
	@PutMapping("/appointments/{id}")
	public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @Valid @RequestBody Appointment appointment) {
		Appointment currentAppointment = appointmentService.getAppointment(id);
		appointment.getSession().setProvider(currentAppointment.getSession().getProvider());
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, appointment);
		return ResponseEntity.ok(appointmentService.updateFromAdmin(id, appointment));
	}

	// Todo: DELETE /appointments/{id}
	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@RequestHeader(value = "User-Agent") String userAgent,
			@PathVariable(name = "id") Long appointmentId) {
		Appointment appointment = appointmentService.getAppointment(appointmentId);
		appointmentService.deleteAppointment(appointment);
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, "");
	}

	// Todo: GET /persons
	@GetMapping("/persons")
	public ResponseEntity<?> getAllPersons() {
		return ResponseEntity.ok(personService.getAllPersons());
	}

	// Todo: GET /persons/{id}
	@GetMapping("/persons/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable(value = "id") Long id){
		Person person = personService.getPersonById(id);
		if(person == null) {
			throw new EntityNotFoundException(String.format("Person with id : %d not found.", id));
		}
		return ResponseEntity.ok(person);
	}

	// Todo: UPDATE /persons/{id}
	@PutMapping("/persons/{id}")
	public ResponseEntity<?> updatePerson(@PathVariable(value = "id")Long id, @RequestBody Person person)
			throws UnprocessableEntityException {
			Person p = personService.updatePerson(id, person);
			emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, p);
			return ResponseEntity.ok(p);
	}

    // Todo: UPDATE /appointments/{id}/approve
    @PatchMapping("/appointments/{id}/approve")
    public ResponseEntity<?> approveAppointment(@PathVariable Long id) throws UnprocessableEntityException {
        Appointment appointment = appointmentService.getAppointment(id);
        if(appointmentService.hasApprovedAppointment(appointment.getSession().getId())) {
           throw new UnprocessableEntityException("The session has approved appointment.");
        }
        appointment.setAppointmentStatus(AppointmentStatus.APPROVED);
        return ResponseEntity.ok(appointmentService.updateAppointment(appointment));
    }
}
