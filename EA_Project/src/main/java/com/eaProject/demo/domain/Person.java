package com.eaProject.demo.domain;

import lombok.Data;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



@Entity
@Data
public class Person {

	@Id @GeneratedValue
	private Long id;
	@Column
	@Email(regexp = ".+@.+\\..+",message="should be in the email format")
	private String email;
	@NotEmpty
	@NotNull
	@Size(max=30)
	private String firstName;
	@NotEmpty
	@NotNull
	@Size(max=30)
	private String lastName;
	@NotEmpty
	@NotNull
	@Size(min =6,max=30)
	private String username;
	@NotEmpty
	@NotNull
	@Size(min =6,max=30)
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
