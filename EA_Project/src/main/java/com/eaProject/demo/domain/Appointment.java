package com.eaProject.demo.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
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
	
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	@JsonProperty(value = "id")
	public void setId(Long id) {
		this.id = id;
	}
	@JsonProperty(value = "appointmentStatus")
	public AppointmentStatus getAppointmentStatus() {
		return appointmentStatus;
	}
	@JsonProperty(value = "appointmentStatus")
	public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}
	@JsonProperty(value = "client")
	public Person getClient() {
		return client;
	}
	@JsonProperty(value = "client")
	public void setClient(Person client) {
		this.client = client;
	}
	@JsonProperty(value = "session")
	public Session getSession() {
		return session;
	}
	@JsonProperty(value = "session")
	public void setSession(Session session) {
		this.session = session;
	}
	@Override
	public String toString() {
		return "Appointment [id=" + id + ", appointmentStatus=" + appointmentStatus + ", client=" + client
				+ ", session=" + session.getId() + "]";
	}

	
	public Appointment(Session session, Person client) {
		this.session = session;
		this.client = client;
	}
}
