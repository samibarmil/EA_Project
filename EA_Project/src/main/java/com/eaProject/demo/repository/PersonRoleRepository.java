package com.eaProject.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaProject.demo.domain.PersonRole;

@Repository
public interface PersonRoleRepository  extends JpaRepository<PersonRole, Long> {

}
