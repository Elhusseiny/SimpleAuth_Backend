package com.qeema.controllers;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qeema.dto.JWTResponseDTO;
import com.qeema.dto.LoginHistoryDTO;
import com.qeema.dto.LoginRequestDTO;
import com.qeema.dto.Response;
import com.qeema.dto.SignUpRequestDTO;
import com.qeema.model.ERole;
import com.qeema.model.LoginHistory;
import com.qeema.model.Role;
import com.qeema.model.User;
import com.qeema.respositories.AdminService;
import com.qeema.security.JwtUtils;
import com.qeema.services.RoleService;
import com.qeema.services.UserDetailsImpl;
import com.qeema.services.UserService;

@CrossOrigin
@RestController
public class MainController {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;
	
	@Autowired
	AdminService adminService ;

	@Autowired
	AuthenticationManager authenticationManager;

	@PostMapping("/api/auth/register")
	public ResponseEntity register(@RequestBody SignUpRequestDTO signupRequest) {
		ResponseEntity response;
		System.out.println(signupRequest);
		if (userService.existsByUsername(signupRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new Response("400", "Error: Username is already taken!"));
		}

		if (userService.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new Response("400", "Error: Email is already in use!"));
		}

		User user = new User(signupRequest.getName(), signupRequest.getUsername(), signupRequest.getEmail(),
				encoder.encode(signupRequest.getPassword()));

		Set<String> strRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		Role a = roleService.findByName("ROLE_USER");
		strRoles.forEach(role -> {
			roles.add(roleService.findByName(role));
		});
		user.setRoles(roles);
		userService.save(user);
		return response = ResponseEntity.ok(new Response("200", "Success Register User"));
	}

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/api/auth/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		User user = userService.getUser(loginRequest.getUsername());
		userService.saveLoginHistory(user);
		userService.addLoggedInUser(user) ;
		return ResponseEntity.ok(new JWTResponseDTO(token, userDetails.getId(), userDetails.getUsername(),
				userDetails.getEmail(), roles));
	}

	@GetMapping("/api/auth/history")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> getLoginHistoryByUser(@RequestParam(value = "username", required = true) String username) {
		LoginHistoryDTO loginHistoryDTO = userService.getUserLoginHistory(username);
		if (loginHistoryDTO != null)
			return ResponseEntity.ok(loginHistoryDTO);
		else
			return ResponseEntity.badRequest().body(new Response("400", "no history found"));
	}

	@GetMapping("/api/auth/logged")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getLoggedInUsers() {
		
		return ResponseEntity.ok().body(adminService.getCurrentLoggedInUsers());
	}
	
	
	@GetMapping("/api/auth/registered")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getRegisteredUsers() {
		
		return ResponseEntity.ok().body(adminService.getRegisteredUsers());
	}
	
	@GetMapping("/api/auth/logout")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> signoutUser(@RequestParam(value = "username", required = true) String username) {
		if (userService.logoutUserByUserName(username) )
		return ResponseEntity.ok().body(new Response("200", "logout successfull"));
		else
			return ResponseEntity.badRequest().body(new Response("400", "couldn't delete"));
	}
	

}