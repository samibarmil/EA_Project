package com.eaProject.demo.controller;

import com.eaProject.demo.domain.AppointmentStatus;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.exceptions.UnauthorizedAccessException;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.services.AppointmentService;
import com.eaProject.demo.services.EmailService;
import com.eaProject.demo.services.NotificationAction;

@RestController
@RequestMapping("/client")
public class ClientController {
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private PersonService personService;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private EmailService emailService;

	@GetMapping("/sessions")
	public ResponseEntity<?> getSessions(@RequestParam(required = false) Boolean futureOnly) {
		if (futureOnly)
			return ResponseEntity.ok(sessionService.getAllFutureSessions());
		return ResponseEntity.ok(sessionService.getAllSessions());
	}

	@GetMapping("/sessions/{id}")
	public ResponseEntity<?> getSession(@PathVariable Long id) {
		return ResponseEntity.ok(sessionService.getSessionById(id));
	}

	// endpoint for creating appointment by Orgil
	@PostMapping("/sessions/{id}/appointments")
	public ResponseEntity<?> createAppointment(@PathVariable Long id)
			throws UnprocessableEntityException {

		Person currentUser = personService.getCurrentUser();
		if (!appointmentService.isFirstAppointmentOfSession(id, currentUser))
			throw new UnprocessableEntityException("Only one appointment allowed for a session");

		Session session = sessionService.getSessionById(id);

		Appointment appointment = appointmentService.addAppointment(new Appointment(session, currentUser));

		emailService.EmailNotification(currentUser, NotificationAction.CREATED, "Appointment");
		appointment.getClient().setPassword(null);
		return ResponseEntity.ok(appointment);
	}

	// endpoint for deleting an appointment by Orgil
	@DeleteMapping("/appointments/{id}")
	public ResponseEntity<?> deleteAppointment(@PathVariable(name = "id") Long appointmentId)
			throws UnprocessableEntityException, UnauthorizedAccessException {
		Person currentUser = personService.getCurrentUser();
		Appointment appointment = appointmentService.getAppointment(appointmentId);

		if (!sessionService.isSessionInAfter48HoursOrMore(appointment.getSession())) {
			throw new UnprocessableEntityException("Can not access session starts in less than 48 hours.");
		}

		if (!appointmentService.isOwnerOfAppointment(currentUser, appointment))
			throw new UnauthorizedAccessException("You do not have right to delete");
		appointmentService.deleteAppointment(appointment);
		emailService.EmailNotification(currentUser, NotificationAction.DELETED, "Appointment");
		return ResponseEntity.ok("count : 1");
	}

	@PutMapping("/appointments/{id}/cancel")
	public ResponseEntity<?> update(@PathVariable(value = "id") Long id)
			throws UnprocessableEntityException, UnauthorizedAccessException {
		Person currentUser = personService.getCurrentUser();
		Appointment appointment = appointmentService.getAppointment(id);

		// check if the appointment belongs to the user
		if (!appointmentService.isOwnerOfAppointment(currentUser, appointment))
			throw new UnauthorizedAccessException("You do not have right to cancel");

		// Appointments can be cancelled or modified up to 48 hours before the session
		if (!sessionService.isSessionInAfter48HoursOrMore(appointment.getSession()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Can not update appointments of passed sessions.");
		appointment.setAppointmentStatus(AppointmentStatus.CANCELED);
		Appointment updatedAppointment = appointmentService.updateFromClient(id, appointment);
		emailService.EmailNotification(currentUser, NotificationAction.UPDATED, "Appointment");
		return ResponseEntity.ok(updatedAppointment);
	}

	// All appointments requested by the client
	@GetMapping(path = "/appointments", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public ResponseEntity<?> GetAll() throws Exception {
		Person currentPerson = personService.getCurrentUser();
		return ResponseEntity.ok(appointmentService.getClientAppointments(currentPerson));
	}

	// appointment of client
	@GetMapping(path = "/appointments/{id}", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public ResponseEntity<?> getAppointment(@PathVariable Long id) {
		Person currentPerson = personService.getCurrentUser();
		return ResponseEntity.ok(appointmentService.getClientAppointment(id, currentPerson));
	}
}
