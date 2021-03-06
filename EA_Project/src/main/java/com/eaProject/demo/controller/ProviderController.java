package com.eaProject.demo.controller;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.exceptions.UnauthorizedAccessException;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import com.eaProject.demo.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/provider")
public class ProviderController {

	@Autowired
	private PersonService personService;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private EmailService emailservice;
	@Autowired
	private AppointmentService appointmentService;

	@GetMapping(path = "/sessions", produces = "application/json")
	ResponseEntity<?> getSession(@RequestParam(required = false, name = "futureOnly") boolean futureOnly) {
		Person currentUser = personService.getCurrentPersonByUsername();
		if(futureOnly){
			Date today = new Date();
			return ResponseEntity.ok(sessionService.getSessionsByProvider(currentUser)
					.stream()
					.filter(session -> session.getDate().after(today))
					.collect(Collectors.toList()));
		}
		List<Session> sessions = sessionService.getSessionsByProvider(currentUser);
		return ResponseEntity.ok(sessions);
	}

	@PostMapping(path = "/sessions", produces = "application/json")
	ResponseEntity<?> addSession(@RequestBody @Valid Session session) {
		Person currentUser = personService.getCurrentPersonByUsername();
		session.setProvider(currentUser);
		Session addedSession = sessionService.addSession(session);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.CREATED, session);
		return ResponseEntity.ok(addedSession);
	}

	@PutMapping(path = "/sessions/{id}")
	ResponseEntity<?> editSession(@PathVariable Long id, @Valid @RequestBody Session session)
			throws UnauthorizedAccessException, UnprocessableEntityException {
		Person currentUser = personService.getCurrentPersonByUsername();
		if (!sessionService.doesSessionBelongsToProvider(id, currentUser))
			throw new UnauthorizedAccessException("You have no access to this session.");
		// Todo : check if session is past
		Session edited_session = sessionService.editSession(id, session);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.UPDATED, edited_session);
		return ResponseEntity.ok(edited_session);
	}

	@GetMapping("/sessions/{id}")
	ResponseEntity<?> getSession(@PathVariable Long id) throws UnauthorizedAccessException {
		Person currentUser = personService.getCurrentPersonByUsername();
		Session session = sessionService.getSessionById(id);

		if (session != null && !session.getProvider().getUsername().equals(currentUser.getUsername()))
			throw new UnauthorizedAccessException("You have no access to this session.");
		return ResponseEntity.ok(session);
	}

	@DeleteMapping("/sessions/{id}")
	ResponseEntity<?> deleteSession(@PathVariable Long id) {
		Person currentUser = personService.getCurrentPersonByUsername();
		Session session = sessionService.getSessionById(id);
		// Todo : check if session is past
		sessionService.removeSessionFromProvider(session.getId(), currentUser);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.DELETED, session);
		return ResponseEntity.ok("count : 1");
	}

	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) throws UnauthorizedAccessException {
		Person currentUser = personService.getCurrentPersonByUsername();
		return ResponseEntity.ok(sessionService.getSessionAppointments(id, currentUser));
	}

	@GetMapping("/sessions/{id}/approved-appointment")
	ResponseEntity<?> getSessionApprovedAppointment(@PathVariable Long id) throws UnauthorizedAccessException {
		Person currentUser = personService.getCurrentPersonByUsername();
		Appointment appointment = appointmentService.getApprovedAppointment(id);
		if(appointment != null &&
				!currentUser.getUsername().equals(
						appointment.getSession().getProvider().getUsername())
		)
			throw new UnauthorizedAccessException("Unauthorized to access this session.");
		return ResponseEntity.ok(appointment);

	}

	// Todo: [GET] /appointments/{id}

}
