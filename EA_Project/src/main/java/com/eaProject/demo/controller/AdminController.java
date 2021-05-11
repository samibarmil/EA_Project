package com.eaProject.demo.controller;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.PersonRole;
import com.eaProject.demo.domain.Role;
import com.eaProject.demo.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	PersonService personService;

	@RequestMapping("/add-provider")
	public ResponseEntity<?> addProvider(@RequestBody Person person) {
		PersonRole[] personRoles = new PersonRole[] { new PersonRole(Role.PROVIDER) };
		person.setPersonRole(Arrays.asList(personRoles));
		Person personWithId = null;
		try {
			personWithId = personService.addPerson(person);
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
		}
		personWithId.setPassword(null);
		return ResponseEntity.ok(personWithId);
	}

	// Todo: GET /sessions?futureOnly=true

	// Todo: GET /sessions/{id}

	// Todo: GET /sessions/{id}/appointments

	// Todo: DELETE /sessions/{id}

	// Todo: GET /appointments/

	// Todo: UPDATE /appointments/{id}

	// Todo: DELETE /appointments/{id}

	// Todo: GET /persons

	// Todo: GET /persons/{id}

	// Todo: UPDATE /persons/{id}
}
