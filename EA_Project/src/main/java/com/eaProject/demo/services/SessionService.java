package com.eaProject.demo.services;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

	@Autowired
	private SessionRepository sessionRepository;

	// Service for get all Session
	public List<Session> getAllSession(){
		return sessionRepository.findAll();
	}

	// Service for get Session by Id
	public Optional<Session> getSessionById(long id){
		return sessionRepository.findById(id);
	}

	// Service for get Sessions by Provider
	public List<Session> getSessionByProvider(Person provider){
		return sessionRepository.findByProvider(provider);
	}

	// Service for add Session
	public Session addSession(Session session) {
		return sessionRepository.save(session);
	}

	// Service for deleting Session
	public void deleteSessionById(Long id) {
		sessionRepository.deleteById(id);
	}

	// Service for editing Session
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

}
