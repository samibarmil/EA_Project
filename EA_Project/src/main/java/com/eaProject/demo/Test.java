package com.eaProject.demo;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.services.EmailService;
import com.eaProject.demo.services.NotificationAction;

@RestController
@RequestMapping("/test")
public class Test {
	
	private EmailService emailservice;
	
	@Autowired
	public Test(EmailService emailService) {
		this.emailservice = emailService;
	}
	
	@PostMapping
	public void testEmail(@RequestBody Person person, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			throw new ValidationException("Person object is not valid");
		}
		
		emailservice.EmailNotification(person, NotificationAction.CANCELED, Session.class.getSimpleName());
	}

}
