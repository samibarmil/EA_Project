package com.eaProject.demo.controller;

import com.eaProject.demo.domain.*;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    PersonService personService;

    @Autowired
    SessionService sessionService;

    @RequestMapping("/add-provider")
    public ResponseEntity<?> addProvider(@RequestBody Person person) {
        PersonRole[] personRoles = new PersonRole[] {new PersonRole(Role.PROVIDER)};
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
    Session getSession(@PathVariable long id) throws Exception {
        return sessionService.getSessionById(id)
                .orElseThrow(() -> new Exception("Id not found"));
    }

    // Todo: GET /sessions/{id}/appointments

    // Todo: DELETE /sessions/{id}
    @DeleteMapping("/sessions/delete/{id}")
    void deleteSession(@PathVariable long id){
        sessionService.deleteSessionById(id);
    }

    // todo: EDIT /sessions/edit/{id}
    @PutMapping("/sessions/edit/{id}")
    Session editSession(@RequestBody Session editSession, @PathVariable long id) throws Exception {
        return sessionService.editSession(id, editSession);
    }

    // todo: ADD /sessions/add
    @PostMapping(path = "/sessions/add")
    Session addSession(@RequestBody Session session){
        return sessionService.addSession(session);
    }

    // Todo: GET /appointments/

    // Todo: UPDATE /appointments/{id}

    // Todo: DELETE /appointments/{id}

    // Todo: GET /persons

    // Todo: GET /persons/{id}

    // Todo: UPDATE /persons/{id}
}
