package com.qeema.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SignUpRequestDTO implements Serializable {

	private static final long serialVersionUID = -3717873746249151313L;

	private String name;
	private String username;
	private String email ;
	private String password;
	private Set<String> roles;

}
