package com.eaProject.demo.domain;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @NoArgsConstructor
public class Address {
	
	private String city;
	private String state;
	private String country;
	private String street;
	private String zip;

	public Address(String country,String city, String state, String street, String zip) {
		this.city = city;
		this.state = state;
		this.country = country;
		this.street = street;
		this.zip = zip;
	}
}
