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

	// Todo: GET /sessions/{id}
	Session getSession(@PathVariable Long id) throws Exception {
		return sessionService.getSessionById(id);
	}

	// Todo: GET /sessions/{id}/appointments

	// Todo: DELETE /sessions/{id}
	@DeleteMapping("/sessions/delete/{id}")
	void deleteSession(@PathVariable Long id) {
		Session session = sessionService.getSessionById(id);
		sessionService.deleteSessionById(session.getId());
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, session);
	}

	// todo: EDIT /sessions/edit/{id}
	@PutMapping("/sessions/edit/{id}")
	ResponseEntity<?> editSession(@RequestBody Session editSession, @PathVariable long id)
			throws UnprocessableEntityException {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, editSession);
		return ResponseEntity.ok(sessionService.editSession(id, editSession));

	}

	// todo: ADD /sessions/add
	@PostMapping(path = "/sessions/add")
	ResponseEntity<?> addSession(@RequestBody Session session) {
		Session newSession = sessionService.addSession(session);
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.CREATED, session);
		return ResponseEntity.ok(newSession);
	}

	// Todo: GET /appointments/
	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) {
		return ResponseEntity.ok(sessionService.getSessionAppointments(id));
	}

	// Todo: UPDATE /appointments/{id}
	@PutMapping("/client/appointments/{id}")
	public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @Valid @RequestBody Appointment appointment) {
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

	//GET /persons
	@GetMapping("/persons")
	public ResponseEntity<?> getAllPersons() {
		return ResponseEntity.ok(personService.getAllPersons());
	}

	//GET /persons/{id}
	@GetMapping("/persons/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable(value = "id") Long id){
		Person person = personService.getPersonById(id);
		if(person == null)
			throw new EntityNotFoundException(String.format("Person with id : %d not found.", id));
		return ResponseEntity.ok(person);
	}

	//UPDATE /persons/{id}
	@PutMapping("/persons/{id}")
	public ResponseEntity<?> updatePerson(@PathVariable(value = "id")Long id, @RequestBody Person person)
			throws UnprocessableEntityException {
			Person p = personService.updatePerson(id, person);
			emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, p);
			return ResponseEntity.ok(p);
	}
	// Todo: UPDATE /persons/{id}

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
