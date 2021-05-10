package com.eaProject.demo.services;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        this.personRepository.save(person);
    }
}
