package com.eaProject.demo.controller;

import com.eaProject.demo.domain.*;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import com.eaProject.demo.repository.PersonRepository;
import com.eaProject.demo.services.PersonDetailService;
import com.eaProject.demo.services.PersonService;
import com.eaProject.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PersonDetailService personDetailService;
    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    PersonService personService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try{
        	// use username and password to authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        // if auth is successful
        // get User object with username, password, role
        UserDetails userDetails = personDetailService.loadUserByUsername(authenticationRequest.getUsername());
        // generate jwt token using user object
        String jwt =  jwtUtil.generateUserToke(userDetails);
        // respond with jwt token
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Person person) throws UnprocessableEntityException {
        PersonRole[] personRoles = new PersonRole[] {new PersonRole(Role.CLIENT)};
        person.setPersonRole(Arrays.asList(personRoles));
        return ResponseEntity.ok(personService.addPerson(person));
    }
}
