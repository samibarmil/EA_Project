package com.eaProject.demo.services;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

	@Autowired
	private SessionRepository sessionRepository;

	// Service for get all Session
	public List<Session> getAllSessions(){
		return sessionRepository.findAll();
	}

	public List<Session> getAllFutureSessions(){
		return sessionRepository.findFutureSessions().orElse(Collections.emptyList());
	}

	public List<Session> getSessionsByProvider(Person provider) {
		return sessionRepository.findByProvider(provider);
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
		sessionRepository.save(session);
		return session;
	}

	// Service for deleting Session
	public void deleteSessionById(Long id) {
		sessionRepository.deleteById(id);
	}

	// Service for editing Session
	public Session editSession(Long sessionId, Session updatedSession) throws Exception {

		if(!sessionId.equals(updatedSession.getId())) throw new Exception("Session id dose not match.");

		Session session = sessionRepository.findById(sessionId)
				.orElseThrow(() ->
						new Exception(String.format("Session with id : %d not found", sessionId))
				);
		sessionRepository.save(updatedSession);
		return updatedSession;
	}

	public void removeSessionFromProvider(Long sessionId, Person provider) throws Exception {
		Session session = sessionRepository.findTopByIdAndProvider(sessionId, provider)
				.orElseThrow(() -> new Exception(String.format("Session with id : %d is not found under given provider.", sessionId)));
		this.deleteSessionById(sessionId);
	}


	public List<Appointment> getSessionAppointments(Long id, Person currentUser) throws Exception {
		Session session = sessionRepository.findById(id)
				.orElseThrow(() -> new Exception(String.format("Session with id : %d not found", id)));

		if(!session.getProvider().getUsername().equals(currentUser.getUsername()))
			throw new Exception("You have no access to this session.");

		return session.getAppointments();
	}

	public Boolean doesSessionBelongsToProvider(Long sessionId, Person provider) {
		Session session = sessionRepository.findTopByIdAndProvider(sessionId, provider)
				.orElse(null);
		return session != null;
	}

	public Boolean isSessionInFuture(Session session) {
		return  session.getDate().after(java.sql.Date.valueOf(LocalDate.now().plusDays(2)));
	}
}
