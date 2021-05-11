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
	PersonService personService;
	@Autowired
	SessionService sessionService;

	@GetMapping(path="/sessions", produces = "application/json")
	public ResponseEntity<?> getSessions() {
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
			return ResponseEntity.ok(sessionService.editSession(id, session));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

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

}


