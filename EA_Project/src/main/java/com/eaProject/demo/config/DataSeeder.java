package com.eaProject.demo.config;

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
public class DataSeeder {

    @Autowired
    private PersonService personService;

    @EventListener
    public void seed (ContextRefreshedEvent event) {
        Person ADMIN = new Person("admin@gmail.com", "Admin", "Admin", "admin", "adminpass");
        PersonRole[] personRoles = new PersonRole[] {new PersonRole(Role.ADMIN)};
        ADMIN.setPersonRole(Arrays.asList(personRoles));
        personService.addPerson(ADMIN);
    }
}
