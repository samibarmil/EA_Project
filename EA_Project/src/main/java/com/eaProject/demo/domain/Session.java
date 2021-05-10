package com.eaProject.demo.domain;

import java.time.LocalDate;
import java.time.LocalTime;
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.*;
import org.aspectj.apache.bcel.classfile.Module.Provide;

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
	private List<Appointment> appointments;

	@Override
	public String toString() {
		return "Session{" +
				"id=" + id +
				", provider=" + provider.getFirstName() + " " + provider.getLastName() +
				", date=" + date +
				", duration=" + duration +
				", startTime=" + startTime +
				", location='" + location + '\'' +
				", appointments=" + appointments +
				'}';
	}
}
