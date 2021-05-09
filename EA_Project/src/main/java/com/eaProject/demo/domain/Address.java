package com.eaProject.demo.domain;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class Address {
	
	private String city;
	private String state;
	private String country;
	private String street;
	private String zip;

}
