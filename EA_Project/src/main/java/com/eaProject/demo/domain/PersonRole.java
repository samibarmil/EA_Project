package com.eaProject.demo.domain;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor
public class PersonRole {
	
	@Id @GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	private Role role;

	public PersonRole(Role role) {
		this.role = role;
	}
	
}
