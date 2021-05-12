package com.eaProject.demo.controller;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.exceptions.UnauthorizedAccessException;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import com.eaProject.demo.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	public ResponseEntity<?> getSessions() { // Todo: ?futureOnly=true
		Person currentUser = personService.getCurrentUser();
		List<Session> sessions = sessionService.getSessionsByProvider(currentUser);
		return ResponseEntity.ok(sessions);
	}

	@PostMapping(path = "/sessions", produces = "application/json")
	ResponseEntity<?> addSession(@RequestBody Session session) {
		Person currentUser = personService.getCurrentUser();
		session.setProvider(currentUser);
		Session addedSession = sessionService.addSession(session);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.CREATED, session);
		return ResponseEntity.ok(addedSession);
	}

	@PutMapping(path = "/sessions/{id}")
	ResponseEntity<?> editSession(@PathVariable Long id, @RequestBody Session session)
			throws UnauthorizedAccessException, UnprocessableEntityException {
		Person currentUse = personService.getCurrentUser();
		if (!sessionService.doesSessionBelongsToProvider(id, currentUse))
			throw new UnauthorizedAccessException("You have no access to this session.");
		Session edited_session = sessionService.editSession(id, session);
		emailservice.DomainEmailNotification(currentUse, NotificationAction.UPDATED, edited_session);
		return ResponseEntity.ok(edited_session);
	}

	@GetMapping("/sessions/{id}")
	ResponseEntity<?> getSession(@PathVariable Long id) throws UnauthorizedAccessException {
		Person currentUser = personService.getCurrentUser();
		Session session = sessionService.getSessionById(id);

		if (session != null && !session.getProvider().getUsername().equals(currentUser.getUsername()))
			throw new UnauthorizedAccessException("You have no access to this session.");
		return ResponseEntity.ok(session);
	}

	@DeleteMapping("/sessions/{id}")
	ResponseEntity<?> deleteSession(@PathVariable Long id) {
		Person currentUser = personService.getCurrentUser();
		Session session = sessionService.getSessionById(id);
		sessionService.removeSessionFromProvider(session.getId(), currentUser);
		emailservice.DomainEmailNotification(currentUser, NotificationAction.DELETED, session);
		return ResponseEntity.ok("count : 1");
	}

	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) throws UnauthorizedAccessException {
		Person currentUser = personService.getCurrentUser();
		return ResponseEntity.ok(sessionService.getSessionAppointments(id, currentUser));
	}

	@GetMapping("/sessions/{id}/approved-appointment")
	ResponseEntity<?> getSessionApprovedAppointment(@PathVariable Long id) throws UnauthorizedAccessException {
		Person currentUser = personService.getCurrentUser();
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
