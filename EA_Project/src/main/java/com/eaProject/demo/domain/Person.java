package com.eaProject.demo.domain;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ManyToAny;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Data;


import java.util.List;

import javax.persistence.*;

@Entity
@Data @NoArgsConstructor
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
