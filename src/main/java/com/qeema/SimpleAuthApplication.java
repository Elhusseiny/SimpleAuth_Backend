package com.qeema;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.qeema.model.User;
import com.qeema.respositories.UserRepository;

@SpringBootApplication
public class SimpleAuthApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@SuppressWarnings("unused")
	public void run(String... arg0) throws Exception {
		List<User> users = userRepo.findAll();
		System.out.println(users);
	}

	@Autowired
	UserRepository userRepo;

	public static void main(String[] args) {
		SpringApplication.run(SimpleAuthApplication.class, args);

	}

}
