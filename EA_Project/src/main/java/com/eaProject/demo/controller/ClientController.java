package com.eaProject.demo.controller;

import com.eaProject.demo.domain.AppointmentStatus;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eaProject.demo.domain.Appointment;
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

	@GetMapping("/sessions")
	public ResponseEntity<?> getSessions(@RequestParam Boolean futureOnly) {
		if(futureOnly)
			return ResponseEntity.ok(sessionService.getAllFutureSessions());
		return ResponseEntity.ok(sessionService.getAllSessions());
	}
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

		// Todo: if session is in the past return error

		Appointment appointment = appointmentService.addAppointment(new Appointment(session, currentUser));
		appointment.getClient().setPassword(null);

		return ResponseEntity.ok(appointment);
	}

	// endpoint for deleting an appointment by Orgil
	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@RequestHeader(value="User-Agent") String userAgent, @PathVariable(name = "id") Long appointmentId) {
		appointmentService.deleteAppointmentClient(appointmentId);
	}
	
	@PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<?> update(@PathVariable(value = "id") Long id) throws Exception{

		Person currentUser = personService.getCurrentUser();
		Appointment appointment = appointmentService.getAppointment(id);

		// check if the appointment belongs to the user
		if(!appointmentService.isOwnerOfAppointment(currentUser, appointment))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have right to cancel");

		//  Appointments can be cancelled or modified up to 48 hours before the session
		if(!sessionService.isSessionInFuture(appointment.getSession()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can not update appointments of passed sessions.");
		appointment.setAppointmentStatus(AppointmentStatus.CANCELED);

		// Todo : Send an email (to the creator of appointment, counselor and customer)
		return ResponseEntity.ok(appointmentService.updatefromclient(id,appointment));
    }

	// All appointments requested by the client
	@GetMapping(path="/appointments",produces = "application/json; charset=UTF-8")
	@ResponseBody
	public ResponseEntity<?> GetAll() throws Exception {
		Person currentPerson = personService.getCurrentUser();
		return ResponseEntity.ok(appointmentService.getClientAppointments(currentPerson));
	}
}
