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
import org.springframework.web.bind.annotation.RequestMapping;
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

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class MainController {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	AdminService adminService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody SignUpRequestDTO signupRequest) {

		try {
			if (userService.existsByUsername(signupRequest.getUsername())) {
				return ResponseEntity.badRequest().body(new Response("400", "Error: Username is already taken!"));
			}

			if (userService.existsByEmail(signupRequest.getEmail())) {
				return ResponseEntity.badRequest().body(new Response("400", "Error: Email is already in use!"));
			}

			userService.registerUser(signupRequest);
			log.info("user " + signupRequest.getUsername() + " registered successfully");
			return ResponseEntity.ok(new Response("200", "Success Register User"));

		} catch (Exception e) {
			log.error("exception in registering user " + signupRequest.getUsername(), e);
			return ResponseEntity.badRequest().body(new Response("400", "Registering failed"));
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());
			User user = userService.getUser(loginRequest.getUsername());
			userService.saveLoginHistory(user);
			userService.addLoggedInUser(user);
			return ResponseEntity.ok(new JWTResponseDTO(token, userDetails.getId(), userDetails.getUsername(),
					userDetails.getEmail(), roles));
		} catch (Exception e) {
			log.error("singing in failed for user" + loginRequest.getUsername());
			return ResponseEntity.badRequest().body(new Response("400", "signing in failed"));
		}
	}

	@GetMapping("/history")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> getLoginHistoryByUser(@RequestParam(value = "username", required = true) String username) {
		try {
			LoginHistoryDTO loginHistoryDTO = userService.getUserLoginHistory(username);
			if (loginHistoryDTO != null)
				return ResponseEntity.ok(loginHistoryDTO);
			else
				return ResponseEntity.badRequest().body(new Response("400", "no history found"));
		} catch (Exception e) {
			log.error("error in fetching history", e);
			return ResponseEntity.badRequest().body(new Response("400", "fetching failed"));
		}
	}

	@GetMapping("/logged")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getLoggedInUsers() {
		try {
			return ResponseEntity.ok().body(adminService.getCurrentLoggedInUsers());
		} catch (Exception e) {
			log.error("logged request failed", e);
			return ResponseEntity.badRequest().body(new Response("400", "request failed"));
		}
	}

	@GetMapping("/registered")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getRegisteredUsers() {
		try {
			return ResponseEntity.ok().body(adminService.getRegisteredUsers());
		} catch (Exception e) {
			log.error("registered request failed", e);
			return ResponseEntity.badRequest().body(new Response("400", "request failed"));
		}
	}

	@GetMapping("/logout")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> signoutUser(@RequestParam(value = "username", required = true) String username) {
		try {
			if (userService.logoutUserByUserName(username))
				return ResponseEntity.ok().body(new Response("200", "logout successfull"));
			else
				return ResponseEntity.badRequest().body(new Response("400", "couldn't delete"));
		} catch (Exception e) {
			log.error("request failed", e);
			return ResponseEntity.badRequest().body(new Response("400", "request failed"));
		}

	}

}