package com.qeema.respositories;

import java.util.List;
import java.util.Optional;

import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qeema.model.LoggedInUsers;
import com.qeema.model.LoginHistory;
import com.qeema.model.User;


@Transactional
@Repository
public interface LoggedInUsersRepository extends JpaRepository<LoggedInUsers, Long> {

	
	public List<LoggedInUsers> findAll() ;
	@Modifying
    @Transactional
	@Query(value = "delete from LoggedInUsers l where l.user = :user")
	void deletebyUser(@Param("user") User user) ;
	 ;
	// public Long deletebyUser(User user) ; not working
	
	
	
}
