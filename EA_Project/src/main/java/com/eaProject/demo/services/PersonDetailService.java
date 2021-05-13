package com.eaProject.demo.services;

import com.eaProject.demo.domain.Person;
import com.eaProject.demo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonDetailService implements UserDetailsService {

    @Autowired
    PersonRepository personRepository;

    // give username and return User object
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findTopByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); // TODO: return unauthorized exception

        List<SimpleGrantedAuthority> roles = person.getPersonRole()
                .stream()
                .map(personRole ->
                        new SimpleGrantedAuthority(personRole.getRole().name()))
                .collect(Collectors.toList());
        return new User(person.getUsername(), person.getPassword(), roles);
    }

    public UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
