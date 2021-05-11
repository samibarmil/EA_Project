package com.eaProject.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.SessionRepository;

public class SessionServices {
	 @Autowired
	   private SessionRepository sessionRepository;
	 
	 public List<Session> getAllSession(){
	        return sessionRepository.findAll();
	    }
	 public Session newSession( Session session){
	        return sessionRepository.save(session);
	    }
	public Session getSession( long id) throws Exception {
	        return sessionRepository.findById(id)
	                .orElseThrow(() -> new Exception("Id not found"));
	    }
	public Session editSession( Session editSession, long id){
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
	public void deleteSession( long id){
	        sessionRepository.deleteById(id);
	    }
}
