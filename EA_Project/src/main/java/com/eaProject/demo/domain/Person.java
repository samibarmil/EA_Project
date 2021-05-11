package com.eaProject.demo.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

import lombok.Data;


import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Data @NoArgsConstructor
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
	@JsonIgnore
	@NotEmpty
	@NotNull
	@Size(min =6,max=30)
	private String password;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "provider")
	private List<Session> sessions;  
	
	@Embedded
	private Address address;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="person_id")
	private List<PersonRole> personRole;

	public Person(String email, String firstName, String lastName, String username, String password) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
	}
}
