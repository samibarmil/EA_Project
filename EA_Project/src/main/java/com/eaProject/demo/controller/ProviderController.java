package com.eaProject.demo.controller;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/session")
public class ProviderController {


	@Autowired
	private SessionService sessionService;

	@GetMapping(path="/provider/sessions",produces = "application/json")
	List<Session> getAllSession(){
		return sessionService.getAllSession();
	}

	@PostMapping(path = "/provider/session/addSession", produces = "application/json")
	Session addSession(@RequestBody Session session){
		return sessionService.addSession(session);
	}

	//	    // Single Item
	//	    @GetMapping(path = "/provider/session/{id}", produces = "application/json")
	//	    Session getSession(@PathVariable long id) throws Exception {
	//	        return sessionservices.getSession(id)
	//	                ;
	//	    }

	@PutMapping(path = "/provider/session/{id}")
	Session editSession(@RequestBody Session editSession, @PathVariable long id) throws Exception{
		return sessionService.editSession(editSession, id);
	}

	@DeleteMapping("/provider/session/delete/{id}")
	void deleteSession(@PathVariable long id) throws Exception{
		sessionService.deleteSessionById(id);
	}
}


