package com.eaProject.demo.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

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
	//@Future --> to discuss
	private Date date;
	private Double duration;
	@Temporal(TemporalType.TIME)
	private Date startTime;
	@Lob
	@NotNull(message = "this field Requires")
	private String location;
	
	@OneToMany(mappedBy = "session")
	@JsonIgnore
	private List<Appointment> appointments;

	@Override
	public String toString() {
		return "Session{" +
				"id=" + id +
				", provider=" + provider.getLastName() + " " + provider.getFirstName() +
				", date=" + date +
				", duration=" + duration +
				", startTime=" + startTime +
				", location='" + location + '\'' +
				'}';
	}
}
