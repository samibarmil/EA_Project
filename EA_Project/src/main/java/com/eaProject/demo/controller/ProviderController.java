package com.eaProject.demo.controller;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;

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

	@GetMapping(path="/sessions", produces = "application/json")
	public ResponseEntity<?> getSessions() { // Todo: ?futureOnly=true
		Person currentUser = personService.getCurrentUser();
		List<Session> sessions = sessionService.getSessionsByProvider(currentUser);
		return ResponseEntity.ok(sessions);
	}

	@PostMapping(path = "/sessions", produces = "application/json")
	ResponseEntity<?> addSession(@RequestBody Session session){
		Person currentUser = personService.getCurrentUser();
		session.setProvider(currentUser);
		return ResponseEntity.ok(sessionService.addSession(session));
	}

	@PutMapping(path = "/sessions/{id}")
	ResponseEntity<?> editSession(@PathVariable Long id, @RequestBody Session session) {
		try {
			// Todo: check if the provider owns the session
			return ResponseEntity.ok(sessionService.editSession(id, session));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	// Todo: [GET] /sessions/{id}

	@DeleteMapping("/sessions/{id}")
	ResponseEntity<?> deleteSession(@PathVariable Long id) {
		try {
			Person currentUser = personService.getCurrentUser();
			sessionService.removeSessionFromProvider(id, currentUser);
			return ResponseEntity.ok("count : 1");
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	// Todo: [GET] /sessions/{id}/appointments
	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) {
		Person currentUser = personService.getCurrentUser();
		try {
			return ResponseEntity.ok(sessionService.getSessionAppointments(id, currentUser));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	// Todo: [GET] /appointments/{id}
}


