package com.eaProject.demo.services;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.domain.Session;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import com.eaProject.demo.repository.PersonRepository;

import java.util.Collection;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    PersonDetailService personDetailService;

    public Person addPerson(@Valid Person person) throws UnprocessableEntityException {
        if (!isEmailUnique(person))
            throw new UnprocessableEntityException("Email must be unique.");

        if (!isUsernameUnique(person))
            throw new UnprocessableEntityException("Username must be unique.");

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        this.personRepository.save(person);
        return person;
    }

    public Person getPersonByUsername(String userName) {
        return  personRepository
                .findTopByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
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
    
    public Person getPersonById(Long id){
    	return personRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Person with id : %d not found.", id)));
    }
    
    public Collection<Person> getAllPersons(){
    	return personRepository.findAll();
    }
    
    public Person updatePerson(Long id, @Valid Person person) throws UnprocessableEntityException {

		if(!id.equals(person.getId()))
		    throw new UnprocessableEntityException("Person id dose not match.");

		personRepository.save(person);
		return person;
    }

    public Person getCurrentPersonByUsername() {
        return getPersonByUsername(personDetailService
                        .getCurrentUser()
                        .getUsername()
                );
    }

}
