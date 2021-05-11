package com.eaProject.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
	public void deleteAppointment(@RequestHeader(value="User-Agent") String userAgent, @PathVariable(name = "id") Long appointmentId) {
		
		appointmentService.deleteAppointmentClient(appointmentId);
	}
	
	@PutMapping("/client/appointments/{id}")
    public Appointment update(@PathVariable(value = "id") Long id,@Valid @RequestBody Appointment appointment) throws Exception{
        return appointmentService.updatefromclient(id,appointment);
    }
	
	@GetMapping(path="/client/appointments",produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<Appointment> GetAll() throws Exception {
		List<Appointment> a = appointmentService.GetAllClient();
		return a;
	}
}
