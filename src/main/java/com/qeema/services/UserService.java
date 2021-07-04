package com.qeema.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qeema.dto.LoginHistoryDTO;
import com.qeema.model.LoggedInUsers;
import com.qeema.model.LoginHistory;
import com.qeema.model.User;
import com.qeema.respositories.LoggedInUsersRepository;
import com.qeema.respositories.LoginHistoryRepository;
import com.qeema.respositories.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired LoginHistoryRepository loginHistoryRepository ;
	
	@Autowired LoggedInUsersRepository  loggedInUsersRepository ; 

	public Boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public User save(User user)
	{
		return userRepository.save(user);
	}
	
	public User getUser(String username)
	{
		return userRepository.findOneByUsername(username);
	}
	
	public LoggedInUsers addLoggedInUser (User user)
	{
		LoggedInUsers loggedInUser = new LoggedInUsers();
		loggedInUser.setTime(new Timestamp(System.currentTimeMillis()));
		loggedInUser.setUser(user);
		return loggedInUsersRepository.save(loggedInUser); 
	}
	
	
	public LoginHistory saveLoginHistory (User user)
	{
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setTime(new Timestamp(System.currentTimeMillis()));
		loginHistory.setUser(user);
		return loginHistoryRepository.save(loginHistory);
	}
	
	public LoginHistoryDTO getUserLoginHistory (String username)
	{
		User user = userRepository.findOneByUsername(username);
		if (user != null)
		{
			LoginHistoryDTO historydto = new LoginHistoryDTO();
			historydto.setUsername(user.getUsername());
			List<LoginHistory> historyList = loginHistoryRepository.findByUser(user);
			for(LoginHistory history : historyList)
			{
				historydto.getTime().add(history.getTime()); 
			}
			return historydto ;
		}
		
		return null;
		
		
	}
	
	public Boolean logoutUserByUserName(String username)
	{
		Boolean isDeleted = false; 
		User user = userRepository.findOneByUsername(username);
		if (username != null)
			isDeleted =  ( loggedInUsersRepository.deletebyUserId(user.getId()) != 0 );
		return isDeleted ;
		
	}

}
