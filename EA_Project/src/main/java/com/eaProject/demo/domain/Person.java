package com.eaProject.demo.domain;

import lombok.Data;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
@Data
public class Person {

	@Id @GeneratedValue
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "provider")
	private List<Session> sessions;  
	
	@Embedded
	private Address address;
	
	@OneToMany
	@JoinColumn(name="person_id")
	private List<PersonRole> personRole;
	
	
	
	public Person() {}

}
