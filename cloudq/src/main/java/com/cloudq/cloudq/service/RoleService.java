package com.cloudq.cloudq.service;

import com.cloudq.cloudq.exeption.RoleNotFoundException;
import com.cloudq.cloudq.model.Role;
import com.cloudq.cloudq.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Create a new role.
     *
     * @param role the role to be created
     * @return the created role
     */
    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role already exists.");
        }
        return roleRepository.save(role);
    }

    /**
     * Get all roles.
     *
     * @return a list of roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Get a role by its ID.
     *
     * @param id the ID of the role
     * @return the found role
     * @throws RoleNotFoundException if the role is not found
     */
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id " + id));
    }

    /**
     * Update an existing role.
     *
     * @param id the ID of the role to update
     * @param updatedRole the updated role data
     * @return the updated role
     * @throws RoleNotFoundException if the role is not found
     */
    @Transactional
    public Role updateRole(Long id, Role updatedRole) {
        Role role = getRoleById(id);

        // Update role details
        role.setName(updatedRole.getName());

        return roleRepository.save(role);
    }

    /**
     * Delete a role by its ID.
     *
     * @param id the ID of the role to delete
     * @throws RoleNotFoundException if the role is not found
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }
}