package com.eaProject.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.eaProject.demo.config.EmailConfig;
import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;

import lombok.Data;

@Service @Data
public class EmailService {
	
	private EmailConfig emailConfig;
	private JavaMailSenderImpl mailSender;
	private SimpleMailMessage mail;
	
	@Autowired
	public EmailService(EmailConfig emailConfig) {
		
		this.mailSender = new JavaMailSenderImpl();
		this.mail = new SimpleMailMessage();
		this.emailConfig = emailConfig;
		
		mailSender.setHost(emailConfig.getHost());
		mailSender.setPort(emailConfig.getPort());
		mailSender.setUsername(emailConfig.getUsername());
		mailSender.setPassword(emailConfig.getPassword());
		
		mail.setFrom("team3@ea.com");
		mail.setSubject("Appointment System Notification");
	}
	
 
	/**
	 * <h1>Send an email for session changes</h1>
	 * @param admin Person object which the email will be sent to.
	 * @param action ENUM of type NotificationAction (CREATED, UPDATED, DELETED or canceled).
	 * @param entity the name of the touched class
	 */
	public void EmailNotification(Person admin, NotificationAction action, String entity) {
		mail.setTo(admin.getEmail());
		mail.setText(entity +" has been "+ action.toString() +" successfuly\n");	
		mailSender.send(mail);	
	}
	
	/**
	 * <h1>Send an email for session changes</h1>
	 * @param admin Person object which the email will be sent to.
	 * @param action ENUM of type NotificationAction (CREATED, UPDATED, DELETED or canceled).
	 * @param entity that has been touched
	 */
	public void DomainEmailNotification(Person person, NotificationAction action, Object entity) {
		mail.setTo(person.getEmail());

		String entityType = "", entityData = "";
		switch (entity.getClass().getSimpleName()) {
		case "Session":
			entityType = "Session";
			entityData = ((Session) entity).toString();
			break;
		case "Appointment":
			entityType = "Appointment";
			entityData = ((Appointment) entity).toString();
			break;
		case "Person":
			entityType = "Person";
			entityData = ((Person) entity).toString();
			break;
		default:
			throw new IllegalArgumentException();

		}

		mail.setText(entityType + " has been " + action.toString() + " successfuly\n" + entityData);

		mailSender.send(mail);
	}

	/**
	 * <h1>Send a custom email notification</h1>
	 * @param person Person object which the email will be sent to.
	 * @param  subject Email subject
	 * @param message Email body
	 */
	public void CustomEmailNotification(Person person,String subject ,String message) {
		mail.setTo(person.getEmail());
		mail.setSubject(subject);
		mail.setText(message);	
		mailSender.send(mail);	
	}
	
	

}
