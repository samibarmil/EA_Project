package com.eaProject.demo.controller;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.repository.SessionRepository;
import com.eaProject.demo.services.SessionServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/session")
public class ProviderController {


	@Autowired
	private SessionServices sessionservices;

	@GetMapping(path="/provider/sessions",produces = "application/json")
	List<Session> getAllSession(){
		return sessionservices.getAllSession();
	}

	@PostMapping(path = "/provider/addSession", produces = "application/json")
	Session newSession(@RequestBody Session session){
		return sessionservices.newSession(session);
	}

	//	    // Single Item
	//	    @GetMapping(path = "/provider/session/{id}", produces = "application/json")
	//	    Session getSession(@PathVariable long id) throws Exception {
	//	        return sessionservices.getSession(id)
	//	                ;
	//	    }

	@PutMapping(path = "/provider/session/{id}")
	Session editSession(@RequestBody Session editSession, @PathVariable long id) throws Exception{
		return sessionservices.editSession(editSession, id);
	}

	@DeleteMapping("/provider/deletesession{id}")
	void deleteSession(@PathVariable long id) throws Exception{
		sessionservices.deleteSession(id);
	}
}


