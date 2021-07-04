package com.qeema.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginHistoryDTO {
	
	String username;
	List<Timestamp> time = new ArrayList<Timestamp>() ;

}
