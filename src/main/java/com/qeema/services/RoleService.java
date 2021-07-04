package com.qeema.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qeema.model.ERole;
import com.qeema.model.Role;
import com.qeema.respositories.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	RoleRepository roleRepository;
	
	public Role findByName(String role)
	{
		return roleRepository.findByName(ERole.valueOf(role)) ;
	}
	

}
