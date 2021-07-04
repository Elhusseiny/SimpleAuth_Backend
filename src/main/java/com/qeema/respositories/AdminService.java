package com.qeema.respositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qeema.model.LoggedInUsers;
import com.qeema.model.User;

@Service
public class AdminService {
	
	@Autowired
	LoggedInUsersRepository loggedInRepository ;
	
	@Autowired
	UserRepository userRepository ;
	
	public List<String> getCurrentLoggedInUsers()
	{
		List<String> usernames = new ArrayList<String>();
		List<LoggedInUsers> loggedInUsersList = loggedInRepository.findAll();
		if (loggedInUsersList!= null && loggedInUsersList.size() != 0 )
		{
			for ( LoggedInUsers loggedInUser : loggedInUsersList )
			{
					usernames.add(loggedInUser.getUser().getUsername());
			}
		}
		
		return usernames; 
	}
	
	public List<String> getRegisteredUsers()
	{
		List<String> usernames = new ArrayList<String>();
		List<User> usersList = userRepository.findAll();
		if (usersList != null && usersList.size() != 0)
		for (User user : usersList )
		{
			usernames.add(user.getUsername()); 
		}
		return usernames ;
	}

}
