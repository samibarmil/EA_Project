package com.eaProject.demo.services;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public Person addPerson(Person person) throws Exception {
        if (!isEmailUnique(person))
            throw new Exception("Email must be unique.");

        if (!isUsernameUnique(person))
            throw new Exception("Username must be unique.");

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        this.personRepository.save(person);
        return person;
    }

    public Person getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return  personRepository
                .findTopByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Boolean isEmailUnique(Person person) {
        Person personByEmail = personRepository.findTopByEmail(person.getEmail())
                .orElse(null);
        return personByEmail == null;
    }

    public Boolean isUsernameUnique(Person person) {
        Person personByUsername = personRepository.findTopByUsername(person.getUsername())
                .orElse(null);
        return personByUsername == null;
    }
}
