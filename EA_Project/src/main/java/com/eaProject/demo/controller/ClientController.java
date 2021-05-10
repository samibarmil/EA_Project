package com.eaProject.demo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eaProject.demo.domain.Appointment;
import com.eaProject.demo.services.AppointmentService;

@RestController
@RequestMapping("/client")
public class ClientController {
	@Autowired
	private AppointmentService appointmentService;

	// endpoint for creating appointment by Orgil
	@PostMapping("/appointments")
	public Appointment createAppointment(@Valid @RequestBody Appointment appointment) {
		return appointmentService.addAppointment(appointment);
	}

	// endpoint for deleting an appointment by Orgil
	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@PathVariable(name = "id") Long appointmentId) {
		appointmentService.deleteAppointmentClient(appointmentId);
	}
}
