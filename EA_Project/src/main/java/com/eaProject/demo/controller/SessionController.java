package com.eaProject.demo.controller;



import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    SessionRepository sessionRepository;

    @GetMapping(produces = "application/json")
    List<Session> getAllSession(){
        return sessionRepository.findAll();
    }

    @PostMapping(path = "/addSession", produces = "application/json")
    Session newSession(@RequestBody Session session){
        return sessionRepository.save(session);
    }

    // Single Item
    @GetMapping(path = "/{id}", produces = "application/json")
    Session getSession(@PathVariable long id) throws Exception {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new Exception("Id not found"));
    }

    @PutMapping("/{id}")
    Session editSession(@RequestBody Session editSession, @PathVariable long id){
        return sessionRepository.findById(id)
                .map(session -> {
                    session.setDate(editSession.getDate());
                    session.setDuration(editSession.getDuration());
                    session.setProvider(editSession.getProvider());
                    session.setLocation(editSession.getLocation());
                    session.setAppointments(editSession.getAppointments());
                    session.setStartTime(editSession.getStartTime());
                    return sessionRepository.save(session);
                }).orElseGet(() -> {
                    editSession.setId(id);
                    return sessionRepository.save(editSession);
                });
    }

    @DeleteMapping("/{id}")
    void deleteSession(@PathVariable long id){
        sessionRepository.deleteById(id);
    }
}
