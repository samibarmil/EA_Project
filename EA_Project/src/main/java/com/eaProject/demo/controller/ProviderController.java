package com.eaProject.demo.controller;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	ResponseEntity<?> getSession(@RequestParam(required = false, name = "futureOnly") boolean futureOnly) throws Exception {
		Person currentUser = personService.getCurrentUser();
		if(futureOnly){
			Date today = new Date();
			List<Session> sessions = sessionService.getSessionsByProvider(currentUser)
					.stream()
					.filter(session -> session.getDate().after(today))
					.collect(Collectors.toList());
		}
		List<Session> sessions = sessionService.getSessionsByProvider(currentUser);
		return ResponseEntity.ok(sessions);
	}

	@PostMapping(path = "/sessions", produces = "application/json")
	ResponseEntity<?> addSession(@RequestBody Session session) {
		Person currentUser = personService.getCurrentUser();
		session.setProvider(currentUser);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.CREATED, session);
		return ResponseEntity.ok(sessionService.addSession(session));
	}

	@PutMapping(path = "/sessions/{id}")
	ResponseEntity<?> editSession(@PathVariable Long id, @RequestBody Session session) {
		Person currentUse = personService.getCurrentUser();
		if (!sessionService.doesSessionBelongsToProvider(id, currentUse))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have no access to this session.");
		try {
			// Todo: check if the provider owns the session
			Session edited_session = sessionService.editSession(id, session);
			emailservice.DomainEmailNotification(currentUse, NotificationAction.UPDATED, edited_session);
			return ResponseEntity.ok(edited_session);
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	@GetMapping("/sessions/{id}")
	ResponseEntity<?> getSession(@PathVariable Long id) {
		Person currentUser = personService.getCurrentUser();
		Session session = sessionService.getSessionById(id).orElse(null);

		if (session == null)
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Session with id : %d not found", id));

		if (session != null && !session.getProvider().getUsername().equals(currentUser.getUsername()))
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have no access to this session.");
		return ResponseEntity.ok(session);
	}

	@DeleteMapping("/sessions/{id}")
	ResponseEntity<?> deleteSession(@PathVariable Long id) {
		try {
			Person currentUser = personService.getCurrentUser();
			sessionService.removeSessionFromProvider(id, currentUser);
			emailservice.DomainEmailNotification(currentUser, NotificationAction.DELETED, sessionService.getSessionById(id));
			return ResponseEntity.ok("count : 1");
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) {
		Person currentUser = personService.getCurrentUser();
		try {
			return ResponseEntity.ok(sessionService.getSessionAppointments(id, currentUser));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	@GetMapping("/sessions/{id}/approved-appointment")
	ResponseEntity<?> getSessionApprovedAppointment(@PathVariable Long id) {
		Person currentUser = personService.getCurrentUser();
		Appointment appointment = null;
		try {
			appointment = appointmentService.getApprovedAppointment(id);
			if(appointment != null &&
					!currentUser.getUsername().equals(
							appointment.getSession().getProvider().getUsername())
			)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized to access this session.");
			return ResponseEntity.ok(appointment);
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	// Todo: [GET] /appointments/{id}

}
