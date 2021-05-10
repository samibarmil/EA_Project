package com.eaProject.demo.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Appointment {

	@Id @GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	private AppointmentStatus appointmentStatus = AppointmentStatus.REQUESTED;
	@ManyToOne
	@JoinColumn
	private Person client;
	@ManyToOne
	@JoinColumn
	private Session session;
	
}
