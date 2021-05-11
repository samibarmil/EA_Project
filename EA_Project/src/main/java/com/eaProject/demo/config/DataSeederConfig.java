package com.eaProject.demo.config;

import com.eaProject.demo.domain.Address;
import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.PersonRole;
import com.eaProject.demo.domain.Role;
import com.eaProject.demo.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;

@Configuration
public class DataSeederConfig {

    @Autowired
    private PersonService personService;

    @EventListener
    public void seed (ContextRefreshedEvent event)  {
        Person ADMIN = new Person("admin@gmail.com", "Admin", "Admin", "admin", "adminpass");
        Address address = new Address("US", "Iowa", "Fairfield", "1000 Nth 8th St.", "52352");
        ADMIN.setAddress(address);
        PersonRole[] personRoles = new PersonRole[] {new PersonRole(Role.ADMIN)};
        ADMIN.setPersonRole(Arrays.asList(personRoles));
        try {
            personService.addPerson(ADMIN);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
