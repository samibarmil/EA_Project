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

import org.aspectj.apache.bcel.classfile.Module.Provide;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class Session {
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn
	private Person provider;
	
	@Temporal(TemporalType.DATE)
	private Date date;
	private Double duration;
	@Temporal(TemporalType.TIME)
	private Date startTime;
	private String location;
	
	@OneToMany(mappedBy = "session")
	private List<Appointment> appointments;
	

}
