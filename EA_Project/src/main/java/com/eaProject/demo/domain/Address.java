package com.eaProject.demo.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @NoArgsConstructor
public class Address {
	@Column
	@NotNull
	@NotEmpty(message = "Required to fill this field")
	@Size(min = 2 , max = 20)
	private String city;
	@Column
	@NotEmpty(message = "Required to fill this field")
	@NotNull
	@Size(min = 2 , max = 15)
	private String state;
	@Column
	@NotEmpty(message = "Required to fill this field")
	@Size(min = 2 , max = 15)
	private String country;
	@Column
	@NotNull
	@NotEmpty(message = "Required to fill this field")
	@Size(min = 2 , max = 30)
	private String street;
	@Column
	@NotNull
	@NotEmpty(message = "Required to fill this field")
	@Size(min = 5 , max = 5)
	private String zip;

	public Address(String country,String city, String state, String street, String zip) {
		this.city = city;
		this.state = state;
		this.country = country;
		this.street = street;
		this.zip = zip;
	}
}
