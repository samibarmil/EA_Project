package com.eaProject.demo.controller;



import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @GetMapping
    List<Session> allSession(){
        return sessionService.getAllSession();
    }

    @PostMapping(path = "/addSession", produces = "application/json")
    Session newSession(@RequestBody Session session){
        return sessionService.addSession(session);
    }

    // Single Item
    @GetMapping(path = "/{id}")
    Session getSession(@PathVariable long id) throws Exception {
        return sessionService.getSessionById(id)
                .orElseThrow(() -> new Exception("Id not found"));
    }

    @PutMapping("/{id}")
    Session editSession(@RequestBody Session editSession, @PathVariable long id){
        return sessionService.getSessionById(id)
                .map(session -> {
                    session.setDate(editSession.getDate());
                    session.setDuration(editSession.getDuration());
                    session.setProvider(editSession.getProvider());
                    session.setLocation(editSession.getLocation());
                    session.setAppointments(editSession.getAppointments());
                    session.setStartTime(editSession.getStartTime());
                    return sessionService.addSession(session);
                }).orElseGet(() -> {
                    editSession.setId(id);
                    return sessionService.addSession(editSession);
                });
    }

    @DeleteMapping("/{id}")
    void deleteSession(@PathVariable long id){
        sessionService.deleteSessionById(id);
    }
}
