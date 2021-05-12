package com.eaProject.demo.controller;

import com.eaProject.demo.domain.*;
import com.eaProject.demo.services.AppointmentService;
import com.eaProject.demo.services.EmailService;
import com.eaProject.demo.services.NotificationAction;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	public ResponseEntity<?> addProvider(@RequestBody Person person) {
		PersonRole[] personRoles = new PersonRole[] { new PersonRole(Role.PROVIDER) };
		person.setPersonRole(Arrays.asList(personRoles));
		Person personWithId = null;
		try {
			personWithId = personService.addPerson(person);
			emailservice.DomainEmailNotification(person, NotificationAction.CREATED, personWithId);
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
		}
		personWithId.setPassword(null);
		return ResponseEntity.ok(personWithId);
	}

	// Todo: GET /sessions?futureOnly=true
	@GetMapping("/sessions")
	ResponseEntity<?> getSession(@RequestParam("futureOnly") boolean futureOnly) throws Exception {
		if(futureOnly) {
			Date date = new Date();
			List<Session> sessionList = sessionService.getAllSessions().stream().filter(session -> {
				return session.getDate().after(date);
			}).collect(Collectors.toList());
			for(Session session: sessionList){
				System.out.println(session.getDate().toString());
			}
			return ResponseEntity.ok(sessionList);
		}
		return ResponseEntity.ok(sessionService.getAllSessions());
	}

	// Todo: GET /sessions/{id}
	@GetMapping("/sessions/{id}")
	ResponseEntity<?> getSession(@PathVariable long id) throws Exception {
		return ResponseEntity.ok(sessionService.getSessionById(id).orElseThrow(() -> new Exception("Id not found")));
	}

	// Todo: GET /sessions/{id}/appointments

	// Todo: DELETE /sessions/{id}
	@DeleteMapping("/sessions/delete/{id}")
	void deleteSession(@PathVariable long id) {
		sessionService.deleteSessionById(id);
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, sessionService.getSessionById(id));
	}

	// todo: EDIT /sessions/edit/{id}
	@PutMapping("/sessions/edit/{id}")
	ResponseEntity<?> editSession(@RequestBody Session editSession, @PathVariable long id) throws Exception {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, editSession);
		return ResponseEntity.ok(sessionService.editSession(id, editSession));
	}

	// todo: ADD /sessions/add
	@PostMapping(path = "/sessions/add")
	ResponseEntity<?> addSession(@RequestBody Session session) {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.CREATED, session);
		return ResponseEntity.ok(sessionService.addSession(session));
	}

	// Todo: GET /appointments/
	@GetMapping("/sessions/{id}/appointments")
	ResponseEntity<?> getSessionAppointments(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(sessionService.getSessionAppointments(id));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
		}
	}

	// Todo: UPDATE /appointments/{id}
	@PutMapping("/client/appointments/{id}")
	public Appointment update(@PathVariable(value = "id") Long id, @Valid @RequestBody Appointment appointment)
			throws Exception {
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, appointment);
		return appointmentService.updatefromadmin(id, appointment);
	}

	// Todo: DELETE /appointments/{id}
	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@RequestHeader(value = "User-Agent") String userAgent,
			@PathVariable(name = "id") Long appointmentId) {
		
		emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.DELETED, "");
		appointmentService.deleteAppointmentClient(appointmentId);
	}

	//GET /persons
	@GetMapping("/persons")
	public ResponseEntity<?> getAllPersons() {
		return ResponseEntity.ok(personService.getAllPersons());
	}

	//GET /persons/{id}
	@GetMapping("/persons/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable(value = "id") Long id){
		try {
			return ResponseEntity.ok(personService.getPersonById(id));
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}

	//UPDATE /persons/{id}
	@PutMapping("/persons/{id}")
	public ResponseEntity<?> updatePerson(@PathVariable(value = "id")Long id, @RequestBody Person person){
		try {
			Person p = personService.updatePerson(id, person);
			emailservice.DomainEmailNotification(personService.getCurrentUser(), NotificationAction.UPDATED, p);
			return ResponseEntity.ok(personService.updatePerson(id, p));
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
	// Todo: UPDATE /persons/{id}

    // Todo: UPDATE /appointments/{id}/approve
    @PatchMapping("/appointments/{id}/approve")
    public ResponseEntity<?> approveAppointment(@PathVariable Long id) {
        Appointment appointment = null;
        try {
            appointment = appointmentService.getAppointment(id);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        if(appointment == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Appointment with id : %d not found", id));

        if(appointmentService.hasApprovedAppointment(appointment.getSession().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The session has approved appointment.");
        }
        appointment.setAppointmentStatus(AppointmentStatus.APPROVED);
        return ResponseEntity.ok(appointmentService.updateAppointment(appointment));
    }
}
