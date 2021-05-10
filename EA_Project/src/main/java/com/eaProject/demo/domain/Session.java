package com.eaProject.demo.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Data
public class Session {
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private Person provider;
	
	@Temporal(TemporalType.DATE)
	private Date date;
	private Double duration;
	@Temporal(TemporalType.TIME)
	private Date startTime;
	private String location;
	
	@OneToMany(mappedBy = "session")
	@JsonIgnore
	private List<Appointment> appointments;

//	@Override
//	public String toString() {
//		return "Session{" +
//				"id=" + id +
//				", provider=" + provider.getFirstName() + " " + provider.getLastName() +
//				", date=" + date +
//				", duration=" + duration +
//				", startTime=" + startTime +
//				", location='" + location + '\'' +
//				", appointments=" + appointments +
//				'}';
//	}
}
