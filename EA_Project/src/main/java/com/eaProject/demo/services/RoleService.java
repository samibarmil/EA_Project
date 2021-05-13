package com.eaProject.demo.services;

import com.eaProject.demo.domain.Role;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;

@Service
public class RoleService {

    public Role getRoleByName(String roleName) {
        Role[] roles = new Role[] {Role.ADMIN, Role.CLIENT, Role.PROVIDER};
        System.out.println(roles[0].name());
        return Arrays.stream(roles)
                .filter(r -> r.name().equals(roleName))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Role with name %s not found", roleName))
                );

    }
}
